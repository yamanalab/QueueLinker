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

import java.util.List;

import jp.queuelinker.client.Vertex;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.sched.LocalThread;

public class LocalPhysicalVertex extends LocalVertex {

    private static final long serialVersionUID = 5117228329539371731L;

    private final Vertex vertex;

    private LocalPhysicalVertex actualVertex;

    private transient ModuleBase instance;

    private final GlobalPhysicalVertex globalVertex;

    private final double localPartitioningRate;

    private final int localPartitionId;

    LocalPhysicalVertex(final Vertex vertex, final GlobalPhysicalVertex globalVertex,
            final LocalThread thread, final ModuleBase instance, final int localPartitionId,
            final double localPartitioningRate) {
        super(thread);

        this.vertex = vertex;
        this.globalVertex = globalVertex;
        this.instance = instance;
        this.localPartitioningRate = localPartitioningRate;
        this.localPartitionId = localPartitionId;

        for (int i = 0; i < vertex.getInputQueueCount(); i++) {
            addInput();
        }
        for (int i = 0; i < vertex.getOutputQueueCount(); i++) {
            addOutput();
        }
    }

    public Vertex getVertex() {
        return vertex;
    }

    @Override
    public int getOutputQueueCount() {
        assert (super.getOutputCount() == vertex.getOutputQueueCount());
        return vertex.getOutputQueueCount();
    }

    @Override
    public int getInputQueueCount() {
        assert (super.getInputCount() == vertex.getInputQueueCount());
        return vertex.getInputQueueCount();
    }

    @Override
    public int getOutputBranchCount(final int queueId) {
        return vertex.getOutputBranchCount(queueId);
    }

    public ModuleBase getModuleInstance() {
        return instance;
    }

    public boolean isBusyLoopMode() {
        return thread.isBusyLoopMode();
    }

    public LocalPhysicalVertex getActualVertex() {
        return actualVertex;
    }

    public void setActualVertex(final LocalPhysicalVertex actualVertex) {
        this.actualVertex = actualVertex;
    }

    public GlobalPhysicalVertex getGlobalVertex() {
        return globalVertex;
    }

    public boolean hasRemoteSource(final int queueId) {
        return getSourceSelectorEdge(queueId) != null;
    }

    public boolean hasRemoteSink(final int queueId) {
        return getSinkSelectorVertex(queueId) != null;
    }

    public boolean isVirtual() {
        return actualVertex != null;
    }

    public LocalEdge getSourceSelectorEdge(final int queueId) {
        List<LocalEdge> edges = getInputEdges(queueId);
        for (LocalEdge edge : edges) {
            if (edge.getSrcVertex() instanceof LocalSelectorVertex)
                return edge;
        }
        return null;
    }

    public LocalSelectorVertex getSinkSelectorVertex(final int queueId) {
        List<LocalEdge> edges = getOutputEdges(queueId);
        for (LocalEdge edge : edges) {
            if (edge.getDestVertex() instanceof LocalSelectorVertex)
                return (LocalSelectorVertex) edge.getDestVertex();
        }
        return null;
    }

    public int getGlobalPartitionId() {
        return globalVertex.getPartitionId();
    }

    public double getGlobalPartitioningRate() {
        return globalVertex.getPartitioningRate();
    }

    public int getLocalPartitionId() {
        return localPartitionId;
    }

    public double getLocalPartitioningRate() {
        return localPartitioningRate;
    }

    public int getGlobalVertexId() {
        return globalVertex.getGlobalPhysicalVertexId();
    }

    @Override
    public String getName() {
        return vertex.getName();
    }

    public void setInstance(final ModuleBase instance) {
        this.instance = instance;
    }

    public int getJobVertexId() {
        return vertex.getVertexId();
    }

    // public int getPartitionCount(int outputQueueId) {
    // List<LocalEdge> edges = super.getOutputEdges(outputQueueId);
    // int maxValue = 0;
    // for (LocalEdge edge : edges)
    // maxValue = Math.max(maxValue, edge.getDestVertexPartitionId() + 1);
    // return maxValue;
    // }
}
