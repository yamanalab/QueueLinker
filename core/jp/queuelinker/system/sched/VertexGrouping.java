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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import jp.queuelinker.system.graphs.LocalVertex;


public class VertexGrouping {

	private final HashMap<LocalVertex, Integer> vertexMap = new HashMap<LocalVertex, Integer>();

	private int nextId;

	private final LocalVertex rootVertex;

	public VertexGrouping(final LocalVertex rootVertex) {
		this.rootVertex = rootVertex;
	}

	public LocalVertex getRootVertex() {
		return rootVertex;
	}

	public boolean sameGroup(final LocalVertex v1, final LocalVertex v2) {
		return vertexMap.get(v1) == vertexMap.get(v2);
	}

	public int createGroupId() {
		return nextId++;
	}

	public int getMappingId(final LocalVertex vertex) {
		return vertexMap.get(vertex);
	}

	public void joinGroup(final int groupId, final LocalVertex vertex) {
		if (groupId >= nextId) {
			throw new RuntimeException("BUG: The group id is illegal.");
		}
		vertexMap.put(vertex, groupId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<LocalVertex, Integer>> iter = vertexMap.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<LocalVertex, Integer> entry = iter.next();
			builder.append(entry.getValue() + " ");
		}
		return builder.toString();
	}
}
