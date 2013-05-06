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

import java.util.HashMap;

import jp.queuelinker.client.ClientSignature;

/**
 * The ClientSet maintains clients connected to the master.
 * This is a singleton class.
 */
final class ClientManager {
    /**
     * The singleton instance.
     */
    private static final ClientManager INSTANCE = new ClientManager();

    /**
     * The clients managed by this class.
     */
    private HashMap<Integer, ClientSignature> clients = new HashMap<>();

    /**
     * Private constructor for singleton.
     */
    private ClientManager() {
    }

    /**
     * Returns the singleton instance of this class.
     * @return The singleton instance of this class.
     */
    public static ClientManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new client that joined to this system.
     * @param signature The signature of the new client.
     */
    public void clientJoined(final ClientSignature signature) {
        clients.put(signature.getClientId(), signature);
    }

    /**
     * Finds a client with the specified ID.
     * @param clientId The ID of the client to be retrieved.
     * @return The signature of the found client, or null if there is no
     *         such a client that has the ID.
     */
    public ClientSignature retrieveClient(final int clientId) {
        return clients.get(clientId);
    }

    /**
     * Removes a client that left from this system.
     * @param signature The signature of the left client.
     */
    public void clientLeft(final ClientSignature signature) {
        assert (clients.containsKey(signature.getClientId()));
        clients.remove(signature.getClientId());
    }
}
