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

import jp.queuelinker.system.annotation.NotThreadSafe;
import jp.queuelinker.system.util.ObjectLinkedList;
import jp.queuelinker.system.util.ObjectLinkedListElement;

/**
 * This is a thread local scheduler for a ThreadUnit and all methods must be called only from the thread of the unit.
 * Depending on the message flow, this decides when each context can run.
 */
@NotThreadSafe
public class ThreadLocalScheduler {
	private final ObjectLinkedList<ThreadContextBase> runnable = new ObjectLinkedList<ThreadContextBase>();

	private final ObjectLinkedList<ThreadContextBase> waiting = new ObjectLinkedList<ThreadContextBase>();

	public void addThreadContext(final ThreadContextBase context) {
		this.waiting.addFirst(context.getListElement());
	}

	public ThreadContextBase nextExecutableContext() {
		return runnable.peekFirst();
	}

	public void executable(final ThreadContextBase context) {
		final ObjectLinkedListElement<ThreadContextBase> element = context.getListElement();
		waiting.remove(element);
		runnable.addLast(element);
	}

	public void unExecutable(final ThreadContextBase context) {
		final ObjectLinkedListElement<ThreadContextBase> element = context.getListElement();
		runnable.remove(element);
		waiting.addLast(element);
	}

	public boolean executable() {
		return !runnable.isEmpty();
	}

	public void newInputAarrived() {
		// Currently, nothing to do
	}
}
