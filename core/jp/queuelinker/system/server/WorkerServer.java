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

import java.net.SocketAddress;

import jp.queuelinker.system.exception.WorkerException;
import jp.queuelinker.system.job.JobDescriptor;

/**
 * A worker server performs communication between master server. An
 * implementation of a worker server must implement this interface.
 */
public interface WorkerServer {
    /**
     * @param jobId The ID of a job.
     * @param globalVertexId
     * @param logicalQueueId
     * @return
     */
    SocketAddress getRemoteAddress(long jobId, int globalVertexId, int logicalQueueId);

    /**
     * @param jobDesc
     * @param stat
     * @throws WorkerException Thrown if an error occurs.
     */
    void sendStat(JobDescriptor jobDesc, LocalJobStatistics stat) throws WorkerException;
}
