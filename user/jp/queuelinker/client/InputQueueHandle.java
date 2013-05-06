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

import java.io.Serializable;

/**
 * This represents an input queue.
 */
public class InputQueueHandle implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4531742268362400416L;

    /**
     * The vertex that this queue is connected to.
     */
    private final Vertex vertex;

    /**
     * The name of this queue.
     */
    private final String queueName;

    /**
     * The ID of this queue.
     */
    private final int queueId;

    /**
     * @param vertex The vertex that this queue is connected to.
     * @param queueName The name of this queue.
     * @param queueId The ID of this queue.
     */
    InputQueueHandle(final Vertex vertex, final String queueName,
                     final int queueId) {
        this.vertex = vertex;
        this.queueName = queueName;
        this.queueId = queueId;
    }

    /**
     * @return The name of this input queue.
     */
    public final String getQueueName() {
        return queueName;
    }

    /**
     * @return The Queue ID.
     */
    public final int getQueueId() {
        return queueId;
    }

    /**
     * @return The vertex that this queue is connected to.
     */
    public final Vertex getVertex() {
        return vertex;
    }
}
