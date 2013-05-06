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

import jp.queuelinker.system.thread.ThreadUnit;

public class DispatchRouteInformation {
	public final ThreadUnit srcUnit;

	public final int srcQueueThreadLocalId;

	public final DispatchAccepter accepter;

	public final int destQueueThreadLocalId;

	public final int destQueueLogicalId;

	public DispatchRouteInformation(final ThreadUnit srcUnit, final int srcQueueThreadLocalId,
			final DispatchAccepter<?> accepter, final int destQueueThreadLocalId, final int destQueueLogicalId) {
		this.srcUnit = srcUnit;
		this.srcQueueThreadLocalId = srcQueueThreadLocalId;
		this.accepter = accepter;
		this.destQueueThreadLocalId = destQueueThreadLocalId;
		this.destQueueLogicalId = destQueueLogicalId;
	}
}