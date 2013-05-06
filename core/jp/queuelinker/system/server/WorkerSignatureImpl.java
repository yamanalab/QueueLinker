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

import java.net.InetAddress;

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;

/**
*
*/
public final class WorkerSignatureImpl implements WorkerSignature {

   /**
    *
    */
   private static final long serialVersionUID = -4537977838898760377L;

   /**
    * The ID of this worker.
    */
   private final int serverId;

   /**
    * The name of this worker.
    */
   private final String hostName;

   /**
    * The Address of this worker.
    */
   private final InetAddress address;

   /**
    * The system information of this worker.
    */
   private final WorkerSystemInfo systemInfo;

   /**
    * @param serverId The ID of this worker.
    * @param hostName The name of this worker.
    * @param address The Address of this worker.
    * @param systemInfo The system information of this worker.
    */
   WorkerSignatureImpl(final int serverId, final String hostName,
                       final InetAddress address, final WorkerSystemInfo systemInfo) {
       this.serverId = serverId;
       this.hostName = hostName;
       this.address = address;
       this.systemInfo = systemInfo;
   }

   /**
    * @return The ID of this worker.
    */
   @Override
public int getServerId() {
       return serverId;
   }

   /**
    * @return The host name of this worker.
    */
   @Override
public String getHostName() {
       return hostName;
   }

   /**
    * @return The system information of this worker.
    */
   @Override
public WorkerSystemInfo getSystemInfo() {
       return systemInfo;
   }

   /**
    * @return The Address of this worker.
    */
   @Override
public InetAddress getAddress() {
       return address;
   }

   @Override
   public boolean equals(final Object obj) {
       if (!(obj instanceof WorkerSignature)) {
           return false;
       }
       final WorkerSignatureImpl target = (WorkerSignatureImpl) obj;
       return serverId == target.serverId && address.equals(target.address);
   }

   @Override
   public int hashCode() {
       return serverId;
   }
}
