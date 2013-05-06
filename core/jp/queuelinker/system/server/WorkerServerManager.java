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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;

/**
 * The WorkerServerManager maintains workers in this system. This is a singleton
 * class.
 */
public final class WorkerServerManager {
    /**
     * The singleton instance of this class.
     */
    private static final WorkerServerManager INSTANCE
                                    = new WorkerServerManager();

    /**
     * This HashMap maintains the instances of WorkerServerInstance.
     */
    private final HashMap<WorkerSignature, WorkerServerInstance> workers
                                                            = new HashMap<>();

    /**
     * Private constructor for singleton class.
     */
    private WorkerServerManager() {
    }

    /**
     * Returns the singleton instance of this class.
     * @return The singleton instance.
     */
    public static WorkerServerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new worker that joined to this system.
     * @param signature The signature of the new worker.
     * @param systemInfo The system information of the new worker.
     */
    public void joinWorker(final WorkerSignature signature,
                           final WorkerSystemInfo systemInfo) {
        WorkerServerInstance worker = new WorkerServerInstance(signature,
                                                               systemInfo);
        workers.put(signature, worker);
    }

    /**
     * Removes a worker that left from this system.
     * @param signature The signature of a worker that left from this system.
     */
    public void leaveWorker(final WorkerSignature signature) {
        workers.remove(signature);
    }

    /**
     * Returns all of the instances of WorkerServerInstance.
     * @return The unmodifiable collection including all WorkerServerInstance.
     */
    public Collection<WorkerServerInstance> getAllWorkers() {
        return Collections.unmodifiableCollection(workers.values());
    }

    /**
     * @return The WorkerServerInstance to be used.
     */
    public WorkerServerInstance getLowLoadInstance() {
        return workers.values().iterator().next();
    }

    /**
     * Finds a WorkerSignature by using a host name.
     * @param hostName The host name of a worker to be retrieved.
     * @return The WorkerSignature of the worker, or null if there is no such
     *         worker with the specified host name.
     */
    public WorkerSignature findWorkerSignature(final String hostName) {
        for (WorkerSignature signature : workers.keySet()) {
            if (signature.getHostName().equals(hostName)) {
                return signature;
            }
        }
        return null;
    }
}
