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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.graphs.GlobalPhysicalEdge;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.GlobalPhysicalVertex;
import jp.queuelinker.system.util.GenericGraph;

/**
 * This class represents a physical graph.
 */
public class PhysicalGraph {

    /**
     * The ID of the job.
     */
    private final long jobId;

    /**
     * The GenericGraph instance to be used to represent this physical graph.
     */
    private final GenericGraph<PhysicalVertex, PhysicalEdge> graph = new GenericGraph<>();

    /**
     * Creates a physical graph with the specified parameters.
     * @param jobId
     * @param gpg
     */
    PhysicalGraph(final long jobId, final GlobalPhysicalGraph gpg) {
        this.jobId = jobId;
        HashMap<GlobalPhysicalVertex, PhysicalVertex> mapping = new HashMap<>();
        Iterator<GlobalPhysicalVertex> vertexIter = gpg.iteratorOfVertex();
        while (vertexIter.hasNext()) {
            GlobalPhysicalVertex gpv = vertexIter.next();
            PhysicalVertex pv = addPhysicalVertex(gpv.getGlobalPhysicalVertexId(), gpv.getVertex(),
                                                  gpv.getWorkerServerSignature());
            mapping.put(gpv, pv);
        }

        Iterator<GlobalPhysicalEdge> edgeIter = gpg.iteratorOfEdge();
        while (edgeIter.hasNext()) {
            GlobalPhysicalEdge gpe = edgeIter.next();
            addPhysicalEdge(mapping.get(gpe.getSrcGlobalVertex()), mapping.get(gpe.getDestGlobalVertex()),
                            gpe.getLogicalEdge());
        }
    }

    /**
     * @param physicalId
     * @param v
     * @param signature
     * @return
     */
    PhysicalVertex addPhysicalVertex(final int physicalId, final Vertex v, final WorkerSignature signature) {
        PhysicalVertex pv = new PhysicalVertex(physicalId, v, signature);
        graph.addVertex(pv);
        return pv;
    }

    /**
     * @param srcVertex
     * @param destVertex
     * @param edge
     * @return
     */
    PhysicalEdge addPhysicalEdge(final PhysicalVertex srcVertex, final PhysicalVertex destVertex, final Edge edge) {
        PhysicalEdge pe = new PhysicalEdge(srcVertex, edge.getSrcQueueId(), destVertex, edge.getDestQueueId());
        graph.addEdge(pe);
        return pe;
    }

    /**
     * Finds physical vertices that are represented by a vertex.
     * @param vertex A vertex
     * @return A list including such physical vertices.
     */
    public final List<PhysicalVertex> findPhysicalVertex(final Vertex vertex) {
        ArrayList<PhysicalVertex> ret = new ArrayList<>();
        Iterator<PhysicalVertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            final PhysicalVertex pv = iter.next();
            if (pv.getVertex().equals(vertex)) {
                ret.add(pv);
            }
        }
        return ret;
    }

    /**
     * Returns the iterator to iterate vertices in this physical graph.
     * @return The iterator to iterate vertices in this physical graph.
     */
    public final Iterator<PhysicalVertex> iteratorOfVertex() {
        return graph.iteratorOfVertex();
    }

    /**
     * Returns an iterator to iterate the edges in this physical graph.
     * @return The iterator to iterate the edges in this physical graph.
     */
    public final Iterator<PhysicalEdge> iteratorOfEdge() {
        return graph.iteratorOfEdge();
    }

    /**
     * Returns the ID of the job of which deployment is described by this physical graph.
     * @return The ID of the job.
     */
    public final long getJobId() {
        return jobId;
    }
}
