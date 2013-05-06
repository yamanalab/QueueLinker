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

package jp.queuelinker.system.thread;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.net.ObjectSelectorCallBack;
import jp.queuelinker.system.net.ObjectSelectorThread;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.thread.router.ThreadLocalOutputRouter;

public class SelectorContext<I extends Serializable, O extends Serializable>
extends PullContext implements InputPullContext<I>, OutputPullContext<O>, ObjectSelectorCallBack {
    private final ObjectSelectorThread selectorThread;

    private LinkedBlockingQueue[] inputs = new LinkedBlockingQueue[0];

    private final ThreadLocalOutputRouter output;

    private int[] logical2chennel = new int[0];

    private volatile int inputCount;

    private volatile int outputCount;

    SelectorContext(final ThreadUnit unit) throws IOException {
        super(unit);
        this.selectorThread = new ObjectSelectorThread();
        this.output = unit.getThreadLocalOutputRouter();
    }

    public void connect(final int logicalId, final SocketAddress remoteAddress) throws IOException {
        final int channelId = selectorThread.connectChannel(remoteAddress, this);
        selectorThread.attach(channelId, logicalId);
        if (logical2chennel.length <= logicalId)
            logical2chennel = Arrays.copyOf(logical2chennel, logicalId + 1);
        logical2chennel[logicalId] = channelId;

        selectorThread.startChannel(channelId, SelectionKey.OP_WRITE);
    }

    public int startAccepting(final int logicalId, final InetAddress bindAddress) throws IOException {
        final int channelId = selectorThread.openServerSocket(bindAddress, this);
        selectorThread.attach(channelId, logicalId);
        selectorThread.startChannel(channelId, SelectionKey.OP_ACCEPT);
        return selectorThread.getLocalPort(channelId);
    }

    @Override
    public long getInputCount() {
        return inputCount;
    }

    @Override
    public void resetInputCount() {
        inputCount = 0;
    }

    @Override
    public void dispatchAccept(final I element, final DispatchRouteInformation route) {
        inputs[route.destQueueLogicalId].add(element);
        selectorThread.activate(logical2chennel[route.destQueueLogicalId], SelectionKey.OP_WRITE);
    }

    @Override
    public long getOutputCount() {
        return outputCount;
    }

    @Override
    public void resetOutputCount() {
        outputCount = 0;
    }

    @Override
    void initialize() {
        // Nothing to do.
    }

    @Override
    void execute() {
        this.selectorThread.directRun();
    }

    @Override
    public boolean receive(final int channelId, final Object obj, final Object attachment) {
        output.send(threadLocalId, (int) attachment, obj);
        return true;
    }

    @Override
    public Object send(final int channelId, final Object attachment) {
        return inputs[(int) attachment].poll();
    }

    @Override
    public void newConnection(final int channelIdOfServer, final SocketChannel newChannel, final Object attachment) {
        int channelId = selectorThread.addSocketChannel(newChannel, this);
        selectorThread.attach(channelId, attachment);

        // TODO: This try must be removed.
        try {
            selectorThread.startChannel(channelId, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionOccured(final int channelId, final Object attachment) {
        // TODO Auto-generated method stub
    }

    @Override
    public void fatalError() {
        // TODO Auto-generated method stub
    }

    public void addNewInputQueue(final String name, final int logicalQueueId, final int threadLocalId) {
        if (inputs.length <= logicalQueueId) {
            inputs = Arrays.copyOf(inputs, logicalQueueId + 1);
        }
        inputs[logicalQueueId] = new LinkedBlockingQueue<>();
    }

    public void addNewOutputQueue(final String name, final int logicalQueueId, final int threadLocalId) {
        unit.addThreadOutputMapping(this, logicalQueueId, threadLocalId);
    }

    @Override
    public String toString() {
        return "SelectorContext";
    }

    @Override
    public void snapShotModule(final ObjectOutputStream output)
            throws InvalidClassException, IOException, NotSerializableException {

    }

    @Override
    public ModuleBase getModuleBase() {
        return null;
    }
}
