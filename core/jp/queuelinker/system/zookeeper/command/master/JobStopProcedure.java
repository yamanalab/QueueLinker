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

import jp.queuelinker.system.job.JobStatus;
import jp.queuelinker.system.job.JobStatus.Status;
import jp.queuelinker.system.zookeeper.MasterByZookeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.KeeperException;

/**
 * This is a procedure to stop a job.
 */
public final class JobStopProcedure extends JobRelatedMasterProcedure {

    /**
     * @param master The instance of MasterByZookeeper.
     * @param jobId The ID of a job to be stopped.
     */
    public JobStopProcedure(final MasterByZookeeper master, final long jobId) {
        super(master, jobId);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.zookeeper.command.master.MasterWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException {
        JobStatus status = new JobStatus();
        status.setStatus(Status.TERMINATING);
        zookeeper.setObject(ZookeeperStrings.jobStatusPath(getJobId()), status, -1);
    }
}
