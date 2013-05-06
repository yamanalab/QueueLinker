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

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.module.base.FlowSwitcherModule;
import jp.queuelinker.module.base.MultipleInputQueuesPushModule;
import jp.queuelinker.module.base.MultipleOutputPushModule;
import jp.queuelinker.module.base.PushModule;
import jp.queuelinker.module.base.SinkModule;
import jp.queuelinker.system.annotation.CallableFromOutsideThreads;
import jp.queuelinker.system.thread.router.DispatchAccepter;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.Dispatcher;
import jp.queuelinker.system.thread.router.SingleDispatcher;
import jp.queuelinker.system.thread.router.ThreadCommunicationPacket;
import jp.queuelinker.system.thread.router.ThreadLocalInputRouter;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;

//import queuelinker.system.server.QueueLinkerWorkerServer;
//import queuelinker.system.server.WorkerServerEventHandler;

public final class PushModulesThreadUnit extends ThreadUnit implements DispatchAccepter {

	private final boolean busyLoopMode;

	private final String name;

	private ThreadUnitProcedure thread;

	private final LinkedBlockingQueue<ThreadCommunicationPacket> input = new LinkedBlockingQueue<ThreadCommunicationPacket>();

	private final LinkedBlockingQueue<ThreadManagementPacket> mngInput = new LinkedBlockingQueue<ThreadManagementPacket>();

	private final ThreadLocalScheduler sched = new ThreadLocalScheduler();

	private final ThreadLocalInputRouter inputRouter = new ThreadLocalInputRouter(this);

	private final ThreadLocalOutputRouter outputRouter = new ThreadLocalOutputRouter(this);

	private volatile boolean stopRequested;

	private final ConcurrentHashMap<Long, ThreadContextBase> contexts = new ConcurrentHashMap<Long, ThreadContextBase>();

	PushModulesThreadUnit(final String name, final int[] cpuAffinityId, final boolean busyLoopMode) {
		super(cpuAffinityId);
		this.name = name;
		this.thread = new ThreadUnitProcedure(name);
		this.busyLoopMode = busyLoopMode;
	}

	@CallableFromOutsideThreads
	public boolean isBusyLoopMode() {
		return busyLoopMode;
	}

	@Override @CallableFromOutsideThreads
	synchronized void startUnit() {
		if (threadState == ThreadState.INITIAL) {
			threadState = ThreadState.RUNNING;
			thread.start();
		}
	}

	@Override @CallableFromOutsideThreads
	synchronized void stopUnit() {
		if (threadState == ThreadState.RUNNING) {
			stopRequested = true;
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
			stopRequested = true;
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
			stopRequested = false;
			thread = new ThreadUnitProcedure(name);
			thread.start();
			threadState = ThreadState.RUNNING;
		}
	}

	@Override @CallableFromOutsideThreads
	synchronized void forceUnitStop() {
		if (threadState == ThreadState.RUNNING) {
			stopRequested = true;
			thread.interrupt();
			thread.stop();
			threadState = ThreadState.DEAD;
		}
	}

	@CallableFromOutsideThreads
	private void addManagementPacket(final ThreadManagementPacket packet) {
		mngInput.add(packet);

		if (!busyLoopMode)
			thread.interrupt();
	}

	@Override @CallableFromOutsideThreads
	public void dispatchAccept(final Object element, final DispatchRouteInformation route) {
		ThreadCommunicationPacket packet = new ThreadCommunicationPacket(route, element);
		input.add(packet);
		sched.newInputAarrived();
	}

	@CallableFromOutsideThreads
	synchronized <I extends Serializable, O extends Serializable> PushModuleContext<I,O>
			addPushThreadContext(final PushModule<I,O> instance)
	{
		QueueLinkerServiceImpl service = new QueueLinkerServiceImpl(instance, thread.getId());
		PushModuleContext<I,O> context = new PushModuleContext<I,O>(this, instance, service);
		contexts.put(context.getHostLocalId(), context);
		addManagementPacket(new NewContextAddPacket(context));
		return context;
	}

