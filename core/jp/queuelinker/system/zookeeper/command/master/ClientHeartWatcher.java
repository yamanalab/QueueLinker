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

import jp.queuelinker.client.ClientSignature;
import jp.queuelinker.system.zookeeper.MasterByZookeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * The ClientHeartWatcher class is used to monitor client status.
 */
public final class ClientHeartWatcher extends MasterWatchHandler {
    /**
     *
     */
    private final int clientId;

    /**
     * @param master
     * @param clientId
     */
    public ClientHeartWatcher(final MasterByZookeeper master, final int clientId) {
        super(master);
        this.clientId = clientId;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.server.zookeeper.MasterWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException {
        final ClientSignature signature =
                (ClientSignature) zookeeper.getObject(ZookeeperStrings.clientSignaturePath(clientId), false, null);
        if (event == null || event.getType() == EventType.NodeCreated) {
            master.joinClient(signature);
            if (event != null) {
                // We have to register the watcher again.
                if (zookeeper.exists(ZookeeperStrings.clientHeartPath(clientId), this) == null) {
                    master.clientLeft(signature);
                    return;
                }
            }
            final ClientRequestWatcher requestWatcher = new ClientRequestWatcher(master, clientId);
            List<String> children = zookeeper.getChildren(ZookeeperStrings.clientRequestPath(clientId), requestWatcher);
            if (!children.isEmpty()) {
                requestWatcher.execute();
            }
        } else if (event.getType() == EventType.NodeDeleted) {
            master.clientLeft(signature);
        }
    }
}
