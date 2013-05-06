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

import jp.queuelinker.client.Edge;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.system.util.GenericEdge;

/**
 *
 */
public class GlobalPhysicalEdge extends GenericEdge<GlobalPhysicalVertex> implements Serializable {

    /**
     *
     */
    private final Edge logicalEdge;

    /**
     * @param srcVertex
     * @param srcQueueId
     * @param destVertex
     * @param destQueueId
     * @param logicalEdge
     */
    GlobalPhysicalEdge(final GlobalPhysicalVertex srcVertex, final int srcQueueId,
            final GlobalPhysicalVertex destVertex, final int destQueueId, final Edge logicalEdge) {
        super(srcVertex, srcQueueId, destVertex, destQueueId);

        assert (srcQueueId == logicalEdge.getSrcQueueId() && destQueueId == logicalEdge.getDestQueueId());
        this.logicalEdge = logicalEdge;
    }

    /**
     * @return
     */
    public GlobalPhysicalVertex getSrcGlobalVertex() {
        return super.getSrcVertex();
    }

    /**
     * @return
     */
    public Vertex getSrcUserVertex() {
        return getSrcGlobalVertex().getVertex();
    }

    /**
     * @return
     */
    public GlobalPhysicalVertex getDestGlobalVertex() {
        return super.getDestVertex();
    }

    /**
     * @return
     */
    public Vertex getDestUserVertex() {
        return getDestGlobalVertex().getVertex();
    }

    /**
     * @return
     */
    public int getSrcQueueLogicalId() {
        return logicalEdge.getSrcQueueId();
    }

    /**
     * @return
     */
    public int getDestQueueLogicalId() {
        return logicalEdge.getDestQueueId();
    }

    /**
     * @return
     */
    public int getLogicalBranchId() {
        return logicalEdge.getBranchId();
    }

    /**
     * @return
     */
    public Edge getLogicalEdge() {
        return logicalEdge;
    }

    /**
     * @return
     */
    public int getGlobalEdgeId() {
        return super.getEdgeId();
    }

    /**
     * @return
     */
    public int getDestVertexPartitionId() {
        return super.getDestVertex().getPartitionId();
    }

    /**
     * @return
     */
    public double getDestPartitioningRate() {
        return super.getDestVertex().getPartitioningRate();
    }

    /**
     * @return
     */
    public HashCoder getDestHashCoder() {
        return logicalEdge.getDestHashCoder();
    }
}
