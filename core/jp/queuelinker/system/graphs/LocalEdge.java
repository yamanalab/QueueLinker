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
import jp.queuelinker.system.util.GenericEdge;

public abstract class LocalEdge extends GenericEdge<LocalVertex> {
    public LocalEdge(LocalVertex srcVertex, int srcPortId,
            LocalVertex destVertex, int destPortId) {
        super(srcVertex, srcPortId, destVertex, destPortId);
    }

    @Override
    public LocalVertex getSrcVertex() {
        return super.getSrcVertex();
    }

    @Override
    public LocalVertex getDestVertex() {
        return super.getDestVertex();
    }

    public int getSrcQueueId() {
        return super.getSrcId();
    }

    public int getDestQueueId() {
        return super.getDestId();
    }

    public abstract int getLogicalBranchId();

    public abstract double getTotalPartitioningRate();

    public abstract int getGlobalPartitioningId();

    public abstract int getLocalPartitioningId();

    public abstract int getSrcQueueThreadLocalId();

    public abstract int getDestQueueThreadLocalId();

    public abstract int getDestQueueLogicalId();

    public abstract GlobalPhysicalEdge getGlobalPhysicalEdge();

    public abstract HashCoder getDestHashCoder();
}
