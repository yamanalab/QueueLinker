/* Copyright 2013 Yamana Laboratory, Waseda University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.queuelinker.system.sched;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.client.LogicalVertex;
import jp.queuelinker.client.SwitcherVertex;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.client.VirtualVertex;
import jp.queuelinker.module.base.FlowSwitcherModule;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.MultipleInputQueuesPushModule;
import jp.queuelinker.module.base.MultipleOutputPushModule;
import jp.queuelinker.module.base.PullInputModule;
import jp.queuelinker.module.base.PullModule;
import jp.queuelinker.module.base.PushInputModule;
import jp.queuelinker.module.base.PushModule;
import jp.queuelinker.module.base.SinkModule;
import jp.queuelinker.module.base.SourceModule;
import jp.queuelinker.system.graphs.LocalEdge;
import jp.queuelinker.system.graphs.LocalPhysicalEdge;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalVertex;
import jp.queuelinker.system.graphs.LocalSelectorVertex;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.graphs.PartitionOrderComparetor;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.thread.MultipleInputQueuesPushModuleContext;
import jp.queuelinker.system.thread.MultipleOutputPushModuleContext;
import jp.queuelinker.system.thread.ObjectInputQueueImpl;
import jp.queuelinker.system.thread.OutputContext;
import jp.queuelinker.system.thread.PullModuleContext;
import jp.queuelinker.system.thread.PullModuleThreadUnit;
import jp.queuelinker.system.thread.PushModuleContext;
import jp.queuelinker.system.thread.PushModulesThreadUnit;
import jp.queuelinker.system.thread.SelectorContext;
import jp.queuelinker.system.thread.SinkModuleContext;
import jp.queuelinker.system.thread.SourceModuleContext;
import jp.queuelinker.system.thread.SwitcherModuleContext;
import jp.queuelinker.system.thread.ThreadContextBase;
import jp.queuelinker.system.thread.ThreadUnit;
import jp.queuelinker.system.thread.ThreadUnitController;
import jp.queuelinker.system.thread.ThreadUnitSet;
import jp.queuelinker.system.thread.router.DispatchAccepter;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.util.ReflectionUtil;

public class VertexDeploymentManager {

    private final ThreadUnitController unitController = ThreadUnitController.getInstance();

    private final HashMap<JobDescriptor, ThreadUnitSet> threadUnits = new HashMap<>();

    public void deploy(final JobDescriptor job, final LocalPhysicalGraph pvGraph) throws IOException {
        threadUnits.put(job, new ThreadUnitSet());
        deployVertices(job, pvGraph);
        deployVirtualAndSwitcherVertices(job, pvGraph);
        deployEdges(pvGraph);
    }

    private void deployVertices(final JobDescriptor job, final LocalPhysicalGraph pvGraph) throws IOException {
        Iterator<LocalVertex> iter = pvGraph.iteratorOfVertex();
        while (iter.hasNext()) {
            final LocalVertex pv = iter.next();
            if (pv instanceof LocalPhysicalVertex) {
                deployLogicalVertex(job, (LocalPhysicalVertex) pv);
            } else if (pv instanceof LocalSelectorVertex) {
                deploySelectorVertex(job, (LocalSelectorVertex) pv);
            } else {
                throw new RuntimeException("BUG: Illegal Vertex Type");
            }
        }
    }

    private void deployLogicalVertex(final JobDescriptor job, final LocalPhysicalVertex pv) {
        final Vertex v = pv.getVertex();

        if (v instanceof LogicalVertex) {
            final ModuleBase instance = pv.getModuleInstance();

            ThreadUnit unit;
            ThreadContextBase context;
            if (ReflectionUtil.isImplemented(v.getModuleClass(), PushInputModule.class)) {
                unit = unitController.createPushModulesUnit(v.getName(), pv.getCpuAffinityIds(), pv.isBusyLoopMode());
                context = handlePushModules((PushModulesThreadUnit) unit, pv, instance);
            } else if (ReflectionUtil.isImplemented(v.getModuleClass(), PullInputModule.class)) {
                unit = unitController.createPullModuleUnit(v.getName(), pv.getCpuAffinityIds(),
                                                           (PullModule<?, ?>) instance);
                context = handlePullModule(pv, (PullModuleThreadUnit) unit, (PullModule<?, ?>) instance);
            } else if (ReflectionUtil.isExtended(v.getModuleClass(), SourceModule.class)) {
                unit = unitController.createDataSourceModuleUnit(v.getName(), pv.getCpuAffinityIds(),
                                                                 (SourceModule<?>) instance);
                context = handleSourceModule(pv, (PullModuleThreadUnit) unit, (SourceModule<?>) instance);
            } else
                throw new RuntimeException("BUG: Illegal Logical Vertex");

            threadUnits.get(job).addNewThreadUnit(unit);
            pv.setContext(context);
            pv.setThreadUnit(unit);
        }
    }

    private void deploySelectorVertex(final JobDescriptor job, final LocalSelectorVertex pv) throws IOException {
        final PullModuleThreadUnit unit = unitController.createSelectorUnit("SelectorThread", pv.getCpuAffinityIds());
        final SelectorContext<?, ?> context = (SelectorContext<?, ?>) unit.getThreadContext();

        for (int i = 0; i < pv.getSelectorInputCount(); i++) {
            int inputThreadLocalId = unitController.createThreadLocalInputQueue(unit);
            unitController.addNewInputQueue(context, "SelectorInput" + i, i, inputThreadLocalId);
            unitController.addThreadInputRoute(unit, inputThreadLocalId, context, i);
            pv.setInputQueueThreadLocalId(i, inputThreadLocalId);
        }
        for (int i = 0; i < pv.getSelectorOutputCout(); i++) {
            int newThreadLocalId = unitController.createThreadLocalOutputQueue(unit);
            unitController.addNewOutputQueue(context, "SelectorOutput" + i, i, newThreadLocalId);
            pv.setOutputQueueThreadLocalId(i, newThreadLocalId);

            int port = context.startAccepting(i, pv.getBindAddress());
            pv.setPort(i, port);
        }

        threadUnits.get(job).addNewThreadUnit(unit);
        pv.setContext(context);
        pv.setThreadUnit(unit);

        // // We have to start the unit to accept socket connections from remote workers.
        // unitController.startThreadUnit(unit);
    }

    private void deployVirtualAndSwitcherVertices(final JobDescriptor job, final LocalPhysicalGraph pvGraph) {
        Iterator<LocalVertex> iter = pvGraph.iteratorOfVertex();
        while (iter.hasNext()) {
            final LocalVertex lv = iter.next();

            if (lv instanceof LocalSelectorVertex)
                continue;

            final LocalPhysicalVertex pv = (LocalPhysicalVertex) lv;
            final ModuleBase instance = pv.getModuleInstance();
            final Vertex v = pv.getVertex();

            if (v instanceof VirtualVertex) {
                assert (!ReflectionUtil.isImplemented(v.getModuleClass(), PullInputModule.class));
                ThreadUnit unit = pv.getActualVertex().getThreadUnit();
                ThreadContextBase context = handlePushModules((PushModulesThreadUnit) unit, pv, instance);
                pv.setContext(context);
                pv.setThreadUnit(unit);
                continue;
            } else if (v instanceof SwitcherVertex) {
                PushModulesThreadUnit unit = unitController.createPushModulesUnit("SwitcherVertex",
                                                                                  pv.getCpuAffinityIds(),
                                                                                  pv.isBusyLoopMode());
                ThreadContextBase context = handlePushModules(unit, pv, instance);
                threadUnits.get(job).addNewThreadUnit(unit);
                pv.setContext(context);
                pv.setThreadUnit(unit);
                continue;
            }
            assert (v instanceof LogicalVertex);
        }
    }

    private void deployEdges(final LocalPhysicalGraph pvGraph) {
        Iterator<LocalVertex> iter = pvGraph.iteratorOfVertex();
        while (iter.hasNext()) {
            final LocalVertex pv = iter.next();

            if (pv instanceof LocalPhysicalVertex) {
                final LocalPhysicalVertex lpv = (LocalPhysicalVertex) pv;
                // System.err.println(lpv.isVirtual() + " " + lpv.getVertex().getName());

                for (int queueId = 0; queueId < lpv.getOutputQueueCount(); queueId++) {
                    for (int branchId = 0; branchId < lpv.getOutputBranchCount(queueId); branchId++) {
                        List<LocalEdge> edges = lpv.getOutputEdges(queueId, branchId);
                        if (!edges.isEmpty()) {
                            final HashCoder coder = edges.get(0).getDestHashCoder();
                            if (coder != null)
                                addHashDispatcher(pv, edges, coder);
                            else
                                addHashDispatcher(pv, edges, new DefaultHashCoder());
                        }
                    }
                }
            } else if (pv instanceof LocalSelectorVertex) {
                final LocalSelectorVertex sv = (LocalSelectorVertex) pv;

                for (int queueId = 0; queueId < sv.getOutputQueueCount(); queueId++) {
                    List<LocalEdge> edges = sv.getOutputEdges(queueId);
                    if (!edges.isEmpty()) {
                        final HashCoder coder = edges.get(0).getDestHashCoder();
                        if (coder != null)
                            addHashDispatcher(pv, edges, coder);
                        else
                            addHashDispatcher(pv, edges, new DefaultHashCoder());
                    }
                }
            }
        }
    }

    private ThreadContextBase handlePushModules(final PushModulesThreadUnit unit, final LocalPhysicalVertex pv,
                                                final ModuleBase instance) {
        if (instance instanceof MultipleOutputPushModule)
            return handleMultipleOutputPushModule(pv, unit, (MultipleOutputPushModule<?, ?>) instance);
        else if (instance instanceof PushModule)
            return handlePushModule(pv, unit, (PushModule<?, ?>) instance);
        else if (instance instanceof SinkModule)
            return handleSinkModule(pv, unit, (SinkModule<?>) instance);
        else if (instance instanceof MultipleInputQueuesPushModule)
            return handleMutipleInputQueuesPushModule(pv, unit, (MultipleInputQueuesPushModule<?, ?>) instance);
        else if (instance instanceof FlowSwitcherModule)
            return handleSwitcherModule(pv, unit, (FlowSwitcherModule<?>) instance);
        else {
            throw new RuntimeException("Unknown Vertex");
        }
    }

    private ThreadContextBase handleSwitcherModule(final LocalPhysicalVertex pv, final PushModulesThreadUnit unit,
                                                   final FlowSwitcherModule<?> instance) {
        final SwitcherModuleContext<?> context = unitController.addSwitcherContext(unit, instance);

        createQueues(unit, pv);
        unitController.addThreadInputRoute(unit, pv.getInputQueueThreadLocalId(0), context, 0);
        for (int i = 0; i < pv.getOutputQueueCount(); i++)
            unitController.addThreadOutputRoute(unit, context, i, pv.getOutputQueueThreadLocalId(i));

        return context;
    }

    private ThreadContextBase handlePushModule(final LocalPhysicalVertex pv, final PushModulesThreadUnit unit,
                                               final PushModule<?, ?> instance) {
        final PushModuleContext<?, ?> context = unitController.addPushThreadContext(unit, instance);

        createQueues(unit, pv);
        unitController.addThreadInputRoute(unit, pv.getInputQueueThreadLocalId(0), context, 0);
        unitController.addThreadOutputRoute(unit, context, 0, pv.getOutputQueueThreadLocalId(0));

        return context;
    }

    private ThreadContextBase handlePullModule(final LocalPhysicalVertex pv, final PullModuleThreadUnit unit,
                                               final PullModule<?, ?> instance) {
        final PullModuleContext<?, ?> context = (PullModuleContext<?, ?>) unit.getThreadContext();
        final Vertex v = pv.getVertex();

        createQueues(unit, pv);

        for (int i = 0; i < v.getInputQueueCount(); i++) {
            unitController.addNewInputQueue((PullModuleContext<?, ?>) unit.getThreadContext(),
                                            v.getInputQueueHandle(i).getQueueName(),
                                            i, pv.getInputQueueThreadLocalId(i),
                                            new ObjectInputQueueImpl(i, v.getInputQueueHandle(i).getQueueName()));
            unitController.addThreadInputRoute(unit, pv.getInputQueueThreadLocalId(i), context, i);
        }

        for (int i = 0; i < v.getOutputQueueCount(); i++) {
            unitController.allowUsingNewOutputQueue(context, v.getOutputQueueHandle(i).getQueueName(), i,
                                                    pv.getOutputQueueThreadLocalId(i));
        }

        return context;
    }

    private ThreadContextBase handleMutipleInputQueuesPushModule(final LocalPhysicalVertex pv,
                                                                 final PushModulesThreadUnit unit,
                                                                 final MultipleInputQueuesPushModule<?, ?> instance) {
        final MultipleInputQueuesPushModuleContext<?, ?> context = unitController
                .addMultipleInputQueuesPushModuleContext(unit, instance);
        final Vertex v = pv.getVertex();
        createQueues(unit, pv);

        for (int i = 0; i < v.getInputQueueCount(); i++) {
            unitController.addNewInputQueue(context, i);

            unitController.addThreadInputRoute(unit, pv.getInputQueueThreadLocalId(i), context, i);
        }

        unitController.addThreadOutputRoute(unit, context, 0, pv.getOutputQueueThreadLocalId(0));

        return context;
    }

    private SourceModuleContext<?> handleSourceModule(final LocalPhysicalVertex pv, final PullModuleThreadUnit unit,
                                                      final SourceModule<?> instance) {
        createQueues(unit, pv);
        final SourceModuleContext<?> context = (SourceModuleContext<?>) unit.getThreadContext();

        final Vertex v = pv.getVertex();

        for (int i = 0; i < v.getOutputQueueCount(); i++) {
            unitController.allowUsingNewOutputQueue(context, v.getOutputQueueHandle(i).getQueueName(), i,
                                                    pv.getOutputQueueThreadLocalId(i));

            unitController.addThreadOutputRoute(unit, (OutputContext<?>) unit.getThreadContext(), i,
                                                pv.getOutputQueueThreadLocalId(i));
        }

        return context;
    }

    private ThreadContextBase handleSinkModule(final LocalPhysicalVertex pv, final PushModulesThreadUnit unit,
                                               final SinkModule<?> instance) {
        final SinkModuleContext<?> context = unitController.addSinkThreadContext(unit, instance);

        createQueues(unit, pv);
        unitController.addThreadInputRoute(unit, pv.getInputQueueThreadLocalId(0), context, 0);
        return context;
    }

    private ThreadContextBase handleMultipleOutputPushModule(final LocalPhysicalVertex pv,
                                                             final PushModulesThreadUnit unit,
                                                             final MultipleOutputPushModule<?, ?> instance) {
        final MultipleOutputPushModuleContext<?, ?> context = unitController
                .addMultipleOutputPushThreadContext(unit, instance);

        createQueues(unit, pv);

        unitController.addThreadInputRoute(unit, pv.getInputQueueThreadLocalId(0), context, 0);
        unitController.addThreadOutputRoute(unit, context, 0, pv.getOutputQueueThreadLocalId(0));
        return context;
    }

    private DispatchRouteInformation addSingleDispatcher(final LocalPhysicalEdge edge) {
        final LocalVertex srcPV = edge.getSrcVertex();
        final LocalVertex destPV = edge.getDestVertex();

        final DispatchAccepter accepter;

        if (srcPV.getThreadUnit() == destPV.getThreadUnit())
            accepter = (DispatchAccepter) destPV.getContext();
        else
            accepter = (DispatchAccepter) destPV.getThreadUnit();

        return unitController
                .addSingleDispatcher(srcPV.getThreadUnit(), edge.getSrcQueueThreadLocalId(), accepter,
                                     edge.getDestQueueThreadLocalId(), edge.getDestQueueLogicalId(), false);
    }

    public DispatchRouteInformation[] addHashDispatcher(final LocalVertex srcPV, final List<LocalEdge> edges,
                                                        final HashCoder coder) {
        Collections.sort(edges, new PartitionOrderComparetor());
        final ThreadUnit srcThreadUnit = srcPV.getThreadUnit();
        final int srcQueueThreadLocalId = edges.get(0).getSrcQueueThreadLocalId();

        DispatchAccepter[] accepters = new DispatchAccepter[edges.size()];
        int[] destQueueThreadLocalId = new int[edges.size()];
        int[] destQueueLogicalId = new int[edges.size()];
        double[] partitioningRate = new double[edges.size()];

        int index = 0;
        for (LocalEdge edge : edges) {
            final LocalVertex destPV = edge.getDestVertex();
            if (srcPV.getThreadUnit() == destPV.getThreadUnit()) {
                accepters[index] = (DispatchAccepter<?>) destPV.getContext();
            } else {
                accepters[index] = (DispatchAccepter<?>) destPV.getThreadUnit();
            }

            if (destPV instanceof LocalPhysicalVertex) {
                destQueueLogicalId[index] = edge.getDestQueueLogicalId();
                destQueueThreadLocalId[index] = edge.getDestQueueThreadLocalId();
            } else {
                destQueueLogicalId[index] = edge.getDestQueueId();
                destQueueThreadLocalId[index] = edge.getDestQueueThreadLocalId();
            }
            index++;
        }

        return unitController.addHashDispatcher(srcThreadUnit, srcQueueThreadLocalId, accepters,
                                                destQueueThreadLocalId, destQueueLogicalId, false, coder,
                                                partitioningRate);
    }

    private void createQueues(final ThreadUnit unit, final LocalPhysicalVertex pv) {
        for (int inputQueueId = 0; inputQueueId < pv.getVertex().getInputQueueCount(); inputQueueId++) {
            int newThreadLocalId = unitController.createThreadLocalInputQueue(unit);
            pv.setInputQueueThreadLocalId(inputQueueId, newThreadLocalId);
        }

        for (int outputQueueId = 0; outputQueueId < pv.getVertex().getOutputQueueCount(); outputQueueId++) {
            int newThreadLocalId = unitController.createThreadLocalOutputQueue(unit);
            pv.setOutputQueueThreadLocalId(outputQueueId, newThreadLocalId);
        }
    }

    public ThreadUnitSet getThreadUnits(final JobDescriptor job) {
        return threadUnits.get(job);
    }

    public void changeDeploy(final LocalPhysicalGraph graph, final LocalPhysicalGraph newGraph) {
        // TODO Auto-generated method stub

    }

    // private TransferContext<?, ?> createTransferContext(ContextVertex cv, PushModulesThreadUnit unit) {
    // final TransferContext<?, ?> context = unitController.addTransferContext(unit);
    // cv.setTransferContext(context);
    //
    // for (int i = 0; i < cv.getLogicalVertex().getInputQueueCount(); i++) {
    // final int outputId = unitController.createThreadLocalOutputQueue(unit);
    // unitController.addThreadOutputRoute(unit, context, i, outputId);
    // cv.getTransferContextVertex().addOutputQueueMapping(i, outputId);
    // }
    //
    // return context;
    // }

}
