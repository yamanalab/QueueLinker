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
import jp.queuelinker.module.base.SourceModule;

public class SourceModuleContext<O extends Serializable>
				extends PullContext implements OutputPullContext<O> {
	private final SourceModule<O> instance;

	private final QueueLinkerServiceImpl service;

	private final OutputStaffImpl<O> outputStaff;

	public SourceModuleContext(final PullModuleThreadUnit unit, final SourceModule<O> instance, final QueueLinkerServiceImpl service)
	{
		super(unit);

		this.instance = instance;
		this.service = service;
		this.outputStaff = new OutputStaffImpl<O>();
	}

	@Override
	void execute() {
		instance.execute(outputStaff, service);
	}

	void addNewOutputQueue(final String name, final int logicalQueueId, final int threadLocalQueueId) {
		unit.addThreadOutputMapping(this, logicalQueueId, threadLocalQueueId);

		ObjectOutputQueueImpl<O> newOutputQueue = new ObjectOutputQueueImpl<O>(
				threadLocalId, logicalQueueId, name, unit.getThreadLocalOutputRouter(), (PullModuleThreadUnit)unit
			);

		outputStaff.addOutputQueue(name, logicalQueueId, newOutputQueue);
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
	void initialize() {
		instance.initialize();
	}

	@Override
	public String toString() {
		return String.format("SourceModuleContext", instance.getClass().getName());
	}

	@Override
	public long getInputCount() {
		return 0;
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
