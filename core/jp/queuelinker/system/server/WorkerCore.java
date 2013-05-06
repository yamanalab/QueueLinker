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

package jp.queuelinker.system.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.exception.SnapShotException;
import jp.queuelinker.system.exception.WorkerException;
import jp.queuelinker.system.graphs.GlobalPhysicalEdge;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalEdge;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalSelectorVertex;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.thread.SelectorContext;

/**
 * The WorkerCore class performs actual processing for the worker works.
 */
public final class WorkerCore {

    /**
     * The signature of this worker.
     */
    private final WorkerSignatureImpl signature;

    /**
     * The system information of this worker.
     */
    private final WorkerSystemInfoImpl systemInfo;

    /**
     * The worker server.
     */
    private final WorkerServer workerServer;

    /**
     * The job manager.
     */
    private final LocalJobSet jobManager;

    /**
     * @param serverId The ID of this worker.
     * @param hostName The host name of this worker.
     * @param address The InetAddress of this worker.
     * @param workerServer The instance of WorkerServer to be used.
     */
    public WorkerCore(final int serverId, final String hostName, final InetAddress address,
            final WorkerServer workerServer) {
        this.systemInfo = WorkerSystemInfoImpl.buildInstance();
        this.signature = new WorkerSignatureImpl(serverId, hostName, address, this.systemInfo);
        this.workerServer = workerServer;
        this.jobManager = new LocalJobSet(this);
    }

    /**
     * Returns the signature of this worker.
     * @return The WorkerSignature of this worker.
     */
    public WorkerSignature getSignature() {
        return signature;
    }

    /**
     * Returns the system information of this worker.
     * @return The WorkerSystemInfo of this worker.
     */
    public WorkerSystemInfo getSystemInfo() {
        return systemInfo;
    }

    /**
     * @param jobDesc The JobDescripter of the new job.
     * @param graph The GlobalPhysicalGraph of this graph.
     * @return The LocalPhysicalGraph describing the deployment of the job.
     * @throws IOException Thrown if an error occurs.
     */
    public LocalPhysicalGraph deployJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph graph)
            throws IOException {
        return jobManager.deployJob(jobDesc, graph);
    }

    /**
     * @param job The JobDescripter of a job to be started.
     */
    public void startJob(final JobDescriptor job) {
        jobManager.startJob(job);
    }

    /**
     * @param job The JobDescripter of a job to be stopped.
     */
    public void stopJob(final JobDescriptor job) {
        jobManager.stopJob(job);
    }

    /**
     * @param job The JobDescripter of a job to be suspended.
     */
    public void suspendJob(final JobDescriptor job) {
        jobManager.suspendJob(job);
    }

    /**
     * @param job The JobDescripter of a job to be resumed.
     */
    public void resumeJob(final JobDescriptor job) {
        jobManager.resumeJob(job);
    }

    /**
     * @param jobDesc The JobDescripter of a job to be restored.
     * @param gpg
     * @param snapShot
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws RestoreException
     */
    public void restoreJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph gpg, final SnapShotDescripter snapShot)
            throws ClassNotFoundException, IOException, RestoreException {
        jobManager.restoreJob(jobDesc, gpg, snapShot);
    }

    /**
     * @param job The JobDescripter of a job to be took a snap shot.
     * @param snapShotDesc
     * @throws SnapShotException
     */
    public void snapShotJob(final JobDescriptor job, final SnapShotDescripter snapShotDesc)
                                                                    throws SnapShotException {
        try {
            jobManager.snapShotJob(job, snapShotDesc);
        } catch (IOException e) {
            throw new SnapShotException(e);
        }
    }

    /**
     * @param jobDesc
     * @throws IOException
     */
    public void startConnecting(final JobDescriptor jobDesc) throws IOException {
        LocalPhysicalGraph lpg = jobManager.getLocalPhysicalGraph(jobDesc);
        Iterator<LocalVertex> iter = lpg.iteratorOfVertex();
        while (iter.hasNext()) {
            LocalVertex lv = iter.next();
            if (lv instanceof LocalSelectorVertex) {
                final LocalSelectorVertex lsv = (LocalSelectorVertex) lv;
                final SelectorContext<?, ?> context = (SelectorContext<?, ?>) lsv.getContext();

                for (int i = 0; i < lsv.getInputQueueCount(); i++) {
                    final List<LocalEdge> edges = lsv.getInputEdges(i);
                    final LocalEdge edge = edges.get(0);
                    final GlobalPhysicalEdge gpe = edge.getGlobalPhysicalEdge();
                    final SocketAddress address = workerServer.getRemoteAddress(jobDesc.getJobId(), gpe
                            .getDestGlobalVertex().getGlobalPhysicalVertexId(), gpe.getLogicalEdge()
                            .getDestQueueHandle().getQueueId());

                    context.connect(i, address);

                    // log ("A vertex %d (queue %d) is connecting to a vertex %d (queue %d) on %s.\n",
                    // gpe.getSrcGlobalVertex()
                    // .getGlobalPhysicalVertexId(), gpe
                    // .getLogicalEdge()
                    // .getSrcQueueHandle().getQueueId(),
                    // gpe.getDestGlobalVertex()
                    // .getGlobalPhysicalVertexId(), gpe
                    // .getLogicalEdge()
                    // .getDestQueueHandle().getQueueId(),
                    // address);
                }
            }
        }
    }

    /**
     * @param jobDesc
     */
    public void sendStat(final JobDescriptor jobDesc) {
        LocalJobStatistics stats = jobManager.getJobStatistics(jobDesc);
        try {
            workerServer.sendStat(jobDesc, stats);
        } catch (WorkerException e) { }
    }

    /**
     * @param jobDesc
     * @return
     */
    public LocalJobStatistics getJobStatistics(final JobDescriptor jobDesc) {
        return jobManager.getJobStatistics(jobDesc);
    }
}
