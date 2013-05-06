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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import jp.queuelinker.system.sched.LocalThread;

public class LocalSelectorVertex extends LocalVertex {

    private static final long serialVersionUID = -1437655631552820560L;

    private transient InetAddress bindAddress;

    private transient HashMap<Integer, Integer> queue2port = new HashMap<>();

    public LocalSelectorVertex(LocalThread thread, InetAddress bindAddress) {
        super(thread);
        this.bindAddress = bindAddress;
    }

    public int getSelectorInputCount() {
        return super.getInputCount();
    }

    public int getSelectorOutputCout() {
        return super.getOutputCount();
    }

    public int addSelectorInput() {
        return super.addInput();
    }

    public int addSelectorOutput() {
        return super.addOutput();
    }

    @Override
    public int getOutputQueueCount() {
        return super.getOutputCount();
    }

    @Override
    public int getInputQueueCount() {
        return super.getInputCount();
    }

    public void setBindAddress(InetAddress address) {
        assert (bindAddress == null);
        this.bindAddress = address;
    }

    public InetAddress getBindAddress() {
        return bindAddress;
    }

    public void setPort(int queueId, int port) {
        queue2port.put(queueId, port);
    }

    public int getPort(int queueId) {
        if (!queue2port.containsKey(queueId))
            throw new IllegalArgumentException("BUG: Illegal Queue Id.");
        return queue2port.get(queueId);
    }

    public InetSocketAddress getAcceptAddress(int queueId) {
        return new InetSocketAddress(bindAddress, getPort(queueId));
    }
}
