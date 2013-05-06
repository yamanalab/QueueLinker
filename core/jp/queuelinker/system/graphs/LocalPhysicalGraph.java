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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.client.LogicalVertex;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.sched.LocalThread;
import jp.queuelinker.system.util.GenericGraph;

public class LocalPhysicalGraph implements Serializable {

    private final GenericGraph<LocalVertex, LocalEdge> graph = new GenericGraph<>();

    private final HashMap<Vertex, ArrayList<LocalPhysicalVertex>> mapping = new HashMap<>();

    public LocalPhysicalVertex addPhysicalVertex(final Vertex lv, final GlobalPhysicalVertex gpv,
                                                 final LocalThread thread, final ModuleBase instance,
                                                 final int localPartitionId, final double localPartitioningRate) {
        LocalPhysicalVertex vertex = new LocalPhysicalVertex(lv, gpv, thread, instance, localPartitionId,
                localPartitioningRate);
        ArrayList<LocalPhysicalVertex> list = mapping.get(vertex.getVertex());
        if (list == null) {
            list = new ArrayList<>();
            mapping.put(vertex.getVertex(), list);
            // System.err.println("Add: " + vertex.getVertex());
        }
        list.add(vertex);
        graph.addVertex(vertex);
        return vertex;
    }

    public LocalSelectorVertex addSelectorVertex(final LocalThread thread, final InetAddress bindAddress) {
        LocalSelectorVertex vertex = new LocalSelectorVertex(thread, bindAddress);
        graph.addVertex(vertex);
        return vertex;
    }

    public LocalPhysicalEdge addPhysicalEdge(final GlobalPhysicalEdge gpe, final LocalPhysicalVertex srcVertex,
                                             final LocalPhysicalVertex destVertex) {
        LocalPhysicalEdge pe = new LocalPhysicalEdge(srcVertex, gpe.getSrcQueueLogicalId(), destVertex,
                gpe.getDestQueueLogicalId(), gpe);
        graph.addEdge(pe);
        return pe;
    }

    public LocalSelectorSourceEdge addLocalSelectorSourceEdge(final LocalSelectorVertex srcSelectorVertex,
                                                              final int srcQueueId,
                                                              final LocalPhysicalVertex destVertex,
                                                              final int destQueueId, final GlobalPhysicalEdge gpe) {
        LocalSelectorSourceEdge srcEdge = new LocalSelectorSourceEdge(srcSelectorVertex, srcQueueId, destVertex,
                destQueueId, gpe);
        graph.addEdge(srcEdge);
        return srcEdge;
    }

    public LocalSelectorDestEdge addLocalSelectorDestEdge(final LocalPhysicalVertex srcVertex, final int srcQueueId,
                                                          final LocalSelectorVertex destSelectorVertex,
                                                          final int destQueueId, final GlobalPhysicalEdge gpe) {
        LocalSelectorDestEdge destEdge = new LocalSelectorDestEdge(srcVertex, srcQueueId, destSelectorVertex,
                destQueueId, gpe);
        graph.addEdge(destEdge);
        return destEdge;
    }

    public Iterator<LocalVertex> iteratorOfVertex() {
        return graph.iteratorOfVertex();
    }

    public Iterator<LocalEdge> iteratorOfEdge() {
        return graph.iteratorOfEdge();
    }

    public List<LocalPhysicalVertex> findPhysicalVertex(final GlobalPhysicalVertex gpv) {
        return findPhysicalVertex(gpv.getVertex());
    }

    public List<LocalPhysicalVertex> findPhysicalVertex(final Vertex lv) {
        ArrayList<LocalPhysicalVertex> list = mapping.get(lv);
        // System.err.println("Get: " + lv.getName() + " Result: " + list);
        if (list == null)
            return new ArrayList<>();
        return Collections.unmodifiableList(list);
    }

    public LocalPhysicalVertex findPhysicalVertex(final Vertex lv, final LocalThread localThread) {
        ArrayList<LocalPhysicalVertex> list = mapping.get(lv);

        for (LocalPhysicalVertex pv : list) {
            if (pv.getLocalThread().equals(localThread))
                return pv;
        }
        return null;
    }

    public Collection<LocalVertex> vertices() {
        return graph.getAllVertices();
    }

    public List<LocalVertex> findSinkModuleVertices() {
        ArrayList<LocalVertex> ret = new ArrayList<>();
        Iterator<LocalVertex> iter = graph.iteratorOfVertex();
        while (iter.hasNext()) {
            LocalVertex vertex = iter.next();
            if (!(vertex instanceof LocalPhysicalVertex))
                continue;

            LocalPhysicalVertex lpv = (LocalPhysicalVertex) vertex;
            if (lpv.getVertex().isSinkModule())
                ret.add(lpv);
        }
        return ret;
    }

    public int getVertexCount() {
        return graph.nodeCount();
    }

    public int getSourceCount() {
        int ret = 0;
        Iterator<LocalVertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            LocalVertex lv = iter.next();
            if (!(lv instanceof LocalPhysicalVertex)) {
                continue;
            }
            LocalPhysicalVertex lpv = (LocalPhysicalVertex) lv;
            if (lpv.getVertex().isSourceModule()) {
                ret++;
            }
        }
        return ret;
    }

    public int getSinkCount() {
        return findSinkModuleVertices().size();
    }

    public LogicalVertex findSourceVertex() {
        Iterator<LocalVertex> iter = iteratorOfVertex();
        while (iter.hasNext()) {
            LocalVertex lv = iter.next();
            if (!(lv instanceof LocalPhysicalVertex)) {
                continue;
            }
            LocalPhysicalVertex lpv = (LocalPhysicalVertex) lv;
            if (lpv.getVertex().isSourceModule()) {
                return (LogicalVertex) lpv.getVertex();
            }
        }
        return null;
    }
}
