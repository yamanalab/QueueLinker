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

package jp.queuelinker.system.job;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


/**
 *
 */
public final class JobCollection {

    /**
     *
     */
    private final HashSet<JobDescriptor> runningJobs = new HashSet<>();

    /**
     *
     */
    private final HashSet<JobDescriptor> finishedJobs = new HashSet<>();

    /**
     *
     */
    private static JobCollection instance;

    /**
     *
     */
    public JobCollection() {
    }

    /**
     * @return
     */
    public static synchronized JobCollection getInstance() {
        if (instance == null) {
            instance = new JobCollection();
        }
        return instance;
    }

    /**
     * @param job
     */
    public void addNewJob(final JobDescriptor job) {
        if (runningJobs.contains(job) || finishedJobs.contains(job)) {
            throw new IllegalArgumentException("BUG: The job is illegal.");
        }
        runningJobs.add(job);
    }

    /**
     * @param job
     */
    public void jobFinished(final JobDescriptor job) {
        if (!runningJobs.remove(job)) {
            throw new IllegalArgumentException("BUG: The job is illegal.");
        }
        finishedJobs.add(job);
    }

    /**
     * @return
     */
    public Collection<JobDescriptor> getRunningJobs() {
        return Collections.unmodifiableCollection(runningJobs);
    }

    /**
     * @return
     */
    public Collection<JobDescriptor> getFinishedJobs() {
        return Collections.unmodifiableCollection(finishedJobs);
    }

    /**
     * @param job
     * @return
     */
    public JobStatus.Status getJobStatus(final JobDescriptor job) {
        assert (runningJobs.contains(job) || finishedJobs.contains(job));
        if (runningJobs.contains(job)) {
            return JobStatus.Status.RUNNING;
        } else {
            return JobStatus.Status.TERMINATED;
        }
    }
}