	@CallableFromOutsideThreads
	synchronized <I extends Serializable, O extends Serializable> MultipleOutputPushModuleContext<I,O>
			addMultipleOutputThreadContext(final MultipleOutputPushModule<I,O> instance)
	{
		QueueLinkerServiceImpl service = new QueueLinkerServiceImpl(instance, thread.getId());
		MultipleOutputPushModuleContext<I,O> context =
				new MultipleOutputPushModuleContext<I, O>(this, instance, service);

		contexts.put(context.getHostLocalId(), context);
		addManagementPacket(new NewContextAddPacket(context));
		return context;
	}

	@CallableFromOutsideThreads
	synchronized <I extends Serializable> SinkModuleContext<I> addSinkThreadContext(final SinkModule<I> instance)
	{
		QueueLinkerServiceImpl service = new QueueLinkerServiceImpl(instance, thread.getId());
		SinkModuleContext<I> context = new SinkModuleContext<I>(this, instance, service);
		contexts.put(context.getHostLocalId(), context);
		addManagementPacket(new NewContextAddPacket(context));
		return context;
	}

	@CallableFromOutsideThreads
	synchronized <I extends Serializable, O extends Serializable> MultipleInputQueuesPushModuleContext<I,O>
			addMutipleInputQueuesPushModuleContext(final MultipleInputQueuesPushModule<I, O> instance)
	{
		QueueLinkerServiceImpl service = new QueueLinkerServiceImpl(instance, thread.getId());
		MultipleInputQueuesPushModuleContext<I,O> context
							= new MultipleInputQueuesPushModuleContext<>(this, instance, service);

		contexts.put(context.getHostLocalId(), context);
		addManagementPacket(new NewContextAddPacket(context));

		return context;
	}


	synchronized <T extends Serializable> SwitcherModuleContext<T> addSwitcherContext(final FlowSwitcherModule<T> instance) {
		SwitcherModuleContext<T> context = new SwitcherModuleContext<>(this, instance);

		contexts.put(context.getHostLocalId(), context);
		addManagementPacket(new NewContextAddPacket(context));

		return context;
	}

	TransferContext<?,?> addTransferContext() {
		TransferContext<?,?> context = new TransferContext<>(this);
		return context;
	}

	@Override @CallableFromOutsideThreads
	void addBranch(final int srcQueueThreadLocalId, final Dispatcher dispatcher) {
		addManagementPacket(new NewBranchPacket(srcQueueThreadLocalId, dispatcher));
	}

	@Override
	void deleteBranch(final int srcQueueThreadLocalId, final Dispatcher dispatcher) {
		// TODO Auto-generated method stub
	}

	@Override @CallableFromOutsideThreads
	void changeBranch(final int srcQueueThreadLocalId, final Dispatcher newDispatcher) {
		addManagementPacket(new ChangeRoutePacket(srcQueueThreadLocalId, newDispatcher));
	}


	@Override @CallableFromOutsideThreads
	public long getTotalCpuCycles() {
		long ret = 0;
		for (ThreadContextBase context : contexts.values()) {
			ret += context.getCpuCycles();
		}
		return ret;
	}

	@Override @CallableFromOutsideThreads
	public long getCpuCycles(final long contextId) {
		if (!contexts.containsKey(contextId))
			throw new IllegalArgumentException("BUG: Illegal context id.");
		return contexts.get(contexts).getCpuCycles();
	}

	public String getName() {
		return name;
	}

	@Override @CallableFromOutsideThreads
	int createInputQueue() {
		final int newQueueId = getNextThreadLocalQueueId();
		addManagementPacket(new NewInputQueuePacket(newQueueId));
		return newQueueId;
	}

	@Override @CallableFromOutsideThreads
	int createOutputQueue() {
		final int newQueueId = getNextThreadLocalQueueId();
		addManagementPacket(new NewOutputQueuePacket(newQueueId));
		return newQueueId;
	}

	@CallableFromOutsideThreads
	void moveContext(final InputContext<?> movingContext, final TransferContext<?, ?> transferContext,
			final PushModulesThreadUnit destUnit, final int destContextId, final SingleDispatcher[] transferDispatcher, final SingleDispatcher[] destUnitDispatcher) {
		addManagementPacket(new MoveContextPacket(movingContext, transferContext, destUnit, destContextId, transferDispatcher, destUnitDispatcher));
	}

