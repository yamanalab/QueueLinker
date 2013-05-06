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

import jp.queuelinker.module.base.HashCoder;

public class LocalSelectorDestEdge extends LocalEdge {

    private final GlobalPhysicalEdge globalEdge;

    public LocalSelectorDestEdge(LocalPhysicalVertex srcVertex, int srcQueueId,
            LocalSelectorVertex destSelectorVertex, int destQueueId,
            GlobalPhysicalEdge globalEdge) {
        super(srcVertex, srcQueueId, destSelectorVertex, destQueueId);

        this.globalEdge = globalEdge;
    }

    public LocalSelectorVertex getDestSelector() {
        return (LocalSelectorVertex) super.getDestVertex();
    }

    public LocalPhysicalVertex getSrcVertex() {
        return (LocalPhysicalVertex) super.getSrcVertex();
    }

    public int getSrcQueueLogicalId() {
        return globalEdge.getSrcQueueLogicalId();
    }

    public int getDestQueueLogicalId() {
        return globalEdge.getDestQueueLogicalId();
    }

    @Override
    public int getLogicalBranchId() {
        return globalEdge.getLogicalBranchId();
    }

    @Override
    public double getTotalPartitioningRate() {
        return globalEdge.getDestPartitioningRate();
    }

    @Override
    public int getSrcQueueThreadLocalId() {
        return getSrcVertex().getOutputQueueThreadLocalId(
                getSrcQueueLogicalId());
    }

    @Override
    public int getDestQueueThreadLocalId() {
        return getDestVertex().getInputQueueThreadLocalId(
                getDestQueueLogicalId());
    }

    @Override
    public int getGlobalPartitioningId() {
        return globalEdge.getDestVertexPartitionId();
    }

    @Override
    public int getLocalPartitioningId() {
        return 0;
    }

    @Override
    public GlobalPhysicalEdge getGlobalPhysicalEdge() {
        return globalEdge;
    }

    @Override
    public HashCoder getDestHashCoder() {
        return globalEdge.getDestHashCoder();
    }
}
