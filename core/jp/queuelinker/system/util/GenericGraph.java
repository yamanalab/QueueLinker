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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

public class GenericGraph<V extends GenericVertex, E extends GenericEdge>
        implements Serializable {

    protected final long graphSerial;

    private static AtomicLong nextGraphSerial = new AtomicLong();

    private int nextVertexId;

    private int nextEdgeId;

    private HashMap<Integer, V> vertices = new HashMap<>();

    private HashMap<Integer, E> edges = new HashMap<>();

    public GenericGraph() {
        this.graphSerial = nextGraphSerial.getAndIncrement();
    }

    public int nodeCount() {
        return vertices.size();
    }

    public int edgeCount() {
        return edges.size();
    }

    public long getGraphSerial() {
        return graphSerial;
    }

    public Iterator<V> iteratorOfVertex() {
        return vertices.values().iterator();
    }

    public Iterator<E> iteratorOfEdge() {
        return edges.values().iterator();
    }

    public void addVertex(V vertex) {
        if (vertex.isGraphMember())
            throw new IllegalArgumentException(
                    "The vertex is arleady member of a graph.");

        vertex.setVertexId(nextVertexId);
        vertex.setGraphSerial(graphSerial);
        vertices.put(nextVertexId, vertex);
        nextVertexId++;
    }

    public void addEdge(E edge) {
        if (edge.isGraphMember())
            throw new IllegalArgumentException(
                    "The edge is arleady member of a graph.");

        edge.setEdgeId(nextEdgeId);
        edge.setGraphSerial(graphSerial);
        edges.put(nextEdgeId, edge);

        int srcBranchId = edge.getSrcVertex().addOutputEdge(edge.getSrcId(),
                edge);
        edge.getDestVertex().addInputEdge(edge.getDestId(), edge);
        edge.setBranchId(srcBranchId);

        nextEdgeId++;
    }

    public void deleteVertex(V vertex) {
        if (!vertex.isGraphMember())
            throw new IllegalArgumentException(
                    "The vertex is not member of any graphs.");
        if (vertex.getGraphSerial() != graphSerial)
            throw new IllegalArgumentException(
                    "The vertex is not member of this graph.");

        vertices.remove(vertex.getVertexId());
        vertex.leaveGraph();
    }

    public void deleteEdge(E edge) {
        if (!edge.isGraphMember())
            throw new IllegalArgumentException(
                    "The edge is not member of any graphs.");
        if (edge.getGraphSerial() != graphSerial)
            throw new IllegalArgumentException(
                    "The edge is not member of this graph.");

        edges.remove(edge.getEdgeId());
        edge.leaveGraph();
    }

    public void findOutEdges(V vertex) {
        if (vertex.getGraphSerial() != graphSerial)
            throw new IllegalArgumentException(
                    "The edge is not member of this graph.");

        vertices.get(vertex.getVertexId());
    }

    public void findInEdges(V vertex) {

    }

    public Collection<V> getAllVertices() {
        return Collections.unmodifiableCollection(vertices.values());
    }

    // protected Collection<E> findOutputEdge(V cv) {
    // }
}
