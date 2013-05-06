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

import jp.queuelinker.module.QueueLinkerService;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.MultipleInputQueuesPushModule;
import jp.queuelinker.system.annotation.NotThreadSafe;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;
import jp.queuelinker.system.unsafe.UnsafeUtil;

@NotThreadSafe
public class MultipleInputQueuesPushModuleContext<I extends Serializable, O extends Serializable>
extends ThreadContextBase implements InputPushContext<I>, OutputPushContext<O> {
	private final MultipleInputQueuesPushModule<I,O> instance;

	private final ThreadLocalOutputRouter outputRouter;

	private final ThreadLocalScheduler sched;

	private final LinkedList<InputItem> inputQueue = new LinkedList<InputItem>();

	private final QueueLinkerService service;

	private boolean executable;

	MultipleInputQueuesPushModuleContext(final PushModulesThreadUnit unit, final MultipleInputQueuesPushModule<I,O> instance, final QueueLinkerService service) {
		super(unit);

		this.instance = instance;
		this.outputRouter = unit.getThreadLocalOutputRouter();
		this.sched = unit.getThreadLocalScheduler();
		this.service = service;
	}

	@Override
	void execute() {
		final InputItem item = inputQueue.poll();
		inputCount++;
		final long start = UnsafeUtil.rdtsc();
		final O result = instance.execute(item.item, item.inputQueueLogicalId, service);
		cpuCycles += UnsafeUtil.rdtsc() - start;
		waitingCount--;

		if (result != null) {
			outputCount++;
			outputRouter.send(threadLocalId, 0, result);
		}

		if (inputQueue.isEmpty()) {
			sched.unExecutable(this);
			executable = false;
		}
	}

	@Override
	public void dispatchAccept(final I element, final DispatchRouteInformation route) {
		inputQueue.add(new InputItem(route.destQueueLogicalId, element));
		waitingCount++;

		if (!executable) {
			sched.executable(this);
			executable = true;
		}
	}

	public void addNewInputQueue(final int logicalQueueId) {

	}

	private class InputItem implements Serializable {
		private static final long serialVersionUID = -1589313548366851494L;

		public final int inputQueueLogicalId;

		public final I item;

		InputItem(final int inputQueueLogicalId, final I item) {
			this.inputQueueLogicalId = inputQueueLogicalId;
			this.item = item;
		}
	}

	@Override
	void initialize() {
		instance.initialize();
	}

	@Override
	public String toString() {
		return String.format("MultipleInputQueuesPushModuleContext (%s)", instance.getClass().getCanonicalName());
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
