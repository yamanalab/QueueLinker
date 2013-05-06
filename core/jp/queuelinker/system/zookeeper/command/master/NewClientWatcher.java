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
 * The NewClientWatcher watches a new client.
 */
public final class NewClientWatcher extends MasterWatchHandler {

    /**
     * @param master
     */
    public NewClientWatcher(final MasterByZookeeper master) {
        super(master);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.zookeeper.MasterWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException {
        List<String> children = zookeeper.getChildren(ZookeeperStrings.clientRegistrationPath(), this);
        for (String child : children) {
            final int clientId = Integer.valueOf(child);

            try {
                zookeeper.delete(ZookeeperStrings.clientRegistrationPath() + "/" + child, -1);
            } catch (KeeperException ke) {
                // The client leaved immediately. Ignore it.
                continue;
            }

            // Initializes some nodes for the client.
            zookeeper.create(ZookeeperStrings.clientPath(clientId),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zookeeper.create(ZookeeperStrings.clientRequestPath(clientId),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zookeeper.create(ZookeeperStrings.clientRequestAckTopPath(clientId),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            final ClientHeartWatcher heartWatcher = new ClientHeartWatcher(master, clientId);
            if (zookeeper.exists(ZookeeperStrings.clientHeartPath(clientId), heartWatcher) != null) {
                heartWatcher.execute();
            }
        }
    }
}
