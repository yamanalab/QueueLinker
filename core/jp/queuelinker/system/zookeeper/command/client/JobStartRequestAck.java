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

package jp.queuelinker.system.zookeeper.command.client;

import jp.queuelinker.system.zookeeper.command.ClientRequestAck;

/**
 *
 */
public final class JobStartRequestAck extends ClientRequestAck {

    /**
     *
     */
    private static final long serialVersionUID = 3921074823066802504L;

    /**
     *
     */
    private final long jobId;

    /**
     * @param requestId
     * @param jobId
     */
    public JobStartRequestAck(final long requestId, final long jobId) {
        super(requestId);
        this.jobId = jobId;
    }

    /**
     * Returns job ID.
     * @return The job ID.
     */
    public long getJobId() {
        return jobId;
    }
}
