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

/**
 * This class represents a logical edge in a logical graph. A logical edge must
 * have a source vertex and a destination vertex.
 */
public final class LogicalEdge extends Edge {

    /**
     *
     */
    private static final long serialVersionUID = -4795804023212351488L;

    /**
     * The queue handle of the source vertex.
     */
    private final OutputQueueHandle srcVertexQueueHandle;

    /**
     * The queue handle of the destination vertex.
     */
    private final InputQueueHandle destVertexQueueHandle;

    /**
     * The property of this edge.
     */
    private final LogicalEdgeProperty property;

    /**
     * Creates a logical edge between two logical vertices.
     * @param srcVertex The vertex that is a source of this edge.
     * @param srcVertexQueueHandle The OutputQueueHandle of the source vertex.
     * @param destVertex The vertex that is a destination of this edge.
     * @param destVertexQueueHandle The InputQueueHandle of the destination vertex.
     * @param property The property of this edge.
     */
    LogicalEdge(final Vertex srcVertex,  final OutputQueueHandle srcVertexQueueHandle,
                final Vertex destVertex, final InputQueueHandle  destVertexQueueHandle,
                final LogicalEdgeProperty property) {
        super(srcVertex, srcVertexQueueHandle.getQueueId(), destVertex, destVertexQueueHandle.getQueueId());

        this.srcVertexQueueHandle = srcVertexQueueHandle;
        this.destVertexQueueHandle = destVertexQueueHandle;
        this.property = property;
    }

    /**
     * Returns the property of this edge.
     * @return The property of this edge.
     */
    public LogicalEdgeProperty getProperty() {
        return property;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Edge#getSrcQueueHandle()
     */
    @Override
    public OutputQueueHandle getSrcQueueHandle() {
        return srcVertexQueueHandle;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Edge#getDestQueueHandle()
     */
    @Override
    public InputQueueHandle getDestQueueHandle() {
        return destVertexQueueHandle;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Edge#getSrcQueueId()
     */
    @Override
    public int getSrcQueueId() {
        return srcVertexQueueHandle.getQueueId();
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Edge#getDestQueueId()
     */
    @Override
    public int getDestQueueId() {
        return destVertexQueueHandle.getQueueId();
    }
}
