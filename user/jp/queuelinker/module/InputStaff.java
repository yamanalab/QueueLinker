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
 * This class provides some methods to access to input queues.
 * @param <I>
 */
public interface InputStaff<I extends Serializable> {
    /**
     * Returns the default input queue.
     * @return The default input queue.
     */
    InputQueue<I> getDefaultInputQueue();

    /**
     * Returns an input queue that has the specified name.
     * @param queueName The name of the desired input queue.
     * @return An input queue that has the specified name.
     */
    InputQueue<?> getInput(String queueName);

    /**
     * Returns an input queue of which ID is the specified number.
     * @param queueId The ID of the desired input queue.
     * @return An input queue that has the specified ID.
     */
    InputQueue<?> getInput(int queueId);

    /**
     * Returns the number of the input queues this vertex has.
     * @return the number of the input queues.
     */
    int getInputQueueCount();

//    /**
//     * @param queueName
//     * @return
//     */
//    BinaryInputQueue getBinaryInput(String queueName);
}
