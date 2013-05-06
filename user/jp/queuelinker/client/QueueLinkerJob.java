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
public final class QueueLinkerJob implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1013572670490037986L;

    /**
     * LogicalGraph representing a new job.
     */
    private final LogicalGraph graph;

    /**
     * @param graph A LogicalGraph representing a new job.
     */
    public QueueLinkerJob(final LogicalGraph graph) {
        this.graph = graph;
    }

    /**
     * @return The LogicalGraph of this job.
     */
    public LogicalGraph getVertexGraph() {
        return graph;
    }
}
