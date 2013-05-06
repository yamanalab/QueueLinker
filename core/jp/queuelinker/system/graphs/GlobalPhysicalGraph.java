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

package jp.queuelinker.system.graphs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.client.Edge;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.util.GenericGraph;

/**
 *
 */
public class GlobalPhysicalGraph implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3808929078182417316L;

    /**
     *
     */
    private final GenericGraph<GlobalPhysicalVertex, GlobalPhysicalEdge> graph = new GenericGraph<>();

    /**
     *
     */
    private final HashMap<Vertex, ArrayList<GlobalPhysicalVertex>> mapping = new HashMap<>();

    /**
     * @param vertex
     * @param worker
     * @param partitionId
     * @param partitioningRate
     * @return
     */
    public GlobalPhysicalVertex addVertex(final Vertex vertex, final WorkerSignature worker, final int partitionId,
                                          final double partitioningRate) {
        GlobalPhysicalVertex gpv = new GlobalPhysicalVertex(vertex, worker, partitionId, partitioningRate);
        graph.addVertex(gpv);
        ArrayList<GlobalPhysicalVertex> list = mapping.get(gpv.getVertex());
        if (list == null) {
            list = new ArrayList<>();
            mapping.put(gpv.getVertex(), list);
        }
        list.add(gpv);
        return gpv;
    }

    /**
     * @param srcVertex
     * @param srcQueueId
     * @param destVertex
     * @param destQueueId
     * @param logicalEdge
     * @return
     */
    public GlobalPhysicalEdge addEdge(final GlobalPhysicalVertex srcVertex, final int srcQueueId, final GlobalPhysicalVertex destVertex,
                                      final int destQueueId, final Edge logicalEdge) {
        GlobalPhysicalEdge edge = new GlobalPhysicalEdge(srcVertex, srcQueueId, destVertex, destQueueId, logicalEdge);
        graph.addEdge(edge);
        return edge;
    }

    /**
     * @param vertex
     * @return
     */
    public List<GlobalPhysicalVertex> findGlobalPhysicalVertex(final Vertex vertex) {
        return new ArrayList<>(mapping.get(vertex));
    }

    /**
     * @param vertex
     * @param worker
     * @return
     */
    public GlobalPhysicalVertex findGlobalPhysicalVertex(final Vertex vertex, final WorkerSignature worker) {
        ArrayList<GlobalPhysicalVertex> list = mapping.get(vertex);
        for (GlobalPhysicalVertex gpv : list) {
            if (gpv.getWorkerServerSignature().equals(worker)) {
                return gpv;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public Iterator<GlobalPhysicalVertex> iteratorOfVertex() {
        return graph.iteratorOfVertex();
    }

    /**
     * @return
     */
    public Iterator<GlobalPhysicalEdge> iteratorOfEdge() {
        return graph.iteratorOfEdge();
    }

    /**
     * @return The number of the nodes.
     */
    public int nodeCount() {
        return graph.nodeCount();
    }

    /**
     * @return
     */
    public int edgeCount() {
        return graph.edgeCount();
    }

    /**
     * @return
     */
    public int getWorkerCount() {
        return getAllWorkers().size();
    }

    /**
     * @return
     */
    public Collection<WorkerSignature> getAllWorkers() {
        Iterator<GlobalPhysicalVertex> iter = iteratorOfVertex();
        HashSet<WorkerSignature> workers = new HashSet<>();
        while (iter.hasNext()) {
            final GlobalPhysicalVertex v = iter.next();
            workers.add(v.getWorkerServerSignature());
        }
        return workers;
    }
}
