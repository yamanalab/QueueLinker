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

import java.util.List;

import jp.queuelinker.system.zookeeper.MasterByZookeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 *
 */
public class NewWorkerWatcher extends MasterWatchHandler {

    /**
     * @param master
     */
    public NewWorkerWatcher(final MasterByZookeeper master) {
        super(master);
    }

    @Override
    public void execute() throws InterruptedException, KeeperException {
        List<String> children = zookeeper.getChildren(ZookeeperStrings.workerRegistrationPath(), this);
        for (String child : children) {
            final int workerId = Integer.valueOf(child);

            try {
                zookeeper.delete(ZookeeperStrings.workerRegistrationPath() + "/" + child, -1);
            } catch (KeeperException ke) {
                // The worker leaved immediately. Ignore it.
                continue;
            }

            zookeeper.create(ZookeeperStrings.workerPath(workerId),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zookeeper.create(ZookeeperStrings.workerRequestPath(workerId),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zookeeper.create(ZookeeperStrings.workerOrderPath(workerId),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            final WorkerHeatWatcher heartWatcher = new WorkerHeatWatcher(master, workerId);
            if (zookeeper.exists(ZookeeperStrings.workerHeartPath(workerId), heartWatcher) != null) {
                heartWatcher.execute();
            }
        }
    }
}
