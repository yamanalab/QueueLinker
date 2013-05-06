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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.client.ClientSignature;
import jp.queuelinker.client.JobStatistics;
import jp.queuelinker.client.LogicalGraph;
import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.server.MasterSignature;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.exception.SnapShotException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.sched.GeneralGlobalScheduler;
import jp.queuelinker.system.sched.GlobalScheduler;
import jp.queuelinker.system.server.LocalJobStatistics.LocalVertexStat;

/**
 * The MasterCore class performs actual processing for the master work.
 */
public final class MasterCore {

    /**
     * The signature of the master.
     */
    private final MasterSignatureImpl signature;

    /**
     * The MasterServer driving this MasterCore class.
     */
    private final MasterServer server;

    /**
     * The ClientManager for maintaining the clients.
     */
    private final ClientManager clientManager = ClientManager.getInstance();

    /**
     * The WorkerServerManager for maintaining workers.
     */
    private final WorkerServerManager workerManager = WorkerServerManager.getInstance();

    /**
     * The GlobalJobManager for managing all jobs.
     */
    private final GlobalJobManager jobManager = GlobalJobManager.getInstance();

    /**
     * The GlobalScheduler for scheduling the jobs.
     */
    private final GlobalScheduler scheduler = new GeneralGlobalScheduler();

    /**
     * The SnapShotManager for maintaining the snapshots.
     */
    private final SnapShotManager snapShotManager = SnapShotManager.getInstance();

    /**
     * Creates the master core with the specified parameters.
     * @param hostName The name of this master.
     * @param server The MasterServer will drive this master core.
     */
    public MasterCore(final String hostName, final MasterServer server) {
        this.signature = new MasterSignatureImpl(hostName);
        this.server = server;
        // checkRecovery();
    }

    /**
     * Recoveries from a previous state if needed.
     */
    public void checkRecovery() {
        List<SnapShotDescripter> descs = server.getSnapShotDescripters();
        snapShotManager.addDescripters(descs);
    }

    /**
     * Processes the new worker joined to this system.
     * @param joinedWorker The signature of the new worker.
     * @param systemInfo The system information of the worker.
     */
    public void joinWorkerServer(final WorkerSignature joinedWorker, final WorkerSystemInfo systemInfo) {
        workerManager.joinWorker(joinedWorker, systemInfo);
    }

    /**
     * Processes the worker left from this system.
     * @param leavedWorker the worker left from this system.
     */
    public void leaveWorkerServer(final WorkerSignature leavedWorker) {
        workerManager.leaveWorker(leavedWorker);
    }

    /**
     * Processes the new worker joined to this system.
     * @param signature The signature of the new client.
     */
    public void loginClient(final ClientSignature signature) {
        clientManager.clientJoined(signature);
    }

    /**
     * Processes a client left from this system.
     * @param signature The signature of the client left from this system.
     */
    public void logoutClient(final ClientSignature signature) {
        clientManager.clientLeft(signature);
    }

    /**
     * Returns the signature of this master.
     * @return The signature of this master.
     */
    public MasterSignature getSignature() {
        return signature;
    }

    /**
     * Starts a job.
     * @param job A job to be executed.
     * @return The JobDescripter of the executed job.
     */
    public JobDescriptor startJob(final QueueLinkerJob job) {
        GlobalPhysicalGraph graph = scheduler.initialSchedule(job);
        JobDescriptor jobDesc = jobManager.addNewJob(job, graph);
        server.deployLocalModules(jobDesc, graph);
        return jobDesc;
    }

    /**
     * This method does not guarantee the job actually stopped. We must use
     * joinJob method to ensure that the job stop.
     * @param jobId The ID of a job to be stopped.
     */
    public void stopJob(final long jobId) {
        final JobDescriptor jobDesc = jobManager.findJobDescripter(jobId);
        server.sendStopJobCommand(jobDesc);
        // jobManager.jobStopping(jobDesc);
    }

    /**
     * Waits a job stopped.
     * @param jobId The ID of a job to be joined.
     */
    public void joinJob(final long jobId) {
        final JobDescriptor jobDesc = jobManager.findJobDescripter(jobId);
        server.joinJob(jobDesc);
    }

    /**
     * Suspends a job.
     * @param jobId The ID of a job to be suspended.
     */
    public void suspendJob(final long jobId) {
        final JobDescriptor jobDesc = jobManager.findJobDescripter(jobId);
        server.sendSuspendJobCommand(jobDesc);
    }

