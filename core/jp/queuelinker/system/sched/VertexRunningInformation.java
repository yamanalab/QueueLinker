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

package jp.queuelinker.system.sched;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;

public class VertexRunningInformation implements Serializable {

	private final int vertexId;

	private final InetAddress address;

	private final HashMap<Integer, SocketAddress> addressMap = new HashMap<>();

	public VertexRunningInformation(final int vertexId) {
		this(vertexId, null);
	}

	public VertexRunningInformation(final int vertexId, final InetAddress address) {
		this.vertexId = vertexId;
		this.address = address;
	}

	public int getVertexId() {
		return vertexId;
	}

	public SocketAddress getAcceptSocketAddress(final int queueId) {
		return addressMap.get(queueId);
	}

	public void addAcceptAddress(final int queueId, final InetSocketAddress acceptAddress) {
		addressMap.put(queueId, acceptAddress);
	}
}
