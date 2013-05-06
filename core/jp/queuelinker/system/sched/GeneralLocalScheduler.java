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
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.client.LogicalVertex;
import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.client.SwitcherVertex;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.client.VirtualVertex;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.SnapShottable;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.graphs.GlobalPhysicalEdge;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.GlobalPhysicalVertex;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalVertex;
import jp.queuelinker.system.graphs.LocalSelectorVertex;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.server.SnapShotFileManager;
import jp.queuelinker.system.util.ReflectionUtil;

public final class GeneralLocalScheduler extends LocalScheduler {

    private final InstanceManager instanceManager = InstanceManager.getInstance();

    private final LocalResourceManager resourceManager = LocalResourceManager.getInstance();

    private final boolean busyLoopMode = false;

    private LocalPhysicalGraph lpg;

    public GeneralLocalScheduler(final QueueLinkerJob job, final WorkerSignature signature,
            final GlobalPhysicalGraph gpg) {
        super(job, signature, gpg);
    }

    @Override
    public LocalPhysicalGraph initialSchedule() {
        lpg = new LocalPhysicalGraph();

        handleLogical(job, gpg);
        handleVirtualAndSwitcher(job, gpg);
        createEdges(job, gpg);

        return lpg;
    }

    @Override
    public void restore(final LocalPhysicalGraph lpg, final SnapShotDescripter snapShot,
                        final SnapShotFileManager fileManager) throws RestoreException {
        restoreLogicalAndSelector(lpg, snapShot, fileManager);
        restoreVirtualAndSwitcher(lpg, snapShot);
    }

    private void restoreLogicalAndSelector(final LocalPhysicalGraph lpg, final SnapShotDescripter snapShot,
                                           final SnapShotFileManager fileManager) throws RestoreException {

        Iterator<LocalVertex> iter = lpg.iteratorOfVertex();
        while (iter.hasNext()) {
            final LocalVertex localVertex = iter.next();

            if (localVertex instanceof LocalPhysicalVertex) {
                final LocalPhysicalVertex lpv = (LocalPhysicalVertex) localVertex;
                final Vertex v = lpv.getVertex();

                if (v instanceof LogicalVertex) {
                    ModuleBase instance;
                    if (ReflectionUtil.isImplemented(v.getModuleClass(), SnapShottable.class)) {
                        // The vertex must be serialized.
                        try {
                            instance = instanceManager.restore(lpv, fileManager.getVertexFile(snapShot, lpv));
                        } catch (ClassNotFoundException | IOException e) {
                            throw new RestoreException(e);
                        }
                    } else {
                        instance = instanceManager.createNewInstance(v);
                    }

                    final LogicalVertex logicalVertex = (LogicalVertex) v;
                    LocalThread localThread = resourceManager.assignCPUResource(job, logicalVertex.getCpuUsage(),
                                                                                busyLoopMode);
                    lpv.setLocalThread(localThread);
                    lpv.setInstance(instance);
                }
            } else if (localVertex instanceof LocalSelectorVertex) {
                final LocalSelectorVertex lsv = (LocalSelectorVertex) localVertex;
                final LocalThread localThread = resourceManager.assignCPUResource(job, 1, busyLoopMode);
                lsv.setBindAddress(signature.getAddress());
                lsv.setLocalThread(localThread);
            }
        }
    }

    private void restoreVirtualAndSwitcher(final LocalPhysicalGraph lpg, final SnapShotDescripter snapShot)
            throws RestoreException {
        Iterator<LocalVertex> iter = lpg.iteratorOfVertex();
        while (iter.hasNext()) {
            final LocalVertex localVertex = iter.next();
            if (localVertex instanceof LocalPhysicalVertex) {
                final LocalPhysicalVertex lpv = (LocalPhysicalVertex) localVertex;
                final Vertex v = lpv.getVertex();

                if (v instanceof VirtualVertex) {
                    final LocalPhysicalVertex actual = lpv.getActualVertex();
                    lpv.setLocalThread(actual.getLocalThread());
                    lpv.setInstance(actual.getModuleInstance());
                } else if (v instanceof SwitcherVertex) {
                    final SwitcherVertex switcher = (SwitcherVertex) v;
                    final ModuleBase instance = instanceManager.createNewInstance(switcher);
                    LocalThread thread = resourceManager.assignCPUResource(job, switcher.getCpuUsage(), busyLoopMode);
                    lpv.setLocalThread(thread);
                    lpv.setInstance(instance);
                }
            }
        }
    }

    private void handleLogical(final QueueLinkerJob job, final GlobalPhysicalGraph gpg) {
        Iterator<GlobalPhysicalVertex> iter = gpg.iteratorOfVertex();

        while (iter.hasNext()) {
            final GlobalPhysicalVertex gpv = iter.next();
            if (!gpv.getWorkerServerSignature().equals(signature)) {
                continue;
            }

            final Vertex v = gpv.getVertex();
            if (v instanceof LogicalVertex) {
                final LogicalVertex lv = (LogicalVertex) v;
                for (int i = 0; i < lv.getVertexProperty().numThreadPerNode(); i++) {
                    LocalThread thread = resourceManager.assignCPUResource(job, lv.getCpuUsage(), busyLoopMode);
                    ModuleBase instance = instanceManager.createNewInstance(lv);
                    lpg.addPhysicalVertex(lv, gpv, thread, instance, i, 1.0);
                }
            }
        }
    }

