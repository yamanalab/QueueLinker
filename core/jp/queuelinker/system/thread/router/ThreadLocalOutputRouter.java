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

package jp.queuelinker.system.thread.router;

import java.util.Arrays;

import jp.queuelinker.system.annotation.NotThreadSafe;
import jp.queuelinker.system.thread.ThreadUnit;

/**
 * This is a thread local router for a ThreadUnit and all methods must be called
 * only from the thread of the unit.
 */
@NotThreadSafe
public class ThreadLocalOutputRouter {
    private final ThreadUnit unit;

    private Dispatcher[][] dispatchers = new Dispatcher[0][0];

    private int[][] queueIdMapping = new int[0][0];

    public ThreadLocalOutputRouter(final ThreadUnit unit) {
        this.unit = unit;
    }

    public void send(final int contextThreadLocalId, final int logicalQueueId, final Object item) {
        final int threadLocalId = queueIdMapping[contextThreadLocalId][logicalQueueId];
        final Dispatcher[] dispatcher = dispatchers[threadLocalId];

        for (Dispatcher d : dispatcher) {
            if (!d.isMutable()) {
                d.dispatch(item);
            }
            // TODO Clone if the branch will destroy the item.
            // else
            // d.dispatch(item.clone());
        }
    }

    public void clearBranch(final int outputQueueThreadLocalId) {
        dispatchers[outputQueueThreadLocalId] = new Dispatcher[0];
    }

    public void addBranch(final int srcQueueThreadLocalId, final Dispatcher dispatcher) {
        final Dispatcher[] oldDispatchers = dispatchers[srcQueueThreadLocalId];
        Dispatcher[] newDispatchers;
        if (oldDispatchers != null) {
            newDispatchers = Arrays.copyOf(oldDispatchers, oldDispatchers.length + 1);
        } else {
            newDispatchers = new Dispatcher[1];
        }

        newDispatchers[newDispatchers.length - 1] = dispatcher;
        dispatchers[srcQueueThreadLocalId] = newDispatchers;
    }

    public void deleteBranch(final int srcQueueThreadLocalId, final Dispatcher dispatcher) {
        final Dispatcher[] oldDispatchers = dispatchers[srcQueueThreadLocalId];
        final Dispatcher[] newDispathcers = new Dispatcher[oldDispatchers.length - 1];

        int l = 0, r = 0;
        while (r < oldDispatchers.length) {
            if (oldDispatchers[r] == dispatcher) {
                r++;
                continue;
            }

            newDispathcers[l++] = oldDispatchers[r++];
        }
        dispatchers[srcQueueThreadLocalId] = newDispathcers;
    }

    public void addRoute(final int srcQueueThreadLocalId, final Dispatcher dispatcher, final DispatchAccepter accepter) {
        for (int i = 0; i < dispatchers[srcQueueThreadLocalId].length; i++) {
            if (dispatchers[srcQueueThreadLocalId][i] == dispatcher) {
                dispatchers[srcQueueThreadLocalId][i].newAccepter(accepter);
            }
        }
        throw new RuntimeException("BUG: Illegal Dispatcher.");
    }

    public void changeRoute(final int srcQueueThreadLocalId, final Dispatcher newDispatcher) {
        dispatchers[srcQueueThreadLocalId] = new Dispatcher[1];
        dispatchers[srcQueueThreadLocalId][0] = newDispatcher;
    }

    public void deleteRoute(final int srcQueueThreadLocalId, final Dispatcher dispatcher, final DispatchAccepter accepter) {
        for (int i = 0; i < dispatchers[srcQueueThreadLocalId].length; i++) {
            if (dispatchers[srcQueueThreadLocalId][i] == dispatcher) {
                dispatchers[srcQueueThreadLocalId][i].removeAccepter(accepter);
            }
        }
        throw new RuntimeException("BUG: Illegal Dispatcher.");
    }

    public void addNewOutputQueue(final int outputQueueThreadLocalId) {
        if (dispatchers.length <= outputQueueThreadLocalId) {
            dispatchers = Arrays.copyOf(dispatchers, outputQueueThreadLocalId + 1);
        }
        if (dispatchers[outputQueueThreadLocalId] != null) {
            throw new IllegalArgumentException("BUG: Illegal consistency of dispathers.");
        }
        dispatchers[outputQueueThreadLocalId] = new Dispatcher[0];
    }

    public void removeOutputQueue(final int outputQueueThreadLocalId) {
        dispatchers[outputQueueThreadLocalId] = null;
    }

    public Dispatcher[] getDispatcher(final int srcQueueThreadLocalId) {
        return dispatchers[srcQueueThreadLocalId];
    }

    public void setOutputMapping(final int contextThreadLocalId, final int logicalId, final int threadLocalId) {
        if (queueIdMapping.length <= contextThreadLocalId) {
            queueIdMapping = Arrays.copyOf(queueIdMapping, contextThreadLocalId + 1);
        }
        if (queueIdMapping[contextThreadLocalId] == null) {
            queueIdMapping[contextThreadLocalId] = new int[logicalId + 1];
        } else if (queueIdMapping[contextThreadLocalId].length <= logicalId) {
            queueIdMapping[contextThreadLocalId] = Arrays.copyOf(queueIdMapping[contextThreadLocalId], logicalId + 1);
        }

        queueIdMapping[contextThreadLocalId][logicalId] = threadLocalId;
    }

}
