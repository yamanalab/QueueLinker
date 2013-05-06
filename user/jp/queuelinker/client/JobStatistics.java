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

package jp.queuelinker.client;

import java.util.HashMap;

/**
 *
 */
public final class JobStatistics {

    /**
     *
     */
    private final HashMap<Vertex, JobStat> stats = new HashMap<>();

    /**
     * @param v The vertex to be added.
     * @param contextCount
     * @param threadCount
     * @param inputCount
     * @param outputCount
     * @param consumedMemory
     */
    public final void addStat(final Vertex v, final long contextCount, final long threadCount,
                              final long inputCount, final long outputCount, final long consumedMemory) {
        stats.put(v, new JobStat(contextCount, threadCount, inputCount, outputCount, consumedMemory));
    }

    /**
     * @param v
     * @return
     */
    public final JobStat getStat(final Vertex v) {
        return stats.get(v);
    }

    /**
     *
     */
    public class JobStat {
        /**
         *
         */
        private final long contextWaitingCount;

        /**
         *
         */
        private final long threadWaitingCount;

        /**
         *
         */
        private final long inputCount;

        /**
         *
         */
        private final long outputCount;

        /**
         *
         */
        private final long consumedMemory;

        /**
         * @param contextWaitingCount
         * @param threadWaitingCount
         * @param inputCount
         * @param outputCount
         * @param consumedMemory
         */
        JobStat(final long contextWaitingCount, final long threadWaitingCount, final long inputCount,
                final long outputCount, final long consumedMemory) {
            this.contextWaitingCount = contextWaitingCount;
            this.threadWaitingCount = threadWaitingCount;
            this.inputCount = inputCount;
            this.outputCount = outputCount;
            this.consumedMemory = consumedMemory;
        }

        /**
         * @return
         */
        public long getContextWaitingCount() {
            return contextWaitingCount;
        }

        /**
         * @return
         */
        public long getThreadWaitingCount() {
            return threadWaitingCount;
        }

        /**
         * @return
         */
        public final long getInputCount() {
            return inputCount;
        }

        /**
         * @return
         */
        public final long getOutputCount() {
            return outputCount;
        }

        /**
         * @return
         */
        public final long getConsumedMemory() {
            return consumedMemory;
        }
    }
}
