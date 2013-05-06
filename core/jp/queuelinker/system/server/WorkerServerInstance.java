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

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;

/**
 * The WorkerServerInstance represents worker.
 */
public final class WorkerServerInstance {
    /**
     * The signature of the worker.
     */
    private final WorkerSignature signature;

    /**
     * The system information of this worker.
     */
    private final WorkerSystemInfo systemInfo;

    /**
     * The current resource data.
     */
    private WorkerServerCurrentResource currentResource
                                = new WorkerServerCurrentResource();

    /**
     * Creates a WorkerServerInstance with specified parameters.
     * @param signature The WorkerSignature of the worker.
     * @param systemInfo The WorkerSystemInfo of the worker.
     */
    public WorkerServerInstance(final WorkerSignature signature,
            final WorkerSystemInfo systemInfo) {
        this.signature = signature;
        this.systemInfo = systemInfo;
    }

    /**
     * Returns the WorkerSignature of this worker.
     * @return The WorkerSignature of this worker.
     */
    public WorkerSignature getSignature() {
        return signature;
    }

    /**
     * Returns the free memory of this worker.
     * @return The free memory size in bytes.
     */
    public long getFreeMemory() {
        return currentResource.getFreeMemory();
    }

    /**
     * Returns the total memory of this worker.
     * @return The total memory size in bytes.
     */
    public long getTotalMemory() {
        return systemInfo.getMemorySize();
    }

    /**
     * Returns the number of CPU cores.
     * @return The number of CPU cores.
     */
    public int getCPUCount() {
        return systemInfo.getCpuCount();
    }

    /**
     * Returns WorkerSystemInfo of this worker.
     * @return The WorkerSystemInfo of this worker.
     */
    public WorkerSystemInfo getSystemInfo() {
        return systemInfo;
    }

    /**
     * Sets the current resource data.
     * @param currentResource The current resource.
     */
    void setCurrentResource(final WorkerServerCurrentResource currentResource) {
        this.currentResource = currentResource;
    }
}
