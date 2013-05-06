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

import jp.queuelinker.module.base.FlowSwitcherModule;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.module.base.MultipleInputQueuesPushModule;
import jp.queuelinker.module.base.MultipleOutputPushModule;
import jp.queuelinker.module.base.PullModule;
import jp.queuelinker.module.base.PushModule;
import jp.queuelinker.module.base.SinkModule;
import jp.queuelinker.module.base.SourceModule;
import jp.queuelinker.system.annotation.NotThreadSafe;
import jp.queuelinker.system.thread.router.DispatchAccepter;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.HashDispatcher;
import jp.queuelinker.system.thread.router.SingleDispatcher;

/**
 * This class controls the thread units on this VM.
 * The query information must be managed outside this class.
 */
@NotThreadSafe
public final class ThreadUnitController {

	private static ThreadUnitController instance;

	private ThreadUnitController() {
	}

	public static synchronized ThreadUnitController getInstance() {
		if (instance == null) {
			instance = new ThreadUnitController();
		}
		return instance;
	}

	public PushModulesThreadUnit createPushModulesUnit(final String name, final int[] cpuAffinityId, final boolean busyLoopMode) {
		return new PushModulesThreadUnit(name, cpuAffinityId, busyLoopMode);
	}

	public PullModuleThreadUnit createDataSourceModuleUnit(final String name, final int[] cpuAffinityId, final SourceModule instance) {
		return PullModuleThreadUnit.createDataSourceUnit(name, cpuAffinityId, instance);
	}

	public PullModuleThreadUnit createPullModuleUnit(final String name, final int[] cpuAffinityId, final PullModule instance) {
		return PullModuleThreadUnit.createPullModuleUnit(name, cpuAffinityId, instance);
	}

	public PullModuleThreadUnit createSelectorUnit(final String name, final int[] cpuAffinityId) throws IOException {
		return PullModuleThreadUnit.createSelectorUnit(name, cpuAffinityId);
	}

	public PushModuleContext<?,?> addPushThreadContext(final PushModulesThreadUnit unit, final PushModule<?,?> instance)
	{
		return unit.addPushThreadContext(instance);
	}


	public MultipleOutputPushModuleContext<?,?> addMultipleOutputPushThreadContext(
			final PushModulesThreadUnit unit, final MultipleOutputPushModule<?,?> instance)
	{
		return unit.addMultipleOutputThreadContext(instance);
	}


	public SinkModuleContext<?> addSinkThreadContext(final PushModulesThreadUnit unit, final SinkModule<?> instance)
	{
		return unit.addSinkThreadContext(instance);
	}

	public SwitcherModuleContext<?> addSwitcherContext(final PushModulesThreadUnit unit, final FlowSwitcherModule<?> instance) {
		return unit.addSwitcherContext(instance);
	}

	public MultipleInputQueuesPushModuleContext addMultipleInputQueuesPushModuleContext(
			final PushModulesThreadUnit unit, final MultipleInputQueuesPushModule instance)
	{
		return unit.addMutipleInputQueuesPushModuleContext(instance);
	}

	public void addNewInputQueue(final MultipleInputQueuesPushModuleContext context, final int logicalId) {
		((PushModulesThreadUnit)context.unit).addContextInputQueue(context, logicalId);
	}

	public void addNewInputQueue(final PullModuleContext context, final String name, final int logicalId, final int threadLocalId,  final ObjectInputQueueImpl<?> newInputQueue) {
		context.addNewInputQueue(name, logicalId, threadLocalId, newInputQueue);
	}

	public void addNewInputQueue(final SelectorContext context, final String name, final int logicalId, final int threadLocalId) {
		context.addNewInputQueue(name, logicalId, threadLocalId);
	}

	public void allowUsingNewOutputQueue(final SourceModuleContext context, final String name, final int logicalQueueId, final int threadLocalId) {
		context.addNewOutputQueue(name, logicalQueueId, threadLocalId);
	}

	public void allowUsingNewOutputQueue(final PullModuleContext context, final String name, final int logicalQueueId, final int threadLocalId) {
		context.addNewOutputQueue(name, logicalQueueId, threadLocalId);
	}

