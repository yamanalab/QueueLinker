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

import java.util.ArrayList;

import jp.queuelinker.module.base.ConstructorArguments;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.module.base.ModuleBase;

/**
 * This class represents a virtual vertex.
 */
public final class VirtualVertex extends Vertex {

    /**
     *
     */
    private static final long serialVersionUID = 4748529990542145224L;

    /**
     * The actual vertex of this virtual vertex.
     */
    private final LogicalVertex actualVertex;

    /**
     *
     */
    private final ArrayList<InputQueueHandle> inputQueues = new ArrayList<>();

    /**
     *
     */
    private final ArrayList<OutputQueueHandle> outputQueues = new ArrayList<>();

    /**
     * Constructor. Creates a virtual vertex of a given vertex.
     * @param name The name of this virtual vertex.
     * @param actualVertex The actual vertex to be created a virtual vertex.
     */
    protected VirtualVertex(final String name,
                            final LogicalVertex actualVertex) {
        super(name);

        this.actualVertex = actualVertex;

        for (int i = 0; i < actualVertex.getInputQueueCount(); i++) {
            addInput();
            final InputQueueHandle actualHandle = actualVertex
                    .getInputQueueHandle(i);
            final InputQueueHandle handle = new InputQueueHandle(this,
                    actualHandle.getQueueName(), actualHandle.getQueueId());
            inputQueues.add(handle);
        }

        for (int i = 0; i < actualVertex.getOutputQueueCount(); i++) {
            addOutput();
            final OutputQueueHandle actualHandle = actualVertex
                    .getOutputQueueHandle(i);
            final OutputQueueHandle handle = new OutputQueueHandle(this,
                    actualHandle.getQueueName(), actualHandle.getQueueId());
            outputQueues.add(handle);
        }
    }

    /**
     * Returns the logical vertex that is the actual vertex of this virtual
     * vertex.
     *
     * @return LogicalVertex that is the actual vertex of this virtual vertex.
     */
    public LogicalVertex getLogicalVertex() {
        return actualVertex;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getModuleClass()
     */
    @Override
    public Class<? extends ModuleBase> getModuleClass() {
        return actualVertex.getModuleClass();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#isSinkModule()
     */
    @Override
    public boolean isSinkModule() {
        return actualVertex.isSinkModule();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#isSourceModule()
     */
    @Override
    public boolean isSourceModule() {
        return actualVertex.isSourceModule();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getDefaultInputQueueHandle()
     */
    @Override
    public InputQueueHandle getDefaultInputQueueHandle() {
        return inputQueues.get(0);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getInputQueueHandle(int)
     */
    @Override
    public InputQueueHandle getInputQueueHandle(final int queueId) {
        return inputQueues.get(queueId);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getDefaultOutputQueueHandle()
     */
    @Override
    public OutputQueueHandle getDefaultOutputQueueHandle() {
        return outputQueues.get(0);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getOutputQueueHandle(int)
     */
    @Override
    public OutputQueueHandle getOutputQueueHandle(final int queueId) {
        return outputQueues.get(queueId);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getInputQueueCount()
     */
    @Override
    public int getInputQueueCount() {
        assert (inputQueues.size() == actualVertex.getInputQueueCount());
        return inputQueues.size();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getOutputQueueCount()
     */
    @Override
    public int getOutputQueueCount() {
        assert (outputQueues.size() == actualVertex.getOutputQueueCount());
        return outputQueues.size();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getConstructorArguments()
     */
    @Override
    public ConstructorArguments getConstructorArguments() {
        return actualVertex.getConstructorArguments();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getCpuUsage()
     */
    @Override
    public int getCpuUsage() {
        return actualVertex.getCpuUsage();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getName()
     */
    @Override
    public String getName() {
        String name = super.getName();
        if (name != null && name.length() != 0) {
            return name;
        }
        return "Virtual of " + actualVertex.getName();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getDistributingMode()
     */
    @Override
    public VertexDistributingMode getDistributingMode() {
        return actualVertex.getDistributingMode();
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getHashCoder(int)
     */
    @Override
    public HashCoder getHashCoder(final int queueId) {
        return actualVertex.getHashCoder(queueId);
    }
}
