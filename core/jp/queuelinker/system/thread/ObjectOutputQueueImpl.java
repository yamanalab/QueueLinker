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
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import jp.queuelinker.module.OutputQueue;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;

/**
 * @param <O>
 */
public final class ObjectOutputQueueImpl<O extends Serializable> implements OutputQueue<O> {

    /**
     *
     */
    private int contextThreadLocalId;

    /**
     *
     */
    private final int logicalQueueId;

    /**
     *
     */
    private final String queueName;

    /**
     *
     */
    private final ThreadLocalOutputRouter router;

    /**
     *
     */
    private final PullModuleThreadUnit unit;

    /**
     *
     */
    private int outputCountStat;  // TOOD: This should be volatile.

    /**
     * @param contextThreadLocalId
     * @param logicalQueueId
     * @param queueName
     * @param router
     * @param unit
     */
    public ObjectOutputQueueImpl(final int contextThreadLocalId, final int logicalQueueId, final String queueName,
                                 final ThreadLocalOutputRouter router, final PullModuleThreadUnit unit) {
        this.contextThreadLocalId = contextThreadLocalId;
        this.logicalQueueId = logicalQueueId;
        this.queueName = queueName;
        this.router = router;
        this.unit = unit;
    }

    @Override
    public boolean add(final O item) {
        unit.checkManagementQueue();
        outputCountStat++;
        router.send(contextThreadLocalId, logicalQueueId, item);
        return true;
    }

    @Override
    public boolean offer(final O item) {
        unit.checkManagementQueue();
        outputCountStat++;
        router.send(contextThreadLocalId, logicalQueueId, item);
        return true;
    }

    @Override
    public boolean offer(final O item, final long arg1, final TimeUnit arg2) throws InterruptedException {
        unit.checkManagementQueue();
        outputCountStat++;
        router.send(contextThreadLocalId, logicalQueueId, item);
        return true;
    }

    @Override
    public void put(final O arg0) throws InterruptedException {
        unit.checkManagementQueue();
        outputCountStat++;
        router.send(contextThreadLocalId, logicalQueueId, arg0);
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean addAll(final Collection<? extends O> arg0) {
        unit.checkManagementQueue();
        for (O item : arg0) {
            outputCountStat++;
            router.send(contextThreadLocalId, logicalQueueId, item);
        }
        return true;
    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    @Override
    public int getQueueId() {
        return logicalQueueId;
    }

    /**
     * @param contextThreadLocalId
     */
    public void setThreadLocalId(final int contextThreadLocalId) {
        this.contextThreadLocalId = contextThreadLocalId;
    }

    @Override
    public String toString() {
        return queueName;
    }

    /**
     * @return
     */
    public long getOutputCountStat() {
        return outputCountStat;
    }
}
