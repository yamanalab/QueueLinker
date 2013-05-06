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
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.module.base.PullModule;
import jp.queuelinker.module.base.SourceModule;
import jp.queuelinker.system.annotation.CallableFromOutsideThreads;
import jp.queuelinker.system.thread.router.DispatchAccepter;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.Dispatcher;
import jp.queuelinker.system.thread.router.ThreadLocalInputRouter;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;

public class PullModuleThreadUnit extends ThreadUnit implements DispatchAccepter {
	/**
	 * The context to be executed in this unit.
	 * This unit has only one context because it does not manage multiple modules.
	 * TODO: Considering changing it as a final field.
	 */
	private PullContext context;

	private final String name;

	private ThreadUnitProcedure thread;

	private final ThreadLocalOutputRouter outputRouter = new ThreadLocalOutputRouter(this);

	private final LinkedBlockingQueue<ThreadManagementPacket> mngInput = new LinkedBlockingQueue<>();

	private final QueueLinkerServiceImpl service;

	private PullModuleThreadUnit(final String name, final int[] cpuAffinityId) {
		super(cpuAffinityId);
		this.name = name;
		this.thread = new ThreadUnitProcedure(name);
		this.service = new QueueLinkerServiceImpl(name, thread.getId());
	}

	static PullModuleThreadUnit createDataSourceUnit(final String name, final int[] cpuAffinityId, final SourceModule<?> instance) {
		PullModuleThreadUnit ret = new PullModuleThreadUnit(name, cpuAffinityId);
		ret.context = new SourceModuleContext(ret, instance, ret.service);
		return ret;
	}

	static PullModuleThreadUnit createPullModuleUnit(final String name, final int[] cpuAffinityId, final PullModule<?,?> instance) {
		PullModuleThreadUnit ret = new PullModuleThreadUnit(name, cpuAffinityId);
		ret.context = new PullModuleContext<>(ret, instance, ret.service);
		return ret;
	}

	static PullModuleThreadUnit createSelectorUnit(final String name, final int[] cpuAffinityId) throws IOException {
		PullModuleThreadUnit ret = new PullModuleThreadUnit(name, cpuAffinityId);
		ret.context = new SelectorContext<>(ret);
		return ret;
	}

	@Override @CallableFromOutsideThreads
	synchronized void startUnit() {
		if (threadState == ThreadState.INITIAL) {
			thread.start();
			threadState = ThreadState.RUNNING;
		}
	}

	@Override @CallableFromOutsideThreads
	synchronized void stopUnit() {
		if (threadState == ThreadState.RUNNING) {
			service.stopRequest();
			thread.interrupt();

			while (thread.isAlive()) {
				try {
					thread.join();
				} catch (InterruptedException e) {}
			}
			threadState = ThreadState.DEAD;
		}
	}

	@Override
	void suspendUnit() {
		if (threadState == ThreadState.RUNNING) {
			service.stopRequest();
			thread.interrupt();

			while (thread.isAlive()) {
				try {
					thread.join();
				} catch (InterruptedException e) {}
			}
			threadState = ThreadState.SUSPENDED;
		}
	}

	@Override
	void resumeUnit() {
		if (threadState == ThreadState.SUSPENDED) {
			service.cancelStopRequest();
			thread = new ThreadUnitProcedure(name);
			thread.start();
			threadState = ThreadState.RUNNING;
		}
	}

	@Override @CallableFromOutsideThreads
	synchronized void forceUnitStop() {
		if (threadState == ThreadState.RUNNING) {
			service.stopRequest();
			thread.interrupt();
			thread.stop();
			threadState = ThreadState.DEAD;
		}
	}

	private class ThreadUnitProcedure extends Thread {
		private final ThreadMXBean threadInformation;

		private volatile long lastCpuTime;
		private volatile long lastUserTime;

		ThreadUnitProcedure(final String name) {
			super(name);
			this.threadInformation = ManagementFactory.getThreadMXBean();
		}

