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

import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.sched.LocalScheduler;

/**
 * The LocalJobInfo class maintains jobs running on a worker. This is a
 * singleton class.
 */
public final class LocalJobInfo {

    /**
     * The singleton instance of this class.
     */
    private static final LocalJobInfo INSTANCE = new LocalJobInfo();

    /**
     *
     */
    private final HashMap<JobDescriptor, LocalScheduler> schedulers = new HashMap<>();

    /**
     *
     */
    private final HashMap<JobDescriptor, GlobalPhysicalGraph> globalGraphs = new HashMap<>();

    /**
     *
     */
    private final HashMap<JobDescriptor, LocalPhysicalGraph> localGraphs = new HashMap<>();

    /**
     * Private constructor for singleton.
     */
    private LocalJobInfo() {
    }

    /**
     * Returns the singleton instance of this class.
     * @return The singleton instance.
     */
    public static LocalJobInfo getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the scheduler of this job.
     * @param job The JobDescriptor of a job to be found the scheduler.
     * @return The LocalScheduler of this job.
     */
    public LocalScheduler getJobScheduler(final JobDescriptor job) {
        return schedulers.get(job);
    }

    /**
     * @param jobDesc
     * @param scheduler
     */
    public void setJobScheduler(final JobDescriptor jobDesc, final LocalScheduler scheduler) {
        schedulers.put(jobDesc, scheduler);
    }

    /**
     * @param job
     * @return
     */
    public LocalPhysicalGraph getLocalPhysicalGraph(final JobDescriptor job) {
        return localGraphs.get(job);
    }

    /**
     * Adds a new job.
     * @param job
     * @param graph
     * @param localGraph
     */
    public void addNewJob(final JobDescriptor job,
                          final GlobalPhysicalGraph graph, final LocalPhysicalGraph localGraph) {
        localGraphs.put(job, localGraph);
        globalGraphs.put(job, graph);
    }
}
