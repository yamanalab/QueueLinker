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
import java.util.LinkedList;

import jp.queuelinker.module.base.FlowSwitcherModule;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;

public class SwitcherModuleContext <T extends Serializable>
				extends ThreadContextBase implements InputPushContext<T>, OutputPushContext<T>
{
	private final FlowSwitcherModule<T> instance;

	private final LinkedList<T> inputQueue = new LinkedList<>();

	private final ThreadLocalOutputRouter outputRouter;

	private final ThreadLocalScheduler sched;

	private boolean executable;

	public SwitcherModuleContext(final PushModulesThreadUnit unit, final FlowSwitcherModule<T> instance) {
		super(unit);

		this.instance = instance;
		this.outputRouter = unit.getThreadLocalOutputRouter();
		this.sched = unit.getThreadLocalScheduler();
	}

	@Override
	public void dispatchAccept(final T element, final DispatchRouteInformation route) {
		inputQueue.add(element);
		waitingCount++;

		if (!executable) {
			sched.executable(this);
			executable = true;
		}
	}

	@Override
	void initialize() {
		instance.initialize();
	}

	@Override
	void execute() {
		final T item = inputQueue.pollFirst();
		inputCount++;
		int result = instance.execute(item);
		outputRouter.send(this.threadLocalId, result, item);
		outputCount++;
		waitingCount--;

		if (inputQueue.isEmpty()) {
			sched.unExecutable(this);
			executable = false;
		}
	}

	@Override
	public String toString() {
		return String.format("SwitcherModuleContext (%s)", instance.getClass().getCanonicalName());
	}

	@Override
	public void snapShotModule(final ObjectOutputStream output) throws InvalidClassException, IOException, NotSerializableException {
		output.writeObject(instance);
	}

	@Override
	public ModuleBase getModuleBase() {
		return instance;
	}
}