		@Override
		public void run() {
			setCpuAffinity();

//			final WorkerServerEventHandler handler = QueueLinkerWorkerServer.getEventHandler();
			try {
				context.execute();
			} catch (Exception ex) {
				// TODO: add handler
				ex.printStackTrace();
			} catch (Error er) {
				// TODO: add handler
				er.printStackTrace();
			} catch (Throwable t) {
				// User defined exception?
				// TODO: add handler
				t.printStackTrace();
			}

			threadState = ThreadState.DEAD;

			synchronized (this) {
				lastCpuTime = threadInformation.getCurrentThreadCpuTime();
				lastUserTime = threadInformation.getCurrentThreadUserTime();
			}
		}

		long getThreadCpuTime() {
			long cpuTime = threadInformation.getThreadCpuTime(getId());
			synchronized (this) {
				if (cpuTime != -1) {
					lastCpuTime = cpuTime;
				}
				return lastCpuTime;
			}
		}

		long getThreadUserTime() {
			long userTime = threadInformation.getThreadUserTime(getId());
			synchronized (this) {
				if (userTime != -1) {
					lastUserTime = userTime;
				}
				return lastUserTime;
			}
		}
	}

	@Override
	public void dispatchAccept(final Object element, final DispatchRouteInformation route) {
		((InputPullContext)context).dispatchAccept(element, route);
	}

	public ThreadContextBase getThreadContext() {
		return context;
	}

	@Override
	void addBranch(final int srcQueueThreadLocalId, final Dispatcher dispatcher) {
		outputRouter.addBranch(srcQueueThreadLocalId, dispatcher);
	}

	@Override @CallableFromOutsideThreads
	int createInputQueue() {
		int nextId = getNextThreadLocalQueueId();
		return nextId;
	}

	@Override @CallableFromOutsideThreads
	int createOutputQueue() {
		int nextId = getNextThreadLocalQueueId();
		outputRouter.addNewOutputQueue(nextId);
		return nextId;
	}

	@Override
	public long getTotalCpuCycles() {
		return context.getCpuCycles();
	}

	@Override
	public long getCpuCycles(final long contextId) {
		if (contextId != context.getHostLocalId())
			throw new IllegalArgumentException("Illegal Context Id");

		return context.getCpuCycles();
	}

	public String getName() {
		return name;
	}

	@Override
	void addThreadInputRoute(final int inputQueueThreadLocalId, final InputContext<?> context, final int inputQueueLogicalId) {
		// Nothing to do because this unit has no input router.
	}

	@Override
	ThreadLocalOutputRouter getThreadLocalOutputRouter() {
		return outputRouter;
	}

	@Override
	ThreadLocalInputRouter getThreadLocalInputRouter() {
		return null;
	}

	@Override
	void addThreadOutputMapping(final OutputContext<?> output, final int outputLogicalId, final int outputQueueThreadLocalId) {
		outputRouter.setOutputMapping(output.getThreadLocalId(), outputLogicalId, outputQueueThreadLocalId);
	}

	@Override
	void deleteBranch(final int srcQueueThreadLocalId, final Dispatcher dispatcher) {

	}

	@Override
	void changeBranch(final int srcQueueThreadLocalId, final Dispatcher newDispatcher) {
		addManagementPacket(new ChangeRoutePacket(srcQueueThreadLocalId, newDispatcher));
	}

	@CallableFromOutsideThreads
	private void addManagementPacket(final ThreadManagementPacket packet) {
		mngInput.add(packet);
	}


	void checkManagementQueue() {
		while (!mngInput.isEmpty()) {
			final ThreadManagementPacket packet = mngInput.poll();
			packet.manipulate(this);
		}
	}

	@Override
	public String toString() {
		return String.format("PullModuleThreadUnit (Context: %s)", context.toString());
	}

	@Override
	public int getInputQueueTotalSize() {
		return context.getWaitingItemCount();
	}
}
