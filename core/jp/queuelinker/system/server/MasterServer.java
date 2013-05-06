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
import java.util.List;

import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.exception.SnapShotException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;

/**
 * The MasterServer class performs network communications with workers and
 * clients.
 */
public interface MasterServer {

    /**
     * Creates instances of module and deploys them according to the
     * GlobalPhysicalGraph.
     * @param job The descriptor of a job to be deployed.
     * @param graph The GlobalPhysicalGraph representing the module deployment.
     */
    void deployLocalModules(JobDescriptor job, GlobalPhysicalGraph graph);

    /**
     * @param graph
     */
    void prepareAccepting(GlobalPhysicalGraph graph);

    /**
     * @param graph
     */
    void connectRemoteWorkers(GlobalPhysicalGraph graph);

    /**
     * Starts the execution of a job.
     * @param jobDesc The JobDescripter of a job to be executed.
     */
    void execute(JobDescriptor jobDesc);

    /**
     * Stops a job. This method sends a job stop command to workers to stop the
     * specified job.
     * @param jobDesc The JobDescripter of a job to be stopped.
     */
    void sendStopJobCommand(JobDescriptor jobDesc);

    /**
     * Suspends a job. This method sends a suspend command to workers to stop
     * the specified job.
     * @param jobDesc The JobDescripter of a job to be suspended.
     */
    void sendSuspendJobCommand(JobDescriptor jobDesc);

    /**
     * @param jobDesc The JobDescripter of a job to be waited.
     */
    void joinJob(JobDescriptor jobDesc);

    /**
     * @param jobDesc
     * @param workers
     * @return
     */
    List<LocalJobStatistics> getStatistics(JobDescriptor jobDesc, Collection<WorkerSignature> workers);

    /**
     * @return
     */
    List<SnapShotDescripter> getSnapShotDescripters();

    /**
     * @param snapDesc
     * @param jobDesc
     * @param graph
     * @throws SnapShotException
     */
    void doSnapShot(SnapShotDescripter snapDesc, JobDescriptor jobDesc, GlobalPhysicalGraph graph)
            throws SnapShotException;

    /**
     * @param snapShotDesc
     * @return
     * @throws RestoreException
     */
    QueueLinkerJob restoreQueueLinkerJob(SnapShotDescripter snapShotDesc) throws RestoreException;

    /**
     * @param snapShotDesc
     * @return
     * @throws RestoreException
     */
    GlobalPhysicalGraph restoreGlobalGraph(SnapShotDescripter snapShotDesc) throws RestoreException;

    /**
     * @param snapShotDesc
     * @param jobDesc
     * @param graph
     * @throws RestoreException
     */
    void doRestore(SnapShotDescripter snapShotDesc, JobDescriptor jobDesc, GlobalPhysicalGraph graph)
            throws RestoreException;
}
