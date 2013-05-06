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

public class SingleDispatcher extends Dispatcher {

	private final DispatchAccepter accepter;

	private final DispatchRouteInformation route;

	public SingleDispatcher(final DispatchRouteInformation route, final boolean mutable) {
		super(mutable);

		this.accepter = route.accepter;
		this.route = route;
	}

	@Override
	void dispatch(final Object element) {
		accepter.dispatchAccept(element, route);
	}

	@Override
	void newAccepter(final DispatchAccepter accepter) {
		throw new RuntimeException("BUG: Not supported method was called.");
	}

	@Override
	void removeAccepter(final DispatchAccepter accepter) {
		throw new RuntimeException("BUG: Not supported method was called.");
	}

	public DispatchRouteInformation getRouteInformation() {
		return route;
	}
}
