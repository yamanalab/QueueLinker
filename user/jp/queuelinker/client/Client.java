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

package jp.queuelinker.client;

import java.util.List;

import jp.queuelinker.server.MasterSignature;
import jp.queuelinker.server.WorkerSignature;

/**
 * Interface for client implementation.
 */
public interface Client {

    /**
     * Send a request to the master to start the job.
     * @param job A job to be executed.
     * @return The handle of the job.
     * @throws ClientException If an error occurs during the starting of the
     *         job.
     */
    JobHandle startJob(QueueLinkerJob job) throws ClientException;

    /**
     * Send a request to the master to suspend a job. The suspended job can be
     * resumed in the future.
     * @param handle A job to be suspended.
     * @throws ClientException If an error occurs during the suspending of the
     *         job.
     */
    void suspendJob(JobHandle handle) throws ClientException;

    /**
     * Sends a request to the master to resume a job.
     * @param handle A job to be resumed.
     * @throws ClientException If an error occurs during the suspending of the
     *         job.
     */
    void resumeJob(JobHandle handle) throws ClientException;

    /**
     * Send a request to the master to stop the job.
     * @param handle A job to be stopped.
     * @throws ClientException If an error occurs during the stopping of the
     *         job.
     */
    void stopJob(JobHandle handle) throws ClientException;

    /**
     * Send a request to the master to wait for the job to stop. The job
     * finished when this method returns.
     * @param handle A job to be observed.
     * @throws ClientException If an error occurs during the joining of the job.
     */
    void joinJob(JobHandle handle) throws ClientException;

    /**
     * Get a PhysicalGraph representing the module deployment of the job.
     * @param handle A job to be retrieved the graph.
     * @return A physical graph representing the deployment.
     * @throws ClientException If an error occurs during the joining of the job.
     */
    PhysicalGraph getPhysicalGraph(JobHandle handle) throws ClientException;

    /**
     * Update the statistics of the graph.
     * @param graph A graph to be updated.
     * @throws ClientException If an error occurs during the joining of the job.
     */
    void updateStats(PhysicalGraph graph) throws ClientException;

    /**
     * Get the signature of this client.
     * @return Signature of this client.
     */
    ClientSignature getClientSignature();

    /**
     * Get the signature of the master.
     * @return Signature of the master.
     */
    MasterSignature getMasterServerSignature();

    /**
     * Get signatures all of the workers.
     * @return List including the signatures of all workers.
     * @throws ClientException If an error occurs.
     */
    List<WorkerSignature> getAllWorkers() throws ClientException;

    /**
     * Get all job handles.
     * @return List including the handles of all jobs.
     * @throws ClientException If an error occurs.
     */
    List<JobHandle> getAllJobs() throws ClientException;

    /**
     * Take a snap shot of a job.
     * @param handle A job handle to be snap snapshotted.
     * @return Snap shot handle.
     * @throws ClientException If an error occurs.
     */
    SnapShotHandle snapShot(JobHandle handle) throws ClientException;

    /**
     * Restore an application state from a snap shot.
     * @param snapShot Snap shot handle.
     * @return A JobHandle.
     * @throws ClientException If an error occurs.
     */
    JobHandle restore(SnapShotHandle snapShot) throws ClientException;

    /**
     * Get all snapshot handles.
     * @return List including all snapshot handles.
     * @throws ClientException If an error occurs.
     */
    List<SnapShotHandle> getAllSnapShots() throws ClientException;

    /**
     * Gather statistics of a job.
     * @param handle A job to be gathered statistics.
     * @return Statistics of the job.
     * @throws ClientException If an error occurs.
     */
    JobStatistics getJobStatistics(JobHandle handle) throws ClientException;
}
