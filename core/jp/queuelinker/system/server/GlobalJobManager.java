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

import java.util.HashMap;

import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;

/**
 * The GlobalJobSet maintains all jobs existing in this system. This is a
 * singleton class.
 */
final class GlobalJobManager {

    /**
     * The singleton instance of this class.
     */
    private static final GlobalJobManager INSTANCE = new GlobalJobManager();

    /**
     * The jobs managed by this system.
     */
    private final HashMap<Long, JobDescriptor> jobs = new HashMap<>();

    /**
     * This HashMap maintains the instances of GlobalPhysicalGraph of the jobs.
     */
    private final HashMap<JobDescriptor, GlobalPhysicalGraph> graphs = new HashMap<>();

    /**
     * The next ID for a new job.
     */
    private int nextJobId;

    /**
     * Constructor. This must be private because this class is singleton.
     */
    private GlobalJobManager() {
    }

    /**
     * Gets the instance of this class.
     * @return The singleton instance.
     */
    public static GlobalJobManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new job and creates a JobDescripter.
     * @param job A new job to be executed.
     * @param graph The graph of the new job.
     * @return The JobDescripter representing this job.
     */
    public JobDescriptor addNewJob(final QueueLinkerJob job, final GlobalPhysicalGraph graph) {
        JobDescriptor jobDesc = new JobDescriptor(nextJobId++, job);
        jobs.put(jobDesc.getJobId(), jobDesc);
        graphs.put(jobDesc, graph);
        return jobDesc;
    }

    /**
     * Finds a JobDescripter with a job ID.
     * @param jobId The ID of the job to be retrieved.
     * @return The JobDescripter representing the job.
     */
    public synchronized JobDescriptor findJobDescripter(final long jobId) {
        return jobs.get(jobId);
    }

    /**
     * Deletes a job. This must be called the job is no longer used.
     * @param jobId The job ID to be removed.
     * @return The JobDescripter of the removed job, or null if there is no such
     *         a job.
     */
    public synchronized JobDescriptor deleteDescripter(final long jobId) {
        return jobs.remove(jobId);
    }

    /**
     * Gets the GlobalPhysicalGraph of a job.
     * @param jobDesc The JobDescripter of the job.
     * @return The GlobalPhysicalGraph of the specified job.
     */
    public synchronized GlobalPhysicalGraph getPhysicalGraph(final JobDescriptor jobDesc) {
        return graphs.get(jobDesc);
    }
}
