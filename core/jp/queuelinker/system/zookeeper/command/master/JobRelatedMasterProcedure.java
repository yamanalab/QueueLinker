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

package jp.queuelinker.system.zookeeper.command.master;

import jp.queuelinker.system.zookeeper.MasterByZookeeper;


/**
 *
 */
public abstract class JobRelatedMasterProcedure extends MasterWatchHandler {

    /**
     * The ID of the job.
     */
    private final long jobId;

    /**
     * @param master The instance of the master.
     * @param jobId The ID of a job that is a target of this command.
     */
    public JobRelatedMasterProcedure(final MasterByZookeeper master, final long jobId) {
        super(master);
        this.jobId = jobId;
    }

    /**
     * @return The job ID
     */
    public final long getJobId() {
        return jobId;
    }
}
