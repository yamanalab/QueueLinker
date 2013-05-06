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

import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.system.util.GenericEdge;

/**
 *
 */
public abstract class Edge extends GenericEdge<Vertex> {

    /**
     *
     */
    private static final long serialVersionUID = -489399957082674350L;

    /**
     * @param srcVertex Source vertex of this edge.
     * @param srcPortId Source port ID of the source vertex.
     * @param destVertex Destination vertex of this edge.
     * @param destPortId Destination port ID of the destination vertex.
     */
    public Edge(final Vertex srcVertex,  final int srcPortId,
                final Vertex destVertex, final int destPortId) {
        super(srcVertex, srcPortId, destVertex, destPortId);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericEdge#getDestVertex()
     */
    @Override
    public final Vertex getDestVertex() {
        return super.getDestVertex();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericEdge#getSrcVertex()
     */
    @Override
    public final Vertex getSrcVertex() {
        return super.getSrcVertex();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericEdge#getBranchId()
     */
    @Override
    public final int getBranchId() {
        return super.getBranchId();
    }

    /**
     * Returns the distributing mode of the source vertex.
     * @return The distributing mode.
     */
    public final VertexDistributingMode getSrcDistributingMode() {
        return getSrcVertex().getDistributingMode();
    }

    /**
     * Returns the distributing mode of the destination vertex.
     * @return The distributing mode.
     */
    public final VertexDistributingMode getDestDistributingMode() {
        return getDestVertex().getDistributingMode();
    }

    /**
     * Returns the hash coder of the destination vertex.
     * @return The hash coder.
     */
    public final HashCoder getDestHashCoder() {
        return getDestVertex().getHashCoder(getDestQueueId());
    }

    /**
     * @return The handle of the output queue that is the source of this edge.
     */
    public abstract OutputQueueHandle getSrcQueueHandle();

    /**
     * @return The Queue ID of the output queue that is the source of this edge.
     */
    public abstract int getSrcQueueId();

    /**
     * @return The handle of the input queue that is the destination of this
     * edge.
     */
    public abstract InputQueueHandle getDestQueueHandle();

    /**
     * @return The Queue ID of the input queue that is the destination of this
     * edge.
     */
    public abstract int getDestQueueId();
}
