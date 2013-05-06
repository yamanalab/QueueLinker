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

package jp.queuelinker.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.module.base.FlowSwitcherModule;
import jp.queuelinker.module.base.LogicalModule;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.util.GenericGraph;

/**
 * This class represents a logical graph.
 */
public class LogicalGraph implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4227496592796699027L;

    /**
     *
     */
    private final GenericGraph<Vertex, Edge> graph = new GenericGraph<>();

    /**
     * Creates a logical graph.
     */
    public LogicalGraph() {
    }

    /**
     * Adds a logical vertex to this graph with specified parameters.
     * @param module The module that performs this vertex function.
     * @param property The property of this vertex.
     * @param name The name of new vertex.
     * @return The new logical vertex.
     */
    public final LogicalVertex addLogicalVertex(final Class<? extends LogicalModule> module,
                                                final LogicalVertexProperty property, final String name) {
        LogicalVertex ret = new LogicalVertex(module, property, name);
        graph.addVertex(ret);
        return ret;
    }

    /**
     * Adds a logical vertex to this graph with specified parameters.
     * @param module The module that performs this vertex function.
     * @param property The property of this vertex.
     * @return The new logical vertex.
     */
    public final LogicalVertex addLogicalVertex(final Class<? extends LogicalModule> module,
                                          final LogicalVertexProperty property) {
        return addLogicalVertex(module, property, "");
    }

    /**
     * Adds a logical vertex to this graph with specified parameters.
     * @param module The module that performs this vertex function.
     * @return The new logical vertex.
     */
    public final LogicalVertex addLogicalVertex(final Class<? extends LogicalModule> module) {
        return addLogicalVertex(module, new LogicalVertexProperty.VertexPropertyBuilder().build());
    }

    /**
     * Adds a logical vertex to this graph with specified parameters.
     * @param module The module that performs this vertex function.
     * @param name The name of new vertex.
     * @return The new logical vertex.
     */
    public final LogicalVertex addLogicalVertex(final Class<? extends LogicalModule> module, final String name) {
        return addLogicalVertex(module, new LogicalVertexProperty.VertexPropertyBuilder().build(), name);
    }

    /**
     * Adds a switcher to this graph with specified parameters.
     * @param module The module that performs this vertex function.
     * @param switcherCount The number of the destination of this switcher.
     * @param name The name of this switcher.
     * @return The new switcher.
     */
    public final SwitcherVertex addFlowSwitcher(final Class<? extends FlowSwitcherModule<?>> module,
                                                final int switcherCount, final String name) {
        SwitcherVertex switcher = new SwitcherVertex(module, name, switcherCount);
        graph.addVertex(switcher);
        return switcher;
    }

    /**
     * Adds a switcher to this graph with specified parameters.
     * @param module The module that performs this vertex function.
     * @param switcherCount The number of the destination of this switcher.
     * @return The new switcher.
     */
    public final SwitcherVertex addFlowSwitcher(final Class<? extends FlowSwitcherModule<?>> module,
                                                final int switcherCount) {
        return addFlowSwitcher(module, switcherCount, "Switcher" + module.getName());
    }

    /**
     * Adds a virtual vertex to this graph with specified parameters.
     * @param vertex The vertex that is virtualized by this vertex.
     * @param name The name of this virtual vertex.
     * @return The new virtual vertex.
     */
    public final VirtualVertex addVirtualVertex(final LogicalVertex vertex, final String name) {
        VirtualVertex virtualVertex = new VirtualVertex(name, vertex);
        graph.addVertex(virtualVertex);
        vertex.addVirtualVertex(virtualVertex);
        return virtualVertex;
    }

    /**
     * Adds a virtual vertex to this graph with specified parameters.
     * @param vertex The vertex that is virtualized by this vertex.
     * @return The new virtual vertex.
     */
    public final VirtualVertex addVirtualVertex(final LogicalVertex vertex) {
        return addVirtualVertex(vertex, "Virtual" + vertex.getName());
    }

    /**
     * Adds a logical edge to this graph with specified parameters. This edge will connect the default output queue and
     * the default input queue of the specified vertices.
     * @param srcVertex The vertex that is the source of the new edge.
     * @param destVertex The vertex that is the destination of the new edge.
     * @return The new logical edge.
     */
    public final LogicalEdge addLogicalEdge(final Vertex srcVertex, final Vertex destVertex) {
        return addLogicalEdge(srcVertex.getDefaultOutputQueueHandle(), destVertex.getDefaultInputQueueHandle(),
                              LogicalEdgeProperty.getDefaultProperty());
    }

    /**
     * Adds a logical edge to this graph with specified parameters. This edge will connect the default output queue and
     * the default input queue of the specified vertices.
     * @param srcVertex The vertex that is the source of the new edge.
     * @param destVertex The vertex that is the destination of the new edge.
     * @param property The property of this edge.
     * @return The new logical edge.
     */
    public final LogicalEdge addLogicalEdge(final Vertex srcVertex, final Vertex destVertex,
                                            final LogicalEdgeProperty property) {
        return addLogicalEdge(srcVertex.getDefaultOutputQueueHandle(), destVertex.getDefaultInputQueueHandle(),
                              property);
    }

    /**
     * Adds a logical edge to this graph with specified parameters.
     * @param srcHandle The handle of the source queue of this edge.
     * @param destHandle The handle of the destination queue of this edge.
     * @return The new logical edge.
     */
    public final LogicalEdge addLogicalEdge(final OutputQueueHandle srcHandle, final InputQueueHandle destHandle) {
        return addLogicalEdge(srcHandle, destHandle, LogicalEdgeProperty.getDefaultProperty());
    }

    /**
     * Adds a logical edge to this graph with specified parameters.
     * @param srcHandle The handle of the source queue of this edge.
     * @param destHandle The handle of the destination queue of this edge.
     * @param property The property of this edge.
     * @return The new logical edge.
     */
    private LogicalEdge addLogicalEdge(final OutputQueueHandle srcHandle, final InputQueueHandle destHandle,
                                       final LogicalEdgeProperty property) {
        LogicalEdge ret = new LogicalEdge(srcHandle.getVertex(), srcHandle,
                                          destHandle.getVertex(), destHandle, property);
        graph.addEdge(ret);
        return ret;
    }

    /**
     * Returns the number of vertices in this graph.
     * @return The number of vertices.
     */
    public final int getVertexCount() {
        return graph.nodeCount();
    }

    /**
     * Returns the number of edges in this graph.
     * @return The number of edges.
     */
    public final int getEdgeCount() {
        return graph.edgeCount();
    }

    /**
     * Returns vertices that use the specified module.
     * @param module
     * @return A list including such vertices.
     */
    public List<Vertex> getVertecies(final Class<? extends ModuleBase> module) {
        ArrayList<Vertex> ret = new ArrayList<Vertex>();
        Iterator<Vertex> iter = iteratorOfVertex();

        while (iter.hasNext()) {
            Vertex vertex = iter.next();
            if (vertex.getModuleClass().equals(module)) {
                ret.add(vertex);
            }
        }
        return ret;
    }

    /**
     * Returns all vertices in this graph.
     * @return A collection including all vertices.
     */
    public final Collection<Vertex> getVertexSet() {
        return graph.getAllVertices();
    }

    /**
     * Finds vertices that use a sink module.
     * @return A list including such vertices.
     */
    public final List<Vertex> findSinkModuleVertex() {
        ArrayList<Vertex> ret = new ArrayList<>();
        Iterator<Vertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            Vertex v = iter.next();
            if (v.isSinkModule()) {
                ret.add(v);
            }
        }
        return ret;
    }

    /**
     * Finds vertices that use a source module.
     * @return
     */
    public Vertex findSourceVertex() {
        Iterator<Vertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            Vertex currentNode = iter.next();
            HashSet<Vertex> visited = new HashSet<Vertex>();
            forwardRecursive(currentNode, visited);
            if (visited.size() == getVertexCount()) {
                return currentNode;
            }
        }

        throw new RuntimeException("BUG: no Source?");
    }

    /**
     * @param currentNode
     * @param visited
     */
    private void forwardRecursive(final Vertex currentNode, final HashSet<Vertex> visited) {
        if (visited.contains(currentNode)) {
            return;
        }

        visited.add(currentNode);

        ArrayList<Edge> es = currentNode.getAllOutputEdges();
        if (es == null) {
            return;
        }

        for (Edge edge : es) {
            forwardRecursive(edge.getDestVertex(), visited);
        }
    }

    /**
     * @param currentNode
     * @param visited
     */
    private void backwardRecursive(final Vertex currentNode, final HashSet<Vertex> visited) {
        if (visited.contains(currentNode)) {
            return;
        }

        visited.add(currentNode);

        ArrayList<Edge> es = currentNode.getAllInputEdges();
        if (es == null) {
            return;
        }

        for (Edge edge : es) {
            backwardRecursive(edge.getSrcVertex(), visited);
        }
    }

