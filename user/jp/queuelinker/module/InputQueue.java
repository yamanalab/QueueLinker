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
import java.util.concurrent.BlockingQueue;

/**
 * @param <I>
 */
public interface InputQueue<I extends Serializable> extends BlockingQueue<I> {
    /**
     * @param item
     */
    void reEnqueue(I item);

    /**
     * Returns the name of the thread context.
     * @return the name of the thread context.
     */
    String getOwnerThreadContextName();

    /**
     * Returns the ID of this thread context.
     * @return the ID of this thread context.
     */
    int getOwnerThreadContextId();

    /**
     * Returns the name of this queue.
     * @return The name of this queue.
     */
    String getQueueName();

    /**
     * Returns the ID of this input queue.
     * @return the ID of this input queue.
     */
    int getQueueId();
}
