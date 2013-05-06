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
 * This class represents the property of a logical edge.
 */
public final class LogicalEdgeProperty implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8278837819766043583L;

    /**
     *
     */
    public enum EdgeMode {
        /**
         *
         */
        NORMAL,
        /**
         *
         */
        ORDERED
    }

    /**
     * True if the communication must be done in a computer.
     */
    private final boolean localOnly;

    /**
     *
     */
    private final EdgeMode edgeMode;

    /**
     * @param builder
     */
    private LogicalEdgeProperty(final VertexEdgePropertyBuilder builder) {
        this.localOnly = builder.localOnly;
        this.edgeMode = builder.edgeMode;
    }

    /**
     * @return True if this edge is set to local mode. Otherwise false.
     */
    public boolean isLocalOnly() {
        return localOnly;
    }

    /**
     * @return The edge mode.
     */
    public EdgeMode getEdgeMode() {
        return edgeMode;
    }

    /**
     * @return
     */
    public static LogicalEdgeProperty getDefaultProperty() {
        return (new VertexEdgePropertyBuilder()).build();
    }

    /**
     *
     */
    public static class VertexEdgePropertyBuilder {
        /**
         *
         */
        private boolean localOnly;

        /**
         *
         */
        private EdgeMode edgeMode;

        /**
         * @param localOnly
         * @return
         */
        public VertexEdgePropertyBuilder localOnly(final boolean localOnly) {
            this.localOnly = localOnly;
            return this;
        }

        /**
         * @param mode
         * @return
         */
        public VertexEdgePropertyBuilder setMode(final EdgeMode mode) {
            this.edgeMode = mode;
            return this;
        }

        /**
         * @return
         */
        public LogicalEdgeProperty build() {
            return new LogicalEdgeProperty(this);
        }
    }
}
