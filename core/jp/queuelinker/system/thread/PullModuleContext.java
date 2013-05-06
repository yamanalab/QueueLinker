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
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.PullModule;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;


public class PullModuleContext <I extends Serializable, O extends Serializable>
					extends PullContext implements InputPullContext<I>, OutputPullContext<O> {

	private final PullModule<I, O> instance;

	private final QueueLinkerServiceImpl service;

	private final InputStaffImpl<I> inputStaff;

	private final OutputStaffImpl<O> outputStaff;

//	private ObjectInputQueueImpl[] inputs = new ObjectInputQueueImpl[0];

	private final CopyOnWriteArrayList<ObjectInputQueueImpl<I>> inputs = new CopyOnWriteArrayList<>();

	public PullModuleContext(final PullModuleThreadUnit unit, final PullModule<I, O> instance, final QueueLinkerServiceImpl service) {
		super(unit);

		this.instance = instance;
		this.service = service;

		this.inputStaff = new InputStaffImpl<I>();
		this.outputStaff = new OutputStaffImpl<O>();
	}

	@Override
	void execute() {
		instance.execute(inputStaff, outputStaff, service);
	}

	void addNewOutputQueue(final String name, final int logicalQueueId, final int threadLocalQueueId) {
		unit.addThreadOutputMapping(this, logicalQueueId, threadLocalQueueId);

		ObjectOutputQueueImpl<O> newOutputQueue = new ObjectOutputQueueImpl<O>(
				threadLocalId, logicalQueueId, name, unit.getThreadLocalOutputRouter(), (PullModuleThreadUnit)unit
			);

		outputStaff.addOutputQueue(name, logicalQueueId, newOutputQueue);
	}

	void addNewInputQueue(final String name, final int logicalQueueId, final int threadLocalId, final ObjectInputQueueImpl<I> newInputQueue) {
		inputStaff.addInputQueue(name, logicalQueueId, newInputQueue);

		while (inputs.size() <= threadLocalId)
			inputs.add(null);
		inputs.set(threadLocalId, newInputQueue);

//		if (inputs.length <= threadLocalId)
//			inputs = Arrays.copyOf(inputs, threadLocalId + 1);
//		inputs[threadLocalId] = newInputQueue;
	}

	@Override
	public void dispatchAccept(final I element, final DispatchRouteInformation route) {
//		inputs[route.destQueueThreadLocalId].add(element);
		inputs.get(route.destQueueThreadLocalId).add(element);
	}

	@Override
	public long getOutputCount() {
		return outputStaff.getOutputCount();
	}

	@Override
	public void resetOutputCount() {
		outputStaff.resetOutputCount();
	}

	@Override
	public long getInputCount() {
		return inputStaff.getInputCount();
	}

	@Override
	public void resetInputCount() {
		inputStaff.resetInputCount();
	}

	@Override
	void initialize() {
		instance.initialize();
	}

	@Override
	public String toString() {
		return String.format("PullModuleContext (%s)", instance.getClass().getCanonicalName());
	}

	@Override
	public void snapShotModule(final ObjectOutputStream output) throws InvalidClassException, IOException, NotSerializableException {
		output.writeObject(instance);
	}

	@Override
	public ModuleBase getModuleBase() {
		return instance;
	}

	@Override
	public int getWaitingItemCount() {
		int ret = 0;
		Iterator<ObjectInputQueueImpl<I>> iter = inputs.iterator();
		while (iter.hasNext()) {
			ObjectInputQueueImpl<I> list = iter.next();
			ret += list.size();
		}
		return ret;
	}
}
