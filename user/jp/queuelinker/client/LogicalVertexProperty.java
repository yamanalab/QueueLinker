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

import java.io.Serializable;

/**
 *
 */
final public class LogicalVertexProperty implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8526171604591338372L;

    private final int numThreadPerNode;
    private final int maxTotalNumThread;
    private final String fixAtNode;
    private final String exceptAtNode;
    private final boolean mergeSortMode;
    private final boolean customSerializeMode;
    private final boolean customDeserializeMode;
    private final VertexDistributingMode distributingMode;

    private LogicalVertexProperty(final VertexPropertyBuilder builder) {
        this.numThreadPerNode = builder.numThreadPerNode;
        this.maxTotalNumThread = builder.maxTotalNumThread;
        this.fixAtNode = builder.fixAtNode;
        this.exceptAtNode = builder.exceptAtNode;
        this.mergeSortMode = builder.mergeSortMode;
        this.customSerializeMode = builder.customSerializeMode;
        this.customDeserializeMode = builder.customDeserializeMode;
        this.distributingMode = builder.distributingMode;
    }

    public int numThreadPerNode() {
        return numThreadPerNode;
    }

    public int maxTotalNumThread() {
        return maxTotalNumThread;
    }

    public String fixAtNode() {
        return fixAtNode;
    }

    public String exceptAtNode() {
        return exceptAtNode;
    }

    public boolean mergeSortMode() {
        return mergeSortMode;
    }

    public boolean customSerializeMode() {
        return customSerializeMode;
    }

    public boolean customDeserializeMode() {
        return customDeserializeMode;
    }

    public VertexDistributingMode distributingMode() {
        return distributingMode;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + numThreadPerNode;
        result = 31 * result + maxTotalNumThread;
        return result;
    }

    public static class VertexPropertyBuilder {
        int numThreadPerNode = 1;
        int maxTotalNumThread = 0;
        String fixAtNode = null;
        String exceptAtNode = null;
        boolean mergeSortMode = false;
        boolean customSerializeMode = false;
        boolean customDeserializeMode = false;
        VertexDistributingMode distributingMode = VertexDistributingMode.FULL;

        public LogicalVertexProperty build() {
            return new LogicalVertexProperty(this);
        }

        public VertexPropertyBuilder numThreadPerNode(final int thread) {
            this.numThreadPerNode = thread;
            return this;
        }

        public VertexPropertyBuilder maxTotalNumThread(final int maxTotalNumThread) {
            this.maxTotalNumThread = maxTotalNumThread;
            return this;
        }

        public VertexPropertyBuilder fixAt(final String hostname) {
            this.fixAtNode = hostname;
            return this;
        }

        public VertexPropertyBuilder exceptAt(final String address) {
            this.exceptAtNode = address;
            return this;
        }

        public VertexPropertyBuilder mergeSortMode(final boolean mode) {
            mergeSortMode = mode;
            return this;
        }

        public VertexPropertyBuilder customSerializeMode(final boolean mode) {
            this.customSerializeMode = mode;
            return this;
        }

        public VertexPropertyBuilder customDeserializeMode(final boolean mode) {
            this.customDeserializeMode = mode;
            return this;
        }

        public VertexPropertyBuilder distributingMode(
                final VertexDistributingMode mode) {
            this.distributingMode = mode;
            return this;
        }
    }
}
