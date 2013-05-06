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
import java.util.List;

import jp.queuelinker.module.base.ConstructorArguments;
import jp.queuelinker.module.base.FlowSwitcherModule;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.module.base.ModuleBase;

/**
 * The SwitcherVertex represents a switcher vertex.
 */
public final class SwitcherVertex extends Vertex {

    /**
     *
     */
    private static final long serialVersionUID = -5635898026765342842L;

    /**
     *
     */
    private final Class<? extends FlowSwitcherModule<?>> module;

    /**
     *
     */
    private final InputQueueHandle inputHandle;

    /**
     *
     */
    private final ArrayList<OutputQueueHandle> outputHandles = new ArrayList<>();

    /**
     * @param module A switcher module that performs
     * @param name The name of this switcher.
     * @param switcherCount The number of the destinations of this switcher.
     */
    SwitcherVertex(final Class<? extends FlowSwitcherModule<?>> module, final String name, final int switcherCount) {
        super(name);

        this.module = module;
        addInput();
        this.inputHandle = new InputQueueHandle(this, "InputQueue", 0);

        for (int i = 0; i < switcherCount; i++) {
            addOutput();
            final OutputQueueHandle outputHandle = new OutputQueueHandle(this, "OutputQueue" + i, i);
            outputHandles.add(outputHandle);
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getModuleClass()
     */
    @Override
    public Class<? extends ModuleBase> getModuleClass() {
        return module;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#isSinkModule()
     */
    @Override
    public boolean isSinkModule() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#isSourceModule()
     */
    @Override
    public boolean isSourceModule() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getDefaultInputQueueHandle()
     */
    @Override
    public InputQueueHandle getDefaultInputQueueHandle() {
        return inputHandle;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getInputQueueHandle(int)
     */
    @Override
    public InputQueueHandle getInputQueueHandle(final int queueId) {
        if (queueId != 0) {
            throw new IllegalArgumentException(
                    "The queueId must be zero because this is a switcher and has only one queue");
        }
        return inputHandle;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getDefaultOutputQueueHandle()
     */
    @Override
    public OutputQueueHandle getDefaultOutputQueueHandle() {
        return outputHandles.get(0);
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getOutputQueueHandle(int)
     */
    @Override
    public OutputQueueHandle getOutputQueueHandle(final int queueId) {
        if (queueId >= outputHandles.size()) {
            throw new IllegalArgumentException(String.format("The queueId is too large (queueId: %d)", queueId));
        }

        return outputHandles.get(queueId);
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getInputQueueCount()
     */
    @Override
    public int getInputQueueCount() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getOutputQueueCount()
     */
    @Override
    public int getOutputQueueCount() {
        return outputHandles.size();
    }

    /**
     * Returns the logical edge that is connected to the input of this vertex.
     * @return The logical edge that is connected to the input of this vertex.
     */
    public LogicalEdge getInputEdge() {
        List<Edge> ret = getInputEdges(0);
        assert (ret.size() == 1);
        return (LogicalEdge) ret.get(0);
    }

    /**
     * Returns the vertex that sends items to this switcher.
     * @return The vertex that sends items to this switcher.
     */
    public Vertex getSourceVertex() {
        return getInputEdge().getSrcVertex();
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getConstructorArguments()
     */
    @Override
    public ConstructorArguments getConstructorArguments() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getCpuUsage()
     */
    @Override
    public int getCpuUsage() {
        return 1; // TODO Fix Me.
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getName()
     */
    @Override
    public String getName() {
        String name = super.getName();
        if (name != null && name.length() != 0) {
            return name;
        }
        return module.getCanonicalName();
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getDistributingMode()
     */
    @Override
    public VertexDistributingMode getDistributingMode() {
        return VertexDistributingMode.FULL;
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.client.Vertex#getHashCoder(int)
     */
    @Override
    public HashCoder getHashCoder(final int queueId) {
        return null;
    }
}
