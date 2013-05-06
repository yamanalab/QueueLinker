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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.queuelinker.module.base.ConstructorArguments;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.module.base.InputModule;
import jp.queuelinker.module.base.LogicalModule;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.OutputModule;
import jp.queuelinker.module.base.SinkModule;
import jp.queuelinker.module.base.SourceModule;
import jp.queuelinker.system.util.CollectionUtil;
import jp.queuelinker.system.util.ReflectionUtil;

/**
 * This class represents a logical vertex.
 */
public class LogicalVertex extends Vertex implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6145575781923766132L;

    /**
     * The Class instance of this module.
     */
    private final Class<? extends LogicalModule> module;

    /**
     *
     */
    private ConstructorArguments arguments = new ConstructorArguments();

    /**
     *
     */
    private final LogicalVertexProperty property;

    /**
     *
     */
    private final ArrayList<InputQueueHandle> inputQueues = new ArrayList<>();

    /**
     *
     */
    private final ArrayList<OutputQueueHandle> outputQueues = new ArrayList<>();

    /**
     *
     */
    private final ArrayList<ThreadContextHandle> threadContexts = new ArrayList<ThreadContextHandle>();

    /**
     *
     */
    private final ArrayList<VirtualVertex> virtualVertices = new ArrayList<>();

    /**
     *
     */
    private final ArrayList<HashCoder> hashCoders = new ArrayList<>();

    /**
     *
     */
    private int nextThreadId;

    /**
     * @param module
     * @param property
     * @param name
     */
    LogicalVertex(final Class<? extends LogicalModule> module, final LogicalVertexProperty property, final String name) {
        super(name);

        this.module = module;
        this.property = property;
        this.threadContexts.add(new ThreadContextHandle("DefaultThreadContext", nextThreadId++));
        addDefaultQueues();
    }

    /**
     * @return
     */
    public Type getDefaultInputType() {
        Class<?> parent = module;
        Class<?> child = module.getSuperclass();
        while (child != null) {
            // TODO Fix me.
            if (child.equals(ModuleBase.class)) {
                return ((ParameterizedType) parent.getGenericSuperclass()).getActualTypeArguments()[0];
            }
            parent = child;
            child = child.getSuperclass();
        }

        // UAhhh. The class is not subclass of IVertex.
        throw new RuntimeException("Bug: The class is not subclass of IVertex: " + module);
    }

    /**
     * @return
     */
    public Type getDefaultOutputType() {
        Class<?> parent = module;
        Class<?> child = module.getSuperclass();
        while (child != null) {
            // TODO Fix me.
            if (child.equals(ModuleBase.class)) {
                return ((ParameterizedType) parent.getGenericSuperclass()).getActualTypeArguments()[1];
            }
            parent = child;
            child = child.getSuperclass();
        }

        // UAhhh. This class is not subclass of IVertex.
        throw new RuntimeException("Bug: The class is not subclass of IVertex: " + module);
    }

    /**
     * @param name
     * @return
     */
    public InputQueueHandle addInputQueue(final String name) {
        return addInputQueue(name, null);
    }

    /**
     * @param name
     * @param coder
     * @return
     */
    public InputQueueHandle addInputQueue(final String name, final HashCoder coder) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name is null or zero length.");
        }
        if (getInputQueueHandle(name) != null) {
            throw new IllegalArgumentException("The name has been used.");
        }

        final int newInputId = super.addInput();
        final InputQueueHandle handle = new InputQueueHandle(this, name, newInputId);
        CollectionUtil.expandArrayListSize(inputQueues, newInputId + 1);
        inputQueues.set(newInputId, handle);
        CollectionUtil.expandArrayListSize(hashCoders, newInputId + 1);
        hashCoders.set(newInputId, coder);

        return handle;
    }

    public void setInputHashCoder(final InputQueueHandle handle, final HashCoder coder) {
        if (handle.getQueueId() >= getInputQueueCount()) {
            throw new IllegalArgumentException("The queue id is too large.");
        }
        hashCoders.set(handle.getQueueId(), coder);
    }

    public HashCoder getHashCoder(final InputQueueHandle handle) {
        if (handle.getQueueId() >= getInputQueueCount()) {
            throw new IllegalArgumentException("The queue id is too large.");
        }
        return hashCoders.get(handle.getQueueId());
    }

    @Override
    public InputQueueHandle getDefaultInputQueueHandle() {
        if (inputQueues.size() == 0) {
            throw new RuntimeException("This vertex has no input queue.");
        }
        return inputQueues.get(0);
    }

    @Override
    public InputQueueHandle getInputQueueHandle(final int queueId) {
        if (queueId >= inputQueues.size() || queueId < 0) {
            throw new IllegalArgumentException("The queueId is too large or less than zero (queueId: " + queueId + ").");
        }
        return inputQueues.get(queueId);
    }

    public InputQueueHandle getInputQueueHandle(final String queueName) {
        for (InputQueueHandle handle : inputQueues) {
            if (handle.getQueueName().equals(queueName)) {
                return handle;
            }
        }
        return null;
    }

    public OutputQueueHandle addOutputQueue(final String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name is null or zero length.");
        }
        if (getOutputQueueHandle(name) != null) {
            throw new IllegalArgumentException("The name has been used.");
        }

        final int newQueueId = super.addOutput();
        final OutputQueueHandle handle = new OutputQueueHandle(this, name, newQueueId);
        CollectionUtil.expandArrayListSize(outputQueues, newQueueId + 1);
        outputQueues.set(newQueueId, handle);
        return handle;
    }

    @Override
    public OutputQueueHandle getDefaultOutputQueueHandle() {
        if (outputQueues.size() == 0) {
            throw new RuntimeException("This vertex has no output queue.");
        }
        return outputQueues.get(0);
    }

    @Override
    public OutputQueueHandle getOutputQueueHandle(final int queueId) {
        if (queueId >= outputQueues.size() || queueId < 0) {
            throw new IllegalArgumentException("The queueId is too large or less than zero (queueId: " + queueId + ").");
        }
        return outputQueues.get(queueId);
    }

    public OutputQueueHandle getOutputQueueHandle(final String name) {
        for (OutputQueueHandle handle : outputQueues) {
            if (handle.getQueueName().equals(name)) {
                return handle;
            }
        }
        return null;
    }

    public ThreadContextHandle addThreadContext(final String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name is null or zero length.");
        }
        if (getThreadContextHandle(name) != null) {
            throw new IllegalArgumentException("The name has been used.");
        }

        final ThreadContextHandle handle = new ThreadContextHandle(name, nextThreadId);
        CollectionUtil.expandArrayListSize(threadContexts, nextThreadId);
        threadContexts.set(nextThreadId, handle);
        nextThreadId++;
        return handle;
    }

    public ThreadContextHandle getThreadContextHandle(final String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The name is null or zero length.");
        }

        for (ThreadContextHandle handle : threadContexts) {
            if (handle.getThreadName().equals(name)) {
                return handle;
            }
        }
        return null;
    }

    public ThreadContextHandle getThreadContextHandle(final int threadId) {
        if (threadId < 0 || threadId >= threadContexts.size()) {
            throw new IllegalArgumentException("The id is illegal:" + threadId);
        }

        return threadContexts.get(threadId);
    }

    public int getThreadContextCount() {
        return threadContexts.size();
    }

    /**
     * Returns the number of input queues.
     * @return The number of input queues.
     */
    @Override
    public int getInputQueueCount() {
        return inputQueues.size();
    }

    /**
     * Returns the number of output queues.
     * @return The number of output queues.
     */
    @Override
    public int getOutputQueueCount() {
        return outputQueues.size();
    }

    public LogicalVertexProperty getVertexProperty() {
        return property;
    }

    @Override
    public ConstructorArguments getConstructorArguments() {
        return arguments;
    }

    public void setConstructorArgument(final ConstructorArguments arguments) {
        if (arguments == null) {
            throw new IllegalArgumentException("The arguments is null.");
        }
        this.arguments = arguments;
    }

    public void addConstructorArgument(final String argName, final Object arg) {
        arguments.addArgument(argName, arg);
    }

    public Object getConstructorArgument(final String argName) {
        return arguments.getArgument(argName);
    }

    @Override
    public VertexDistributingMode getDistributingMode() {
        return property.distributingMode();
    }

    public List<VirtualVertex> getVirtualVertices() {
        return Collections.unmodifiableList(virtualVertices);
    }

    @Override
    public List<Edge> getOutputEdges(final int queueId) {
        return super.getOutputEdges(queueId);
    }

    @Override
    public List<Edge> getInputEdges(final int queueId) {
        return super.getInputEdges(queueId);
    }

    @Override
    public String toString() {
        return String.format("Vertex of %s", module);
    }

    @Override
    public int hashCode() {
        // equals() is overridden by the super class.
        return this.module.hashCode();
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (name != null && name.length() != 0) {
            return name;
        }
        return module.getCanonicalName();
    }

    @Override
    public int getCpuUsage() {
        return 1;
    }

    void addVirtualVertex(final VirtualVertex virtualVertex) {
        virtualVertices.add(virtualVertex);
    }

    @Override
    public Class<? extends ModuleBase> getModuleClass() {
        return module;
    }

    @Override
    public boolean isSinkModule() {
        return ReflectionUtil.isExtended(module, SinkModule.class);
    }

    @Override
    public boolean isSourceModule() {
        return ReflectionUtil.isExtended(module, SourceModule.class);
    }

    private void addDefaultQueues() {
        if (ReflectionUtil.isImplemented(module, InputModule.class)) {
            addInputQueue("DefaultInputQueue");
        }
        if (ReflectionUtil.isImplemented(module, OutputModule.class)) {
            addOutputQueue("DefaultOutputQueue");
        }
    }

    @Override
    public HashCoder getHashCoder(final int queueId) {
        return hashCoders.get(queueId);
    }
}
