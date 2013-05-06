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

import jp.queuelinker.module.CustomHashCode;
import jp.queuelinker.module.CustomSerializer;

/**
 * An instance of this class represents an output queue.
 */
public final class OutputQueueHandle implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4197448968829350695L;

    /**
     * The vertex that puts items into this queues.
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
     *
     */
    private CustomSerializer<?> serializer;

    /**
     *
     */
    private CustomHashCode hashCoder;

    /**
     * Creates an output queue handle.
     * @param vertex The vertex that is a source of this output queue.
     * @param queueName The name of this queue.
     * @param queueId The ID of this queue.
     */
    OutputQueueHandle(final Vertex vertex, final String queueName, final int queueId) {
        this.vertex = vertex;
        this.queueName = queueName;
        this.queueId = queueId;
    }

    /**
     * Returns the name of this queue.
     * @return The name of this queue.
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Returns the ID of this queue.
     * @return The ID of this queue.
     */
    public int getQueueId() {
        return queueId;
    }

    /**
     * @param serializer A new serializer for this output queue.
     */
    public void setCustomSerializer(final CustomSerializer<?> serializer) {
        this.serializer = serializer;
    }

    /**
     * @return The serializer of this output queue.
     */
    public CustomSerializer<?> getCustomSerializer() {
        return serializer;
    }

    /**
     * @param hashCoder A new hash coder of this output queue.
     */
    public void setCustomHashCoder(final CustomHashCode hashCoder) {
        this.hashCoder = hashCoder;
    }

    /**
     * Returns the hash coder of this output queue.
     * @return The hash coder of this output queue.
     */
    public CustomHashCode getCustomHashCoder() {
        return hashCoder;
    }

    /**
     * Returns the logical vertex that is a source of this output queue.
     * @return The logical vertex that is a source of this output queue.
     */
    public Vertex getVertex() {
        return vertex;
    }
}
