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

public class LocalSelectorSourceEdge extends LocalEdge {

    private final GlobalPhysicalEdge globalEdge;

    public LocalSelectorSourceEdge(LocalSelectorVertex srcSelector,
            int srcQueueId, LocalPhysicalVertex destVertex, int destQueueId,
            GlobalPhysicalEdge globalEdge) {
        super(srcSelector, srcQueueId, destVertex, destQueueId);
        this.globalEdge = globalEdge;
    }

    public LocalSelectorVertex getSrcSelector() {
        return (LocalSelectorVertex) super.getSrcVertex();
    }

    @Override
    public LocalPhysicalVertex getDestVertex() {
        return (LocalPhysicalVertex) super.getDestVertex();
    }

    public int getSrcLogicalId() {
        return globalEdge.getSrcQueueLogicalId();
    }

    public int getDestLogicalId() {
        return globalEdge.getDestQueueLogicalId();
    }

    @Override
    public int getLogicalBranchId() {
        return globalEdge.getLogicalBranchId();
    }

    public int getDestVertexLocalPartitionId() {
        return getDestVertex().getLocalPartitionId();
    }

    @Override
    public double getTotalPartitioningRate() {
        return getDestVertex().getLocalPartitioningRate();
    }

    @Override
    public int getSrcQueueThreadLocalId() {
        return getSrcVertex().getOutputQueueThreadLocalId(getSrcId());
    }

    @Override
    public int getDestQueueThreadLocalId() {
        return getDestVertex().getInputQueueThreadLocalId(getDestLogicalId());
    }

    @Override
    public int getGlobalPartitioningId() {
        return globalEdge.getDestVertexPartitionId();
    }

    @Override
    public int getLocalPartitioningId() {
        return getDestVertex().getLocalPartitionId();
    }

    @Override
    public GlobalPhysicalEdge getGlobalPhysicalEdge() {
        return globalEdge;
    }

    @Override
    public int getDestQueueLogicalId() {
        return globalEdge.getDestQueueLogicalId();
    }

    @Override
    public HashCoder getDestHashCoder() {
        return globalEdge.getDestHashCoder();
    }
}
