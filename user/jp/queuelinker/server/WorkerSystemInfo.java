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

package jp.queuelinker.server;

import java.io.Serializable;

/**
 *
 */
public interface WorkerSystemInfo extends Serializable {

    /**
     * Returns the number of CPUs that the worker has.
     * @return The number of CPUs.
     */
    int getCpuCount();

    /**
     * Returns the memory size that the worker has.
     * @return Memory size.
     */
    long getMemorySize();
}
