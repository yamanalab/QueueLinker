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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jp.queuelinker.system.thread.router.Dispatcher;
import jp.queuelinker.system.thread.router.ThreadLocalInputRouter;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;
import jp.queuelinker.system.unsafe.Affinity;

/**
 * A management unit of a thread. Public methods are called from an outside thread to control this unit but they are not
 * reentrant.
 */
public abstract class ThreadUnit {

    protected int[] affinityCpuId;

    protected final long hostLocalId;

    private static AtomicLong nextHostLocalId = new AtomicLong();

    private final AtomicInteger nextThreadLocalQueueId = new AtomicInteger();

    private final AtomicInteger nextThreadLocalContextId = new AtomicInteger();

    protected enum ThreadState {
        INITIAL, RUNNING, SUSPENDED, DEAD
    };

    protected volatile ThreadState threadState = ThreadState.INITIAL;

    public ThreadUnit(final int[] affinityCpuId) {
        this.affinityCpuId = affinityCpuId;
        this.hostLocalId = getNextHostLocalId();
    }

    /**
     * Returns the Affinity CPU IDs which this thread runs on
     * @return The CPU IDs which this thread runs on.
     */
    public int[] getAffinityCpuId() {
        return Arrays.copyOf(affinityCpuId, affinityCpuId.length);
    }

    protected boolean setCpuAffinity() {
        if (affinityCpuId != null) {
            int ret = Affinity.sched_setaffinity(affinityCpuId);
            if (ret != 0) {
                return false;
            }
        }
        return true;
    }

    public long getHostLocalId() {
        return hostLocalId;
    }

    private long getNextHostLocalId() {
        return nextHostLocalId.getAndIncrement();
    }

    protected int getNextThreadLocalQueueId() {
        // TODO This counter may overflow. Reuse the counter.
        return nextThreadLocalQueueId.getAndIncrement();
    }

    protected int getNextThreadLocalContextId() {
        return nextThreadLocalContextId.getAndIncrement();
    }

    abstract void startUnit();

    abstract void stopUnit();

    abstract void suspendUnit();

    abstract void resumeUnit();

    abstract void forceUnitStop();

    /**
     * Returns the total CPU Cycles used by this ThreadUnit.
     * @return
     */
    public abstract long getTotalCpuCycles();

    public abstract long getCpuCycles(long contextId);

    public abstract int getInputQueueTotalSize();

    abstract void addBranch(int srcQueueThreadLocalId, Dispatcher dispatcher);

    abstract void deleteBranch(int srcQueueThreadLocalId, Dispatcher dispatcher);

    abstract void changeBranch(int srcQueueThreadLocalId, Dispatcher newDispatcher);

    abstract int createInputQueue();

    /**
     * Creates an output queue.
     * @return
     */
    abstract int createOutputQueue();

    abstract void addThreadInputRoute(int inputQueueThreadLocalId, InputContext<?> context, int inputQueueLogicalId);

    abstract void addThreadOutputMapping(OutputContext<?> context, int outputLogicalId, int outputQueueThreadLocalId);

    abstract ThreadLocalOutputRouter getThreadLocalOutputRouter();

    abstract ThreadLocalInputRouter getThreadLocalInputRouter();

}
