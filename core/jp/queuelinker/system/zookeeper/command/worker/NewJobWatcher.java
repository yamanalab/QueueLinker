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

package jp.queuelinker.system.zookeeper.command.worker;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.zookeeper.WorkerByZooKeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.KeeperException;

/**
 * This class watches new coming jobs and starts the jobs.
 */
public final class NewJobWatcher extends WorkerWatchHandler {

    /**
     * This HashSet maintains the paths of jobs already to be processed.
     */
    private HashSet<String> launchedJobs = new HashSet<>();

    /**
     * @param worker
     * @param zookeeper
     */
    public NewJobWatcher(final WorkerByZooKeeper worker, final ZookeeperConnection zookeeper) {
        super(worker, zookeeper);
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.server.zookeeper.WorkerWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException, IOException {
        List<String> children = zookeeper.getChildren(ZookeeperStrings.jobTopPath(), this);
        HashSet<String> newJobs = new HashSet<>();
        for (String child : children) {
            newJobs.add(child);
            if (launchedJobs.contains(child)) {
                continue;   // Skip already handled jobs.
            }
            final int jobId = Integer.valueOf(child);
            JobWatcher jobWatcher = new JobWatcher(jobId, worker, zookeeper);
            jobWatcher.initialExecute();
        }
        launchedJobs = newJobs;
    }
}