	@Override
	void addThreadInputRoute(final int inputQueueThreadLocalId, final InputContext<?> context, final int inputQueueLogicalId)
	{
		addManagementPacket(new NewInputRoutePacket(inputQueueThreadLocalId, context, inputQueueLogicalId));
	}

	@Override
	void addThreadOutputMapping(final OutputContext<?> context, final int outputLogicalId, final int outputThreadLocalId) {
		addManagementPacket(new OutputMappingPacket(context, outputLogicalId, outputThreadLocalId));
	}

	ThreadLocalScheduler getThreadLocalScheduler() {
		return sched;
	}

	void addContextInputQueue(final MultipleInputQueuesPushModuleContext<?,?> context, final int logicalId) {
		addManagementPacket(new AddContextInputQueuePacket(context, logicalId));
	}

	@Override
	ThreadLocalOutputRouter getThreadLocalOutputRouter() {
		return outputRouter;
	}

	@Override
	ThreadLocalInputRouter getThreadLocalInputRouter() {
		return inputRouter;
	}

	public void addThreadContext(final ThreadContextBase context) {
		contexts.put((long)context.threadLocalId, context);
	}

	public void removeContext(final ThreadContextBase context) {
		// TODO Auto-generated method stub
		contexts.remove(context.threadLocalId);
	}

	public void activateMovedContext(final int destContextId, final SingleDispatcher[] destUnitDispatcher) {
		for (int i = 0; i < destUnitDispatcher.length; i++) {
			outputRouter.changeRoute(destUnitDispatcher[i].getRouteInformation().srcQueueThreadLocalId, destUnitDispatcher[i]);
		}
	}

	@Override @CallableFromOutsideThreads
	public int getInputQueueTotalSize() {
		return input.size();
	}

	@Override
	public String toString() {
		if (affinityCpuId != null) {
			StringBuilder builder = new StringBuilder("PushModulesThreadUnit ");
			for (ThreadContextBase context : contexts.values()) {
				builder.append(context.toString());
			}
			builder.append(" on CPU ");
			for (int i = 0; i < affinityCpuId.length; i++)
				builder.append(affinityCpuId[i] + " ");
			return builder.toString();
		}
		else {
			return "PushModulesThreadUnit: " + contexts.size() + " contexts";
		}
	}

	class ThreadUnitProcedure extends Thread {
//		final WorkerServerEventHandler handler;

		ThreadUnitProcedure(final String name) {
			super(name);
//			this.handler = QueueLinkerWorkerServer.getEventHandler();
		}

		@Override
		public void run() {
			setCpuAffinity();
			procManagementPacket();

			if (busyLoopMode)
				busyLoopWait();
			else
				noBusyLoopWait();
		}

		private void busyLoopWait() {
			ThreadCommunicationPacket packet = null;

			while (!stopRequested) {
				while ((packet = input.poll()) == null) {
					if (stopRequested)
						return;
					procManagementPacket();
				}
				procManagementPacket();
				procInput(packet);
			}
		}

		private void noBusyLoopWait() {
			ThreadCommunicationPacket packet;

			while (!stopRequested) {
				try {
					packet = input.take();
					procInput(packet);
				} catch (InterruptedException e) { }
				procManagementPacket();
			}
		}

		private void procInput(final ThreadCommunicationPacket packet) {
			inputRouter.threadInput(packet.route, packet.element);
			while (sched.executable()) {
				final ThreadContextBase context = sched.nextExecutableContext();
				try {
					context.execute();
				} catch (Exception ex) {
					ex.printStackTrace();
					stopRequested = true;
					return;
					// TODO add handler
				} catch (Error er) {
					er.printStackTrace();
					stopRequested = true;
					return;
					// TODO add handler
				} catch (Throwable t) {
					t.printStackTrace();
					stopRequested = true;
					return;
					// User defined exception?
					// TODO add handler
				}
			}
		}

		private void procManagementPacket() {
			while (!mngInput.isEmpty()) {
				ThreadManagementPacket packet = mngInput.poll();
				packet.manipulate(PushModulesThreadUnit.this);
			}
		}
	}
}
