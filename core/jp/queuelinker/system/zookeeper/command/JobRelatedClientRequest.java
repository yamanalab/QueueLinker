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

package jp.queuelinker.system.zookeeper.command;

/**
 *
 */
public abstract class JobRelatedClientRequest implements ClientRequest {
    /**
     *
     */
    private static final long serialVersionUID = 481623843325615034L;

    /**
     *
     */
    public final long jobId;

    /**
     * @param jobId
     */
    public JobRelatedClientRequest(final long jobId) {
        this.jobId = jobId;
    }

    /**
     * @return
     */
    public final long getJobId() {
        return jobId;
    }
}
