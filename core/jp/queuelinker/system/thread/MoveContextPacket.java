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

import jp.queuelinker.system.thread.router.SingleDispatcher;

public class MoveContextPacket implements ThreadManagementPacket {
	private final InputContext<?> movingContext;

	private final TransferContext<?, ?> transferContext;

	private final PushModulesThreadUnit destUnit;

	private final int destContextId;

	private final SingleDispatcher[] transferDispatcher;

	private final SingleDispatcher[] destUnitDispatcher;

	public MoveContextPacket(final InputContext<?> movingContext, final TransferContext<?, ?> transferContext,
	                         final PushModulesThreadUnit destUnit, final int destContextId,
	                         final SingleDispatcher[] transferDispatcher, final SingleDispatcher[] destUnitDispatcher) {
		this.movingContext = movingContext;
		this.transferContext = transferContext;
		this.destUnit = destUnit;
		this.destContextId = destContextId;
		this.transferDispatcher = transferDispatcher;
		this.destUnitDispatcher = destUnitDispatcher;
	}

	@Override
	public void manipulate(final ThreadUnit unit) {
		unit.getThreadLocalInputRouter().swapDestContext(movingContext, transferContext);

		for (int i = 0; i < transferDispatcher.length; i++) {
			final int outputQueue = transferDispatcher[i].getRouteInformation().srcQueueThreadLocalId;
			unit.getThreadLocalOutputRouter().clearBranch(outputQueue);
			unit.getThreadLocalOutputRouter().addBranch(outputQueue, transferDispatcher[i]);
		}

		destUnit.activateMovedContext(destContextId, destUnitDispatcher);
	}
}
