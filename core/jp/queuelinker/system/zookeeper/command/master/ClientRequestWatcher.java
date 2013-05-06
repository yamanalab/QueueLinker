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
import jp.queuelinker.system.zookeeper.command.ClientRequest;

import org.apache.zookeeper.KeeperException;

/**
 * The ClientRequestWatcher class is used to monitor requests from clients.
 */
public final class ClientRequestWatcher extends MasterWatchHandler {
    /**
     *
     */
    private final int clientId;

    /**
     * @param master
     * @param clientId
     */
    public ClientRequestWatcher(final MasterByZookeeper master, final int clientId) {
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
        List<String> names = zookeeper.getChildren(ZookeeperStrings.clientRequestPath(clientId), this);
        for (String name : names) {
            final String path = ZookeeperStrings.clientRequestPath(clientId) + "/" + name;
            ClientRequest request;
            try {
                request = (ClientRequest) zookeeper.getObject(path, false, null);
                zookeeper.delete(path, -1);
            } catch (KeeperException e) {
                continue;
            }
            final long requestId = Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
            master.clientRequest(request, clientId, requestId);
        }
    }
}
