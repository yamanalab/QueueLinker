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

import jp.queuelinker.client.Edge;
import jp.queuelinker.module.base.HashCoder;

public class LocalPhysicalEdge extends LocalEdge {

    private final GlobalPhysicalEdge gpe;

    LocalPhysicalEdge(final LocalPhysicalVertex srcVertex, final int srcQueueLogicalId, final LocalPhysicalVertex destVertex,
            final int destQueueLogicalId, final GlobalPhysicalEdge gpe) {
        super(srcVertex, srcQueueLogicalId, destVertex, destQueueLogicalId);
        this.gpe = gpe;
    }

    public int getSrcQueueLogicalId() {
        return gpe.getSrcQueueLogicalId();
    }

    @Override
    public int getSrcQueueThreadLocalId() {
        return getSrcVertex().getOutputQueueThreadLocalId(getSrcQueueLogicalId());
    }

    @Override
    public int getDestQueueLogicalId() {
        return gpe.getDestQueueLogicalId();
    }

    @Override
    public int getDestQueueThreadLocalId() {
        return getDestVertex().getInputQueueThreadLocalId(getDestQueueLogicalId());
    }

    @Override
    public int getLogicalBranchId() {
        return gpe.getLogicalBranchId();
    }

    @Override
    public GlobalPhysicalEdge getGlobalPhysicalEdge() {
        return gpe;
    }

    public Edge getLogicalEdge() {
        return gpe.getLogicalEdge();
    }

    public int getLocalEdgeId() {
        return super.getEdgeId();
    }

    @Override
    public LocalPhysicalVertex getDestVertex() {
        return (LocalPhysicalVertex) super.getDestVertex();
    }

    @Override
    public LocalPhysicalVertex getSrcVertex() {
        return (LocalPhysicalVertex) super.getSrcVertex();
    }

    @Override
    public double getTotalPartitioningRate() {
        return getDestVertex().getLocalPartitioningRate();
    }

    @Override
    public int getGlobalPartitioningId() {
        return getDestVertex().getGlobalPartitionId();
    }

    @Override
    public int getLocalPartitioningId() {
        return getDestVertex().getLocalPartitionId();
    }

    @Override
    public HashCoder getDestHashCoder() {
        return gpe.getDestHashCoder();
    }
}