//    /**
//     * Checks if this graph is a tree or not.
//     * @return True if this graph is tree, or false.
//     */
//    public final boolean isTree() {
//        final List<Vertex> sinks = findSinkModuleVertex();
//        if (sinks.size() != 1) {
//            return false;
//        }
//
//        HashSet<Vertex> visited = new HashSet<>();
//
//        backwardRecursive(sinks.get(0), visited);
//
//        return visited.size() == getVertexCount();
//    }

    /**
     * Returns an iterator to iterate all vertices in this graph.
     * @return An iterator to iterate all vertices in this graph.
     */
    public final Iterator<Vertex> iteratorOfVertex() {
        return graph.iteratorOfVertex();
    }

    /**
     * Returns an iterator to iterate all edges in this graph.
     * @return An iterator to iterate all edges in this graph.
     */
    public final Iterator<Edge> iteratorOfEdge() {
        return graph.iteratorOfEdge();
    }

    /**
     * @return
     */
    public int getSourceCount() {
        int ret = 0;
        Iterator<Vertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            Vertex v = iter.next();
            if (v instanceof LogicalVertex && ((LogicalVertex) v).isSourceModule()) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * @return
     */
    public int getSinkCount() {
        int ret = 0;
        Iterator<Vertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            Vertex v = iter.next();
            if (v instanceof LogicalVertex && ((LogicalVertex) v).isSinkModule()) {
                ret++;
            }
        }
        return ret;
    }
}