	public void addNewOutputQueue(final SelectorContext context, final String name, final int logicalQueueId, final int newThreadLocalId) {
		context.addNewOutputQueue(name, logicalQueueId, newThreadLocalId);
	}

	public DispatchRouteInformation addSingleDispatcher(
			final ThreadUnit srcUnit, final int srcQueueThreadLocalId,
			final DispatchAccepter accepter, final int destQueueThreadLocalId, final int destQueueLogicalId,
			final boolean mutable)
	{
		final DispatchRouteInformation routeInfo = new DispatchRouteInformation(srcUnit, srcQueueThreadLocalId, accepter, destQueueThreadLocalId, destQueueLogicalId);
		SingleDispatcher dispatcher = new SingleDispatcher(routeInfo, mutable);
		srcUnit.addBranch(srcQueueThreadLocalId, dispatcher);
		return routeInfo;
	}

	public DispatchRouteInformation[] addHashDispatcher(
			final ThreadUnit srcUnit, final int srcQueueThreadLocalId,
			final DispatchAccepter[] accepter, final int[] destQueueThreadLocalId, final int[] destQueueLogicalId,
			final boolean mutable, final HashCoder coder, final double[] partitioningRate)
	{
		DispatchRouteInformation[] routes = new DispatchRouteInformation[accepter.length];
		for (int i = 0; i < routes.length; i++)
			routes[i] = new DispatchRouteInformation(srcUnit, srcQueueThreadLocalId, accepter[i], destQueueThreadLocalId[i], destQueueLogicalId[i]);

		HashDispatcher dispatcher = new HashDispatcher(routes, partitioningRate, coder, mutable);
		srcUnit.addBranch(srcQueueThreadLocalId, dispatcher);
		return routes;
	}

	public void addThreadInputRoute(final ThreadUnit unit, final int inputQueueThreadLocalId,
			final InputContext context, final int inputQueueLogicalId) {
		unit.addThreadInputRoute(inputQueueThreadLocalId, context, inputQueueLogicalId);
	}

	public void addThreadOutputRoute(final ThreadUnit unit, final OutputContext<?> context, final int outputQueueLogicalId, final int outputQueueThreadLocalid) {
		unit.addThreadOutputMapping(context, outputQueueLogicalId, outputQueueThreadLocalid);
	}

	public void removeRoute(final DispatchRouteInformation subBranchRoute) {

	}

	public void startThreadUnit(final ThreadUnit unit) {
		unit.startUnit();
	}

	public void stopThreadUnit(final ThreadUnit threadUnit) {
		threadUnit.stopUnit();
	}



	public int createThreadLocalInputQueue(final ThreadUnit unit) {
		return unit.createInputQueue();
	}

	public int createThreadLocalOutputQueue(final ThreadUnit unit) {
		return unit.createOutputQueue();
	}

	public void moveContext(final PushModulesThreadUnit srcUnit, final InputContext<?> movingContext, final TransferContext<?, ?> transferContext,
			final PushModulesThreadUnit destUnit, final int destContextId, final SingleDispatcher[] transferDispatcher, final SingleDispatcher[] destUnitDispatcher) {
		srcUnit.moveContext(movingContext, transferContext, destUnit, destContextId, transferDispatcher, destUnitDispatcher);
	}

	public TransferContext<?, ?> addTransferContext(final PushModulesThreadUnit unit) {
		return unit.addTransferContext();
	}

	public void changeDispatcher(final ThreadUnit threadUnit, final int outputQueueThreadLocalId, final SingleDispatcher dispatcher) {
		threadUnit.changeBranch(outputQueueThreadLocalId, dispatcher);
	}

	public void suspendThreadUnit(final ThreadUnit unit) {
		unit.suspendUnit();
	}

	public void resumeThreadUnit(final ThreadUnit unit) {
		unit.resumeUnit();
	}

//	public void stopAllUnits() {
//		for (ThreadUnitSet unitSet : unitSets.values())
//			unitSet.startAllWaitingUnits();
//	}
//
//	public void startWaitingUnits() {
//		for (ThreadUnitSet unitSet : unitSets.values())
//			unitSet.startAllWaitingUnits();
//	}
}
