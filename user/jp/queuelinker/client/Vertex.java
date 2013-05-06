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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jp.queuelinker.module.base.ConstructorArguments;
import jp.queuelinker.module.base.HashCoder;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.util.GenericVertex;

/**
 * This is the base class of Vertex implementation. All logical vertices extend this class.
 */
public abstract class Vertex extends GenericVertex<Vertex, Edge> {

    /**
     *
     */
    private static final long serialVersionUID = -6663036338972306687L;

    /**
     *
     */
    private final LinkedList<String> preferableWorkers = new LinkedList<>();

    /**
     * Constructor.
     * @param name The name of this vertex.
     */
    protected Vertex(final String name) {
        super(name);
    }

    /**
     * Adds a worker that this vertex should run on.
     * @param hostName
     */
    public void addPreferableWorker(final String hostName) {
        if (preferableWorkers.contains(hostName)) {
            throw new IllegalArgumentException("The Server is already registered as a preferable worker.");
        }
        preferableWorkers.add(hostName);
    }

    /**
     * Removes a preferable worker.
     * @param signature
     * @return True if a worker was removed. Or, false.
     */
    public boolean removePreferableWorker(final WorkerSignature signature) {
        return preferableWorkers.remove(signature);
    }

    /**
     * @return
     */
    public List<String> getPreferableWorkers() {
        return Collections.unmodifiableList(preferableWorkers);
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#getAllInputEdges()
     */
    @Override
    public ArrayList<Edge> getAllInputEdges() {
        return super.getAllInputEdges();
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#getAllOutputEdges()
     */
    @Override
    public ArrayList<Edge> getAllOutputEdges() {
        return super.getAllOutputEdges();
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#getVertexId()
     */
    @Override
    public int getVertexId() {
        return super.getVertexId();
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#getOutputBranchCount(int)
     */
    @Override
    public int getOutputBranchCount(final int queueId) {
        return super.getOutputBranchCount(queueId);
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.util.GenericVertex#getName()
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * @return
     */
    public abstract Class<? extends ModuleBase> getModuleClass();

    /**
     * @return
     */
    public abstract ConstructorArguments getConstructorArguments();

    /**
     * @return
     */
    public abstract boolean isSinkModule();

    /**
     * @return
     */
    public abstract boolean isSourceModule();

    /**
     * @return
     */
    public abstract InputQueueHandle getDefaultInputQueueHandle();

    /**
     * @param queueId
     * @return
     */
    public abstract InputQueueHandle getInputQueueHandle(int queueId);

    /**
     * @return
     */
    public abstract int getInputQueueCount();

    /**
     * @return
     */
    public abstract OutputQueueHandle getDefaultOutputQueueHandle();

    /**
     * @param queueId
     * @return
     */
    public abstract OutputQueueHandle getOutputQueueHandle(int queueId);

    /**
     * @return
     */
    public abstract int getOutputQueueCount();

    /**
     * @return
     */
    public abstract int getCpuUsage();

    /**
     * @return
     */
    public abstract VertexDistributingMode getDistributingMode();

    /**
     * @param queueId
     * @return
     */
    public abstract HashCoder getHashCoder(int queueId);
}
