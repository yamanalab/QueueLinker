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

/**
 * This class represents a status of a job.
 */
public final class JobStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7840264733072937056L;

    /**
     *
     */
    public enum Status {
        /**
         * A status that the modules of the job are being deployed on the workers.
         */
        DEPLOYING,
        /**
         * A status that the workers are performing network connections.
         */
        CONNECTING,
        /**
         * A status that the job is running normally.
         */
        RUNNING,
        /**
         * A status that the job is now being terminated.
         */
        TERMINATING,
        /**
         * A status that the job is stopped.
         */
        TERMINATED,
    }

    /**
     *
     */
    private Status status;

    /**
     *
     */
    public JobStatus() {
        status = Status.DEPLOYING;
    }

    /**
     * Returns the status.
     * @return The status of a job.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param newStatus
     */
    public void setStatus(final Status newStatus) {
        this.status = newStatus;
    }
}
