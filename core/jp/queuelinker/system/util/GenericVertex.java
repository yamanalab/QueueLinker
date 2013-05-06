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

package jp.queuelinker.system.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GenericVertex <V extends GenericVertex, E extends GenericEdge> implements Serializable {
	private final String name;
	
	private int vertexId = -1;
	
	private long graphSerial;
	
	private int inputCount;
	
	private int outputCount;

	private ArrayList<ArrayList<E>> inputEdges = new ArrayList<>();
	
	private ArrayList<ArrayList<E>> outputEdges = new ArrayList<>();
	
	protected GenericVertex() {
		this("GenericVertex", 0, 0);
	}
	
	protected GenericVertex(String name) {
		this(name, 0, 0);
	}
	
	protected GenericVertex(int initialInputQueueCount, int initialOutputQueueCount) {
		this("GenericVertex", initialInputQueueCount, initialOutputQueueCount);
	}
	
	protected GenericVertex(String name, int initialInputQueueCount, int initialOutputQueueCount) {
		this.name = name;
		for (int i = 0; i < initialInputQueueCount; i++)
			addInput();
		for (int i = 0; i < initialOutputQueueCount; i++)
			addOutput();
	}
		
	protected int getInputCount() {
		return inputCount;
	}
	
	protected int getOutputCount() {
		return outputCount;
	}
	
	protected List<E> getInputEdges(int queueId) {
		if (queueId >= inputCount || queueId < 0)
			throw new IllegalArgumentException("The queue Id is illegal: " + queueId);
		return Collections.unmodifiableList(inputEdges.get(queueId));
	}
	
	protected ArrayList<E> getAllInputEdges() {
		ArrayList<E> ret = new ArrayList<>();
		for (ArrayList<E> list : inputEdges) {
			for (E e : list) {
				ret.add(e);
			}
		}
		return ret;
	}
	
	protected List<E> getOutputEdges(int queueId) {
		if (queueId >= outputCount || queueId < 0)
			throw new IllegalArgumentException("The queue Id is illegal: " + queueId);
		return Collections.unmodifiableList(outputEdges.get(queueId));
	}

	
	protected ArrayList<E> getAllOutputEdges() {
		ArrayList<E> ret = new ArrayList<>();
		for (ArrayList<E> list : outputEdges) {
			for (E e : list) {
				ret.add(e);
			}
		}
		return ret;
	}

	protected int addInput() {
		inputEdges.add(new ArrayList<E>());
		return inputCount++;
	}
	
	protected int addOutput() {
		outputEdges.add(new ArrayList<E>());
		return outputCount++;
	}
	
	protected int getVertexId() {
		return vertexId;
	}
	
	protected String getName() {
		return name;
	}
	
	protected long getGraphSerial() {
		return graphSerial;
	}
	
	protected boolean isGraphMember() {
		return vertexId >= 0;
	}
	
	
	protected void addInputEdge(int queueId, E edge) {
		inputEdges.get(queueId).add(edge);
	}
	
	protected int addOutputEdge(int queueId, E edge) {
		final ArrayList<E> edges = outputEdges.get(queueId);
		edges.add(edge);
		return edges.size() - 1;
	}
	
	protected void setGraphSerial(long graphSerial) {
		this.graphSerial = graphSerial;
	}
	
	protected void setVertexId(int newId) {
		this.vertexId = newId;
	}
	
	protected void leaveGraph() {
		vertexId = -1;
		graphSerial = -1;
	}
	
	protected int getOutputBranchCount(int queueId) {
		return outputEdges.get(queueId).size();
	}
	
	@Override
	public int hashCode() {
		return vertexId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GenericVertex))
			return false;
		
		final GenericVertex v = (GenericVertex)obj;
		return this.graphSerial == v.graphSerial && this.vertexId == v.vertexId;
	}
	
	@Override
	public String toString() {
		return "Vertex (id = " + vertexId + ")";
	}
}
