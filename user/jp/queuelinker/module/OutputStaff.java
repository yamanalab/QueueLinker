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

package jp.queuelinker.module;

import java.io.Serializable;

/**
 * This class provides some methods to access output queues.
 * @param <O>
 */
public interface OutputStaff<O extends Serializable> {

    /**
     * Returns the default output queue of this vertex.
     * @return The default output queue of this vertex.
     */
    OutputQueue<O> getDefaultOutputQueue();

    /**
     * Returns an output queue that has the specified name. Don't call this method frequently because it performs a
     * lookup.
     * @param queueName The name of an output queue to be found.
     * @return The found output queue.
     */
    OutputQueue<?> getOutputQueue(String queueName);

    /**
     * Returns an output queue of which ID is the specified value.
     * @param queueId An ID of a queue to be found.
     * @return The found output queue.
     */
    OutputQueue<?> getOutputQueue(int queueId);


    /**
     * Returns the number of output queues that the vertex has.
     * @return The number of output queues.
     */
    int getOutputQueueCount();
}
