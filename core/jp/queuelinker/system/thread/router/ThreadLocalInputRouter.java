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

package jp.queuelinker.system.thread.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.queuelinker.system.thread.InputContext;
import jp.queuelinker.system.thread.ThreadUnit;



public class ThreadLocalInputRouter {
	private final ThreadUnit unit;

	/**
	 * Contexts on this thread unit.
	 */
	private InputContext[] destContexts = new InputContext[0];

	public ThreadLocalInputRouter(final ThreadUnit unit) {
		this.unit = unit;
	}

	public void threadInput(final DispatchRouteInformation route, final Object item) {
		destContexts[route.destQueueThreadLocalId].dispatchAccept(item, route);
	}

	public void setInputRoute(final int destQueueThreadLocalId, final InputContext destContext) {
		destContexts[destQueueThreadLocalId] = destContext;
	}

	public void addNewInputQueue(final int inputQueueThreadLocalId) {
		if (destContexts.length <= inputQueueThreadLocalId) {
			destContexts = Arrays.copyOf(destContexts, inputQueueThreadLocalId + 1);
		}
	}

	public void removeInputQueue(final int inputQueueThreadLocalId) {
		if (destContexts[inputQueueThreadLocalId] == null)
			throw new IllegalArgumentException("BUG: Illegal queue id");

		destContexts[inputQueueThreadLocalId] = null;
		if (inputQueueThreadLocalId == destContexts.length - 1) {
			destContexts = Arrays.copyOf(destContexts, destContexts.length - 1);
		}
	}

	public List<Integer> findDestContexts(final InputContext<?> context) {
		ArrayList<Integer> ret = new ArrayList<>();
		for (int i = 0; i < destContexts.length; i++) {
			if (destContexts[i] == context) {
				ret.add(i);
			}
		}
		return ret;
	}

	public void swapDestContext(final InputContext<?> oldContext, final InputContext<?> newContext) {
		for (int i = 0; i < destContexts.length; i++) {
			if (destContexts[i] == oldContext) {
				destContexts[i] = newContext;
			}
		}
	}
}
