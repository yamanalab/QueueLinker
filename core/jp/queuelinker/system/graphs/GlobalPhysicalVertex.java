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
import java.util.Arrays;
import java.util.List;

import jp.queuelinker.client.Vertex;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.util.GenericVertex;

public class GlobalPhysicalVertex extends GenericVertex<GlobalPhysicalVertex, GlobalPhysicalEdge> implements
        Serializable {

    private static final long serialVersionUID = 1L;

    private final Vertex vertex;

    private final WorkerSignature worker;

    private final int partitionId;

    private final double partitioningRate;

    private final GlobalPhysicalEdge[][][] outputs;

    GlobalPhysicalVertex(final Vertex vertex, final WorkerSignature worker, final int partitionId, final double partitioningRate) {
        super(vertex.getInputQueueCount(), vertex.getOutputQueueCount());
        this.vertex = vertex;
        this.worker = worker;
        this.partitionId = partitionId;
        this.partitioningRate = partitioningRate;
        this.outputs = new GlobalPhysicalEdge[vertex.getOutputQueueCount()][][];
        for (int queue = 0; queue < vertex.getOutputQueueCount(); queue++) {
            this.outputs[queue] = new GlobalPhysicalEdge[vertex.getOutputBranchCount(queue)][0];
        }
    }

    @Override
    protected int addOutputEdge(final int queueId, final GlobalPhysicalEdge edge) {
        super.addOutputEdge(queueId, edge);

        final int branchId = edge.getLogicalBranchId();
        final int destPartitionId = edge.getDestVertexPartitionId();
        if (outputs[queueId][branchId].length <= destPartitionId) {
            outputs[queueId][branchId] = Arrays.copyOf(outputs[queueId][branchId], destPartitionId + 1);
        }

        if (outputs[queueId][branchId][destPartitionId] != null) {
            throw new RuntimeException("BUG: duplicated partitionID");
        }
        outputs[queueId][branchId][destPartitionId] = edge;

        return destPartitionId;
    }

    public int getLogicalInputQueueCount() {
        return vertex.getInputQueueCount();
    }

    public int getLogicalOutputQueueCount() {
        return vertex.getOutputQueueCount();
    }

    public int getLogicalVertexId() {
        return vertex.getVertexId();
    }

    public Vertex getVertex() {
        return vertex;
    }

    @Override
    public List<GlobalPhysicalEdge> getOutputEdges(final int logicalQueueId) {
        return super.getOutputEdges(logicalQueueId);
    }

    public List<GlobalPhysicalEdge> getOutputEdges(final int logicalQueueId, final int logicalBranchId) {
        return new ArrayList<>(Arrays.asList(outputs[logicalQueueId][logicalBranchId]));
    }

    public GlobalPhysicalEdge getOutputEdge(final int logicalQueueId, final int logicalBranchId, final int partitionId) {
        return outputs[logicalQueueId][logicalBranchId][partitionId];
    }

    @Override
    public List<GlobalPhysicalEdge> getInputEdges(final int logicalQueueId) {
        return super.getInputEdges(logicalQueueId);
    }

    public WorkerSignature getWorkerServerSignature() {
        return worker;
    }

    public int getGlobalPhysicalVertexId() {
        return super.getVertexId();
    }

    public double getPartitioningRate() {
        return partitioningRate;
    }

    public int getPartitionId() {
        return partitionId;
    }
}
