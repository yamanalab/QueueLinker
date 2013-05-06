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

import jp.queuelinker.system.annotation.NotThreadSafe;


@NotThreadSafe
public class RoundRobinDispatcher extends Dispatcher {

	private DispatchAccepter[] accepters;

	private DispatchRouteInformation[] routes;

	private int offset;

	public RoundRobinDispatcher(final boolean mutable) {
		super(mutable);
	}


	@Override
	public void dispatch(final Object element) {
		accepters[offset].dispatchAccept(element, routes[offset]);
		if (++offset >= accepters.length)
			offset = 0;
	}

	@Override
	void newAccepter(final DispatchAccepter accepter) {
		// TODO Auto-generated method stub

	}


	@Override
	void removeAccepter(final DispatchAccepter accepter) {
		// TODO Auto-generated method stub

	}
}
