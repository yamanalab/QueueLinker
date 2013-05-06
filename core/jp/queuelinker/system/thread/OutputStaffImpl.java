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

import jp.queuelinker.module.OutputQueue;
import jp.queuelinker.module.OutputStaff;

/**
 * @param <O>
 */
class OutputStaffImpl<O extends Serializable> implements OutputStaff<O> {

	private final ArrayList<ObjectOutputQueueImpl<?>> queueList = new ArrayList<ObjectOutputQueueImpl<?>>();

	private final HashMap<String, ObjectOutputQueueImpl<?>> queues = new HashMap<String, ObjectOutputQueueImpl<?>>();

	public OutputStaffImpl() {
	}

	@Override
	public synchronized OutputQueue<O> getDefaultOutputQueue() {
		return (OutputQueue<O>) queueList.get(0);
	}

	@Override
	public synchronized OutputQueue<?> getOutputQueue(final String queueName) {
		return queues.get(queueName);
	}

	@Override
	public synchronized OutputQueue<?> getOutputQueue(final int queueId) {
		if (queueId >= queueList.size()) {
			throw new IllegalStateException("The queue ID is too large: " + queueId);
		}
		return queueList.get(queueId);
	}

	@Override
	public synchronized int getOutputQueueCount() {
		return queueList.size();
	}

	synchronized void addOutputQueue(final String name, final int logicalQueueId, final ObjectOutputQueueImpl<?> newOutputQueue) {
		while (logicalQueueId >= queueList.size()) {
			queueList.add(null);
		}
		queueList.set(logicalQueueId, newOutputQueue);
		queues.put(name, newOutputQueue);
	}

	public long getOutputCount() {
		//TODO It seems to require a lock.
		long ret = 0;
		for (ObjectOutputQueueImpl queue : queues.values()) {
			ret += queue.getOutputCountStat();
		}
		return ret;
	}

	public void resetOutputCount() {
		// TODO Auto-generated method stub
	}
}
