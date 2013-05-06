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

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;


public final class TransferContext <I extends Serializable, O extends Serializable>
extends ThreadContextBase implements InputContext<I>, OutputContext<O> {

	private final ThreadLocalOutputRouter outputRouter;

	public TransferContext(final ThreadUnit unit) {
		super(unit);

		this.outputRouter = unit.getThreadLocalOutputRouter();
	}

	@Override
	public void dispatchAccept(final I element, final DispatchRouteInformation route) {
		outputRouter.send(threadLocalId, route.destQueueLogicalId, element);
	}

	@Override
	void initialize() {
	}

	@Override
	void execute() {
		throw new RuntimeException("BUG: TransferContext.execute() was called.");
	}

	@Override
	public void snapShotModule(final ObjectOutputStream output)
	            throws InvalidClassException, IOException, NotSerializableException {

	}

	@Override
	public ModuleBase getModuleBase() {
		return null;
	}
}
