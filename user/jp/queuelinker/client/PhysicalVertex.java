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

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.util.GenericVertex;

/**
 * This class represents a physical vertex.
 */
public class PhysicalVertex extends GenericVertex<PhysicalVertex, PhysicalEdge> {

    /**
     *
     */
    private static final long serialVersionUID = -1136282730484325056L;

    /**
     * The global unique ID of this physical vertex.
     */
    private final int physicalId;

    /**
     * The vertex of this physical vertex. This can be a logical vertex.
     */
    private final Vertex vertex;

    /**
     * The signature of the worker that this physical vertex runs on.
     */
    private final WorkerSignature workerSignature;

    /**
     * The number of items that this physical vertex processed.
     */
    private long inputCountStat;

    /**
     * The number of items that this physical vertex generated.
     */
    private long outputCountStat;

    /**
     * Creates a physical vertex with the specified parameters.
     * @param physicalId The global unique ID of this physical vertex.
     * @param vertex The vertex of this physical vertex. This can be a logical vertex.
     * @param workerSignature The signature of the worker that this physical vertex runs on.
     */
    PhysicalVertex(final int physicalId, final Vertex vertex, final WorkerSignature workerSignature) {
        super(vertex.getName(), vertex.getInputQueueCount(), vertex.getOutputQueueCount());
        this.physicalId = physicalId;
        this.vertex = vertex;
        this.workerSignature = workerSignature;
    }

    /**
     * @return The signature of the worker that this
     */
    public final WorkerSignature getWorkerSignature() {
        return workerSignature;
    }

    /**
     * Returns the vertex that is of this physical vertex.
     * @return The vertex that is of this physical vertex.
     */
    public final Vertex getVertex() {
        return vertex;
    }

    /**
     * Returns the global unique ID of this physical vertex.
     * @return The global unique ID of this physical vertex.
     */
    public final int getPhysicalId() {
        return physicalId;
    }

    /**
     * @param inputCountStat The number of items that this physical vertex processed.
     */
    final void setInputCountStat(final long inputCountStat) {
        this.inputCountStat = inputCountStat;
    }

    /**
     * @return The number of items that this physical vertex processed.
     */
    public final long getInputCountStat() {
        return inputCountStat;
    }

    /**
     * @param outputCountStat The number of items that this physical vertex generated.
     */
    final void setOutputCountStat(final long outputCountStat) {
        this.outputCountStat = outputCountStat;
    }

    /**
     * @return The number of items that this physical vertex generated.
     */
    public final long getOutputCountStat() {
        return outputCountStat;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#getName()
     */
    @Override
    public final String getName() {
        return super.getName();
    }
}
