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

import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.annotation.CallableFromOutsideThreads;
import jp.queuelinker.system.util.ObjectLinkedListElement;

public abstract class ThreadContextBase {
	private final ObjectLinkedListElement<ThreadContextBase> myElement;

	protected final long hostLocalId;

	protected int threadLocalId;

	/**
	 * The ThreadUnit that this context belongs to.
	 */
	protected final ThreadUnit unit;

	private static long nextHostLocalId;

	protected volatile long cpuCycles;

	protected volatile int inputCount;

	protected volatile int outputCount;

	protected volatile int waitingCount;

	public ThreadContextBase(final ThreadUnit unit) {
		this.myElement = new ObjectLinkedListElement<ThreadContextBase>(this);
		this.hostLocalId = nextHostLocalId();
		this.unit = unit;
		this.threadLocalId = unit.getNextThreadLocalContextId();
	}

	ObjectLinkedListElement<ThreadContextBase> getListElement() {
		return myElement;
	}

	public int getThreadLocalId() {
		return threadLocalId;
	}

	@CallableFromOutsideThreads
	public long getHostLocalId() {
		return hostLocalId;
	}

	@CallableFromOutsideThreads
	public ThreadUnit getThreadUnit() {
		return unit;
	}

	private synchronized static long nextHostLocalId() {
		return nextHostLocalId++;
	}

	@CallableFromOutsideThreads
	public long getCpuCycles() {
		return cpuCycles;
	}

	@CallableFromOutsideThreads
	public void resetCpuCyelces() {
		cpuCycles = 0;
	}

	@CallableFromOutsideThreads
	public long getInputCount() {
		return inputCount;
	}

	@CallableFromOutsideThreads
	public long getOutputCount() {
		return outputCount;
	}

	@CallableFromOutsideThreads
	public void resetInputCount() {
		inputCount = 0;
	}

	@CallableFromOutsideThreads
	public void resetOutputCount() {
		outputCount = 0;
	}

	public int getWaitingItemCount() {
		return waitingCount;
	}

	abstract void initialize();

	abstract void execute();

	public abstract void snapShotModule(ObjectOutputStream output) throws InvalidClassException, IOException, NotSerializableException;

	public abstract ModuleBase getModuleBase();

}
