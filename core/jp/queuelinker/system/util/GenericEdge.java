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

package jp.queuelinker.system.util;

import java.io.Serializable;

/**
 * @param <V>
 */
public abstract class GenericEdge<V extends GenericVertex> implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7696479115681821326L;

    /**
     *
     */
    private final V srcVertex;

    /**
     *
     */
    private final int srcPortId;

    /**
     *
     */
    private int srcBranchId;

    /**
     *
     */
    private final V destVertex;

    /**
     *
     */
    private final int destPortId;

    /**
     *
     */
    private int edgeId = -1;

    /**
     *
     */
    private long graphSerial = -1;

    /**
     * @param srcVertex
     * @param srcPortId
     * @param destVertex
     * @param destPortId
     */
    public GenericEdge(final V srcVertex, final int srcPortId, final V destVertex, final int destPortId) {
        this.srcVertex = srcVertex;
        this.srcPortId = srcPortId;
        this.destVertex = destVertex;
        this.destPortId = destPortId;
    }

    /**
     * Get the source vertex of this edge.
     * @return The source vertex of this edge.
     */
    protected V getSrcVertex() {
        return srcVertex;
    }

    /**
     * @return
     */
    protected int getSrcId() {
        return srcPortId;
    }

    /**
     * @return
     */
    protected V getDestVertex() {
        return destVertex;
    }

    /**
     * @return
     */
    protected int getDestId() {
        return destPortId;
    }

    /**
     * @return
     */
    protected int getEdgeId() {
        return edgeId;
    }

    /**
     * @return
     */
    protected long getGraphSerial() {
        return graphSerial;
    }

    /**
     * @return
     */
    protected int getBranchId() {
        return srcBranchId;
    }

    /**
     * @param branchId
     */
    protected void setBranchId(final int branchId) {
        this.srcBranchId = branchId;
    }

    /**
     * @param edgeId
     */
    final void setEdgeId(final int edgeId) {
        this.edgeId = edgeId;
    }

    /**
     * @param graphSerial
     */
    final void setGraphSerial(final long graphSerial) {
        this.graphSerial = graphSerial;
    }

    /**
     * Check if this vertex is a member of a graph.
     * @return true if this vertex is a member of a graph.
     * Otherwise, false.
     */
    final boolean isGraphMember() {
        return edgeId >= 0;
    }

    /**
     *
     */
    final void leaveGraph() {
        edgeId = -1;
        graphSerial = -1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return edgeId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof GenericEdge)) {
            return false;
        }

        final GenericEdge edge = (GenericEdge) obj;
        return this.graphSerial == edge.graphSerial
               && this.edgeId == edge.edgeId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return "Edge (id = " + edgeId + ")";
    }
}
