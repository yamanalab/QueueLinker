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

import java.io.Serializable;

import jp.queuelinker.client.QueueLinkerJob;

/**
 * A instance of the JobDescriptor class represents a job. This class is used
 * only from core implementations. Users must see only JobHandle class in
 * jp.queuelinker.client package.
 */
public final class JobDescriptor implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4483527985526090983L;

    /**
     * The global unique ID of this job.
     */
    private final long jobId;

    /**
     *
     */
    private final QueueLinkerJob job;

    /**
     * Creates a descriptor with the specified parameters.
     * @param jobId The ID of the job.
     * @param job The QueueLinkerJob of the job.
     */
    public JobDescriptor(final int jobId, final QueueLinkerJob job) {
        this.jobId = jobId;
        this.job = job;
    }

    /**
     * Returns the ID of the job.
     * @return The global unique ID of this job.
     */
    public long getJobId() {
        return jobId;
    }

    /**
     * Returns the QueueLinkerJob of this job.
     * @return The QueueLinkerJob of this job.
     */
    public QueueLinkerJob getJob() {
        return job;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) jobId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof JobDescriptor)) {
            return false;
        } else if (((JobDescriptor) obj).jobId == jobId) {
            return true;
        }
        return false;
    }
}
