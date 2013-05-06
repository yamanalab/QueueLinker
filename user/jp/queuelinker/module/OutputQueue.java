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
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @param <O>
 */
public interface OutputQueue<O extends Serializable> {
    /**
     * Returns the name of this queue.
     * @return The name of this queue.
     */
    String getQueueName();

    /**
     * Returns the queue ID. The returned id is unique in the input queues. And
     * it is the same with the logical queue id of the logical vertex.
     * @return The queue id.
     */
    int getQueueId();

    /**
     * Adds an item to this queue.
     * @param item An item to be added.
     * @return True if the item is added successfully. Or, false.
     */
    boolean add(O item);

    /**
     * @param item
     * @return
     */
    boolean offer(O item);

    /**
     * @param item
     * @param arg1
     * @param arg2
     * @return
     * @throws InterruptedException
     */
    boolean offer(O item, long arg1, TimeUnit arg2) throws InterruptedException;

    /**
     * @param arg0
     * @throws InterruptedException
     */
    void put(O arg0) throws InterruptedException;

    /**
     * @return
     */
    int remainingCapacity();

    /**
     * @param arg0
     * @return
     */
    boolean addAll(Collection<? extends O> arg0);
}
