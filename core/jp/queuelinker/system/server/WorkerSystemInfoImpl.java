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

import jp.queuelinker.server.WorkerSystemInfo;


/**
*
*/
public final class WorkerSystemInfoImpl implements WorkerSystemInfo {

   /**
    *
    */
   private static final long serialVersionUID = -7907765422596574634L;

   /**
    * The number of CPUs.
    */
   private final int cpuCount;

   /**
    * Memory size.
    */
   private final long memorySize;

   /**
    * @param cpuCount The number of CPUs.
    * @param memorySize Memory size.
    */
   WorkerSystemInfoImpl(final int cpuCount, final long memorySize) {
       this.cpuCount = cpuCount;
       this.memorySize = memorySize;
   }

   /**
    * Creates an instance of this class on this worker.
    * @return The worker system information.
    */
   static WorkerSystemInfoImpl buildInstance() {
       return new WorkerSystemInfoImpl(Runtime.getRuntime().availableProcessors(),
                                       Runtime.getRuntime().totalMemory());
   }

   /**
    * Returns the number of CPUs that the worker has.
    * @return The number of CPUs.
    */
   @Override
public int getCpuCount() {
       return cpuCount;
   }

   /**
    * Returns the memory size that the worker has.
    * @return Memory size.
    */
   @Override
public long getMemorySize() {
       return memorySize;
   }
}
