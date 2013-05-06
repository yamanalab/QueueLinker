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
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

import jp.queuelinker.module.InputQueue;

public final class ObjectInputQueueImpl <I extends Serializable> implements InputQueue<I> {

	private final LinkedTransferQueue<I> queue = new LinkedTransferQueue<>();

	private long inputCountStat;

	private final String queueName;

	private final int queueId;

	public ObjectInputQueueImpl(final int queueId, final String queueName) {
	    this.queueId = queueId;
	    this.queueName = queueName;
	}

	@Override
	public boolean add(final I arg0) {
		inputCountStat++;
		return queue.add(arg0);
	}

	@Override
	public boolean contains(final Object obj) {
	    return queue.contains(obj);
	}

	@Override
	public int drainTo(final Collection<? super I> collection) {
	    return queue.drainTo(collection);
	}

	@Override
	public int drainTo(final Collection<? super I> arg0, final int arg1) {
		return queue.drainTo(arg0, arg1);
	}

	@Override
	public boolean offer(final I arg0) {
	    throw new RuntimeException("This is an input queue. Not Supported.");
	}

	@Override
	public boolean offer(final I arg0, final long arg1, final TimeUnit arg2)
			throws InterruptedException {
        throw new RuntimeException("This is an input queue. Not Supported.");
	}

	@Override
	public I poll(final long arg0, final TimeUnit arg1) throws InterruptedException {
		return queue.poll(arg0, arg1);
	}

	@Override
	public void put(final I arg0) throws InterruptedException {
	    throw new RuntimeException("This is an input queue. Not Supported.");
	}

	@Override
	public int remainingCapacity() {
		return queue.remainingCapacity();
	}

	@Override
	public boolean remove(final Object arg0) {
	    return queue.remove(arg0);
	}

	@Override
	public I take() throws InterruptedException {
		return queue.take();
	}

	@Override
	public I element() {
	    return queue.element();
	}

	@Override
	public I peek() {
		return queue.peek();
	}

	@Override
	public I poll() {
		return queue.poll();
	}

	@Override
	public I remove() {
		return queue.remove();
	}

	@Override
	public boolean addAll(final Collection<? extends I> c) {
	    throw new RuntimeException("This is an input queue. Not Supported.");
	}

	@Override
	public void clear() {
		queue.clear();
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
	    return queue.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public Iterator<I> iterator() {
	    return queue.iterator();
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
	    return queue.retainAll(c);
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Object[] toArray() {
	    return queue.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
	    return queue.toArray(a);
	}

	@Override
	public void reEnqueue(final I item) {
	    throw new RuntimeException("This is an input queue. Not Supported.");
	}

	@Override
	public String getOwnerThreadContextName() {
	    return "";
	}

	@Override
	public int getOwnerThreadContextId() {
		return -1;
	}

	@Override
	public String getQueueName() {
		return queueName;
	}

	@Override
	public int getQueueId() {
	    return queueId;
	}

	public long getInputCountStat() {
		return inputCountStat;
	}
}
