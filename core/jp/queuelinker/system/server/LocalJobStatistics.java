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

package jp.queuelinker.system.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class LocalJobStatistics implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2532011884779901515L;

    /**
     *
     */
    private final HashMap<Integer, List<LocalVertexStat>> stats
                                                    = new HashMap<>();

    /**
     * @param pvId
     * @param localPartitionId
     * @param waitingOnThreadQueue
     * @param waitingOnContext
     * @param inputCount
     * @param outputCount
     * @param consumedMemory
     */
    public void addStat(final int pvId, final int localPartitionId,
            final int waitingOnThreadQueue, final int waitingOnContext,
            final long inputCount, final long outputCount,
            final long consumedMemory) {
        List<LocalVertexStat> list = stats.get(pvId);
        if (list == null) {
            list = new ArrayList<>();
            stats.put(pvId, list);
        }
        list.add(new LocalVertexStat(localPartitionId, waitingOnThreadQueue,
                waitingOnContext, inputCount, outputCount, consumedMemory));
    }

    /**
     * @param pvId
     * @return
     */
    public List<LocalVertexStat> getLocalVertexStat(int pvId) {
        return stats.get(pvId);
    }

    /**
     *
     */
    public class LocalVertexStat implements Serializable {
        private static final long serialVersionUID = 4238913240046522373L;

        private final int localPartitionId;
        private final int waitingOnThreadQueue;
        private final int waitingOnContext;
        private final long inputCount;
        private final long outputCount;
        private final long consumedMemory;

        public LocalVertexStat(int localPartitionId, int waitingOnThreadQueue,
                int waitingOnContext, long inputCount, long outputCount,
                long consumedMemory) {
            this.localPartitionId = localPartitionId;
            this.waitingOnThreadQueue = waitingOnThreadQueue;
            this.waitingOnContext = waitingOnContext;
            this.inputCount = inputCount;
            this.outputCount = outputCount;
            this.consumedMemory = consumedMemory;
        }

        /**
         * @return
         */
        public int getLocalPartitionId() {
            return localPartitionId;
        }

        /**
         * @return
         */
        public int getWaitingOnThread() {
            return waitingOnThreadQueue;
        }

        /**
         * @return
         */
        public int getWaitingOnContext() {
            return waitingOnContext;
        }

        /**
         * @return
         */
        public long getInputCount() {
            return inputCount;
        }

        /**
         * @return
         */
        public long getOutputCount() {
            return outputCount;
        }

        /**
         * @return
         */
        public long getConsumedMemory() {
            return consumedMemory;
        }
    }
}