    /**
     * @param jobId The ID of a job to be taken a snap shot.
     * @return The SnapShotDescripter of this snap shot.
     * @throws SnapShotException Thrown if an error occurs.
     */
    public SnapShotDescripter snapShotJob(final long jobId) throws SnapShotException {
        final JobDescriptor jobDesc = jobManager.findJobDescripter(jobId);
        SnapShotDescripter snapShotDesc = snapShotManager.createNewDescripter(jobDesc);
        GlobalPhysicalGraph graph = jobManager.getPhysicalGraph(jobDesc);
        server.doSnapShot(snapShotDesc, jobDesc, graph);
        return snapShotDesc;
    }

    /**
     * This method is not used.
     * @param jobDesc The JobDescripter that finished the deployment.
     */
    public void deployingFinished(final JobDescriptor jobDesc) {
        throw new RuntimeException("Currently, this method is not used.");
    }

    /**
     * This method is not used.
     * @param jobDesc The JobDescripter that finished the network connection.
     */
    public void connectingFinished(final JobDescriptor jobDesc) {
        throw new RuntimeException("Currently, this method is not used.");
    }

    /**
     * @param jobId The ID of a job to be found.
     * @return The JobDescripter of the job.
     */
    public JobDescriptor findJobDescripter(final long jobId) {
        return jobManager.findJobDescripter(jobId);
    }

    /**
     * Gets the statistics of a job.
     * @param jobDesc The JobDescripter of a job to be measured.
     * @return The JobStatistics of the job.
     */
    public JobStatistics getJobStatistics(final JobDescriptor jobDesc) {
        final GlobalPhysicalGraph graph = jobManager.getPhysicalGraph(jobDesc);
        Collection<WorkerSignature> workers = graph.getAllWorkers();
        List<LocalJobStatistics> stats = server.getStatistics(jobDesc, workers);

        JobStatistics ret = new JobStatistics();
        final LogicalGraph lg = jobDesc.getJob().getVertexGraph();
        Iterator<Vertex> iter = lg.iteratorOfVertex();
        while (iter.hasNext()) {
            final Vertex v = iter.next();
            // Aggregate the statistics.
            long contextCount = 0, threadCount = 0, inputCount = 0, outputCount = 0, consumedMemory = 0;
            for (LocalJobStatistics stat : stats) {
                List<LocalVertexStat> lvss = stat.getLocalVertexStat(v.getVertexId());
                if (lvss == null) {
                    continue;
                }
                for (LocalVertexStat lvs : lvss) {
                    contextCount += lvs.getWaitingOnThread();
                    threadCount += lvs.getWaitingOnThread();
                    inputCount += lvs.getInputCount();
                    outputCount += lvs.getOutputCount();
                    consumedMemory += lvs.getConsumedMemory();
                }
            }

            ret.addStat(v, contextCount, threadCount, inputCount, outputCount, consumedMemory);
        }

        return ret;
    }

    /**
     * @param jobDesc
     * @return
     */
    public GlobalPhysicalGraph getGlobalPhysicalGraph(final JobDescriptor jobDesc) {
        return jobManager.getPhysicalGraph(jobDesc);
    }

    /**
     * @param snapShotDesc
     * @return
     * @throws RestoreException
     */
    public JobDescriptor restoreJob(final SnapShotDescripter snapShotDesc) throws RestoreException {
        QueueLinkerJob job = server.restoreQueueLinkerJob(snapShotDesc);
        GlobalPhysicalGraph graph = server.restoreGlobalGraph(snapShotDesc);
        JobDescriptor jobDesc = jobManager.addNewJob(job, graph);
        scheduler.restoreJob(job, graph);
        server.doRestore(snapShotDesc, jobDesc, graph);
        return jobDesc;
    }

    /**
     * @return
     */
    public Collection<SnapShotDescripter> getAllSnapShots() {
        return snapShotManager.getAllSnapShots();
    }

    /**
     * @param name
     * @return
     */
    public SnapShotDescripter findSnapShotDescripter(final String name) {
        return snapShotManager.findSnapShotDesc(name);
    }

    public void clientJoin(final ClientSignature signature) {
        clientManager.clientJoined(signature);
    }

    public void clientLeft(final ClientSignature signature) {
        clientManager.clientLeft(signature);
    }
}