    private void handleVirtualAndSwitcher(final QueueLinkerJob job, final GlobalPhysicalGraph gpg) {
        Iterator<GlobalPhysicalVertex> iter = gpg.iteratorOfVertex();

        while (iter.hasNext()) {
            final GlobalPhysicalVertex gpv = iter.next();

            if (!gpv.getWorkerServerSignature().equals(signature)) {
                continue;
            }

            final Vertex v = gpv.getVertex();

            if (v instanceof VirtualVertex) {
                final VirtualVertex virtualVertex = (VirtualVertex) v;
                List<LocalPhysicalVertex> pvs = lpg.findPhysicalVertex(virtualVertex.getLogicalVertex());
                for (LocalPhysicalVertex pv : pvs) {
                    LocalPhysicalVertex lpv = lpg.addPhysicalVertex(virtualVertex, gpv, pv.getLocalThread(),
                                                                    pv.getModuleInstance(), pv.getLocalPartitionId(),
                                                                    pv.getLocalPartitioningRate());
                    lpv.setActualVertex(pv);
                }
            } else if (v instanceof SwitcherVertex) {
                final SwitcherVertex switcherVertex = (SwitcherVertex) v;
                final Vertex srcVertex = switcherVertex.getInputEdge().getSrcVertex();
                List<LocalPhysicalVertex> srcPVs = lpg.findPhysicalVertex(srcVertex);
                for (LocalPhysicalVertex srcPV : srcPVs) {
                    ModuleBase instance = instanceManager.createNewInstance(switcherVertex);
                    LocalThread thread = resourceManager.assignCPUResource(job, switcherVertex.getCpuUsage(),
                                                                           busyLoopMode);
                    lpg.addPhysicalVertex(switcherVertex, gpv, thread, instance, 0, 1.0);
                }
            }
        }
    }

    private void createEdges(final QueueLinkerJob job, final GlobalPhysicalGraph gpg) {
        Iterator<GlobalPhysicalEdge> iter = gpg.iteratorOfEdge();

        LocalSelectorVertex selectorVertex = null;

        while (iter.hasNext()) {
            final GlobalPhysicalEdge gpe = iter.next();
            final GlobalPhysicalVertex srcGPV = gpe.getSrcGlobalVertex();
            final GlobalPhysicalVertex destGPV = gpe.getDestGlobalVertex();
            final List<LocalPhysicalVertex> srcLPVs = lpg.findPhysicalVertex(srcGPV);
            final List<LocalPhysicalVertex> destLPVs = lpg.findPhysicalVertex(destGPV);

            if (srcGPV.getWorkerServerSignature().equals(signature)) {
                if (destGPV.getWorkerServerSignature().equals(signature)) {
                    // Both vertices will run on this worker. Connect them locally.
                    for (LocalPhysicalVertex srcPV : srcLPVs) {
                        for (LocalPhysicalVertex destPV : destLPVs) {
                            lpg.addPhysicalEdge(gpe, srcPV, destPV);
                        }
                    }
                } else {
                    // The destination vertex will run on a remote worker and
                    // the source vertex will run on this worker and has to send data to the remote vertex.
                    if (selectorVertex == null) {
                        selectorVertex = createSelectorVertex(signature.getAddress());
                    }
                    final int selectorInput = selectorVertex.addSelectorInput();
                    for (LocalPhysicalVertex srcPV : srcLPVs) {
                        lpg.addLocalSelectorDestEdge(srcPV, gpe.getSrcQueueLogicalId(), selectorVertex, selectorInput,
                                                     gpe);
                    }
                }
            } else if (destGPV.getWorkerServerSignature().equals(signature)) {
                // The source vertex will run on a remote worker and
                // the destination vertex will run on this worker and has to receive data from it.
                if (selectorVertex == null) {
                    selectorVertex = createSelectorVertex(signature.getAddress());
                }
                final int selectorOutput = selectorVertex.addSelectorOutput();
                for (LocalPhysicalVertex destPV : destLPVs) {
                    lpg.addLocalSelectorSourceEdge(selectorVertex, selectorOutput, destPV, gpe.getDestQueueLogicalId(),
                                                   gpe);
                }
            }
        }
    }

    private LocalSelectorVertex createSelectorVertex(final InetAddress bindAddress) {
        LocalThread thread = resourceManager.assignCPUResource(job, 1, busyLoopMode);
        return lpg.addSelectorVertex(thread, bindAddress);
    }

    @Override
    public LocalPhysicalGraph schedule() {
        return lpg;
    }

    @Override
    public void setOperatorStat(final LocalVertex vertex, final VertexStat stat) {
        // TODO Auto-generated method stub
    }
}
