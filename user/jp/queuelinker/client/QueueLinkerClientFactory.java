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

package jp.queuelinker.client;

import java.net.UnknownHostException;


/**
 * This class provides clients.
 */
public final class QueueLinkerClientFactory {

    /**
     *
     */
    private QueueLinkerClientFactory() {
    }

    /**
     * Create a client and connect to a zookeeper server.
     * @param connectString Connection string.
     * @param sessionTimeout Timeout.
     * @return Client using Zookeeper.
     * @throws ClientException Thrown if an error occurs.
     */
    public static Client getClientWithZookeeper(
                                      final String connectString,
                                      final int sessionTimeout)
            throws ClientException {
        return new ClientByZookeeper(connectString, sessionTimeout);
    }

    /**
     * Create a local mode client.
     * @return Local mode client.
     * @throws UnknownHostException Thrown if an error occurs.
     */
    public static Client getLocalClient() throws UnknownHostException {
        return new StandaloneModeClient();
    }
}
