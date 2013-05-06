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

package jp.queuelinker.system.thread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import jp.queuelinker.module.InputQueue;
import jp.queuelinker.module.InputStaff;

public class InputStaffImpl<I extends Serializable> implements InputStaff<I> {
    private final ArrayList<ObjectInputQueueImpl<?>> queueList = new ArrayList<>();

    private final HashMap<String, ObjectInputQueueImpl<?>> queues = new HashMap<>();

    public InputStaffImpl() {

    }

    synchronized void addInputQueue(final String name, final int logicalId, final ObjectInputQueueImpl<I> newInputQueue) {
        while (logicalId >= queueList.size()) {
            queueList.add(null);
        }
        queueList.set(logicalId, newInputQueue);
        queues.put(name, newInputQueue);
    }

    @Override
    public InputQueue<I> getDefaultInputQueue() {
        return (InputQueue<I>) queueList.get(0);
    }

    @Override
    public synchronized InputQueue<?> getInput(final String queueName) {
        return queues.get(queueName);
    }

    @Override
    public synchronized InputQueue<?> getInput(final int queueId) {
        if (queueId >= queueList.size()) {
            throw new IllegalStateException("The queue ID is too large: " + queueId);
        }
        return queueList.get(queueId);
    }

    @Override
    public synchronized int getInputQueueCount() {
        return queueList.size();
    }

    public long getInputCount() {
        long ret = 0;
        for (ObjectInputQueueImpl queue : queues.values()) {
            ret += queue.getInputCountStat();
        }
        return ret;
    }

    public void resetInputCount() {
        // TODO Auto-generated method stub

    }

//    @Override
//    public BinaryInputQueue getBinaryInput(final String queueName) {
//        return null;
//    }
}
