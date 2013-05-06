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

import jp.queuelinker.system.util.GenericEdge;

/**
 * This class represents a physical edge.
 */
public class PhysicalEdge extends GenericEdge<PhysicalVertex> {

    /**
     *
     */
    private static final long serialVersionUID = -7099365142111894337L;

    /**
     * @param srcVertex The physical vertex that is a source of this edge.
     * @param srcQueueId The ID of the source queue.
     * @param destVertex The physical vertex that is a destination of this edge.
     * @param destQueueId The ID of the destination queue.
     */
    public PhysicalEdge(final PhysicalVertex srcVertex, final int srcQueueId,
                        final PhysicalVertex destVertex, final int destQueueId) {
        super(srcVertex, srcQueueId, destVertex, destQueueId);
    }

    /**
     * Returns the physical vertex that is a source of this edge.
     * @return The vertex that is a source of this edge.
     */
    public final PhysicalVertex getSrcPhysicalVertex() {
        return super.getSrcVertex();
    }

    /**
     * Returns the physical vertex that is a destination of this edge.
     * @return The physical vertex that is a destination of this edge.
     */
    public final PhysicalVertex getDestPhysicalVertex() {
        return super.getDestVertex();
    }
}
