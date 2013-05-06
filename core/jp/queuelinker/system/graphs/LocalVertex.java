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

package jp.queuelinker.system.graphs;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.queuelinker.system.sched.LocalThread;
import jp.queuelinker.system.thread.ThreadContextBase;
import jp.queuelinker.system.thread.ThreadUnit;
import jp.queuelinker.system.util.GenericVertex;

public abstract class LocalVertex extends GenericVertex<LocalVertex, LocalEdge> {

    private static final long serialVersionUID = 1L;

    protected transient LocalThread thread;

    protected transient ThreadContextBase context;

    protected transient ThreadUnit threadUnit;

    private transient int[] inputQueueMapping = new int[0];

    private transient int[] outputQueueMapping = new int[0];

    public LocalVertex(LocalThread thread) {
        this.thread = thread;
    }

    public LocalThread getLocalThread() {
        return thread;
    }

    public void setLocalThread(LocalThread thread) {
        this.thread = thread;
    }

    public int[] getCpuAffinityIds() {
        return new int[] { thread.getAffinityId() };
    }

    public ThreadUnit getThreadUnit() {
        return threadUnit;
    }

    public void setThreadUnit(ThreadUnit threadUnit) {
        this.threadUnit = threadUnit;
    }

    public ThreadContextBase getContext() {
        return context;
    }

    public void setContext(ThreadContextBase context) {
        this.context = context;
    }

    @Override
    public List<LocalEdge> getInputEdges(int inputQueueId) {
        return super.getInputEdges(inputQueueId);
    }

    @Override
    public List<LocalEdge> getOutputEdges(int outputQueueId) {
        return new ArrayList<LocalEdge>(super.getOutputEdges(outputQueueId));
    }

    public List<LocalEdge> getOutputEdges(int outputQueueId, int logicalBranchId) {
        List<LocalEdge> candidates = super.getOutputEdges(outputQueueId);
        ArrayList<LocalEdge> ret = new ArrayList<>();
        for (LocalEdge cand : candidates) {
            if (cand.getLogicalBranchId() == logicalBranchId)
                ret.add(cand);
        }
        return ret;
    }

    public int getInputQueueCount() {
        return super.getInputCount();
    }

    public int getOutputQueueCount() {
        return super.getOutputCount();
    }

    public void setInputQueueThreadLocalId(int inputQueueId, int threadLocalId) {
        if (inputQueueMapping.length <= inputQueueId)
            inputQueueMapping = Arrays.copyOf(inputQueueMapping,
                    inputQueueId + 1);
        inputQueueMapping[inputQueueId] = threadLocalId;
    }

    public void setOutputQueueThreadLocalId(int outputQueueId, int threadLocalId) {
        if (outputQueueMapping.length <= outputQueueId)
            outputQueueMapping = Arrays.copyOf(outputQueueMapping,
                    outputQueueId + 1);
        outputQueueMapping[outputQueueId] = threadLocalId;
    }

    public int getInputQueueThreadLocalId(int inputQueueId) {
        return inputQueueMapping[inputQueueId];
    }

    public int getOutputQueueThreadLocalId(int outputQueueId) {
        return outputQueueMapping[outputQueueId];
    }

    @Override
    public int getVertexId() {
        return super.getVertexId();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        inputQueueMapping = new int[0];
        outputQueueMapping = new int[0];
    }

    private void readObjectNoData() throws ObjectStreamException {
        inputQueueMapping = new int[0];
        outputQueueMapping = new int[0];
    }
}
