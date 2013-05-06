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

package jp.queuelinker.system.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import jp.queuelinker.system.util.ByteArrayBuffer;
import jp.queuelinker.system.util.ByteConverter;
import jp.queuelinker.system.util.SerializeUtil;

/**
 *
 */
public class ObjectSelectorThread implements SelectorCallBack {
    /**
     *
     */
    private final SelectorThread thread;

    /**
     * @throws IOException
     */
    public ObjectSelectorThread() throws IOException {
        this.thread = new SelectorThread();
    }

    /**
     * @param threadName
     * @throws IOException
     */
    public ObjectSelectorThread(final String threadName) throws IOException {
        this.thread = new SelectorThread(threadName);
    }

    /**
     *
     */
    public void start() {
        thread.start();
    }

    /**
     * @param daemon
     */
    public void start(final boolean daemon) {
        thread.start(daemon);
    }

    /**
     *
     */
    public void directRun() {
        thread.run();
    }

    /**
     * @param bindAddress
     * @param callBack
     * @return
     * @throws IOException
     */
    public int openServerSocket(final InetAddress bindAddress, final ObjectSelectorCallBack callBack)
            throws IOException {
        return openServerSocket(bindAddress, 0, callBack);
    }

    /**
     * @param bindAddress
     * @param port
     * @param callBack
     * @return
     * @throws IOException
     */
    public int openServerSocket(final InetAddress bindAddress, final int port, final ObjectSelectorCallBack callBack)
            throws IOException {
        int channelId = thread.openServerSocket(bindAddress, port, this);
        thread.attach(channelId, new ChannelAttachment(callBack));
        return channelId;
    }

    /**
     * @param bindAddress
     * @param callBack
     * @return
     * @throws IOException
     */
    public int openServerSocket(final InetSocketAddress bindAddress, final ObjectSelectorCallBack callBack)
            throws IOException {
        int channelId = thread.openServerSocket(bindAddress, this);
        thread.attach(channelId, new ChannelAttachment(callBack));
        return channelId;
    }

    /**
     * @param remoteAddress
     * @param callBack
     * @return
     * @throws IOException
     */
    public int connectChannel(final SocketAddress remoteAddress, final ObjectSelectorCallBack callBack)
            throws IOException {
        int channelId = thread.connectChannel(remoteAddress, this);
        thread.attach(channelId, new ChannelAttachment(callBack));
        return channelId;
    }

    /**
     * @param channel
     * @param callBack
     * @return
     */
    public int addSocketChannel(final SelectableChannel channel, final ObjectSelectorCallBack callBack) {
        int channelId = thread.addSocketChannel(channel, this);
        thread.attach(channelId, new ChannelAttachment(callBack));
        return channelId;
    }

    /**
     * @param channelId
     * @param attachement
     */
    public void attach(final int channelId, final Object attachement) {
        final ChannelAttachment ca = (ChannelAttachment) thread.getAttachment(channelId);
        ca.clientAttachment = attachement;
    }

    /**
     * @param channelId
     * @param interestOps
     * @throws ClosedChannelException
     */
    public void startChannel(final int channelId, final int interestOps) throws ClosedChannelException {
        thread.startChannel(channelId, interestOps);
    }

    /**
     * @param channelId
     * @param interestOps
     */
    public void activate(final int channelId, final int interestOps) {
        thread.activate(channelId, interestOps);
    }

    /**
     * @param channelId
     * @param interestOps
     */
    public void deactivate(final int channelId, final int interestOps) {
        thread.deactivate(channelId, interestOps);
    }

    /**
     * @param interestOps
     */
    public void activateAll(final int interestOps) {
        thread.activateAll(interestOps);
    }

    /**
     * @param channelId
     */
    public void closeChannel(final int channelId) {
        thread.closeChannel(channelId);
    }

    /**
     *
     */
    public void shutdown() {
        thread.shutdown();
    }

    /**
     * @param channelId
     * @return
     */
    public int getLocalPort(final int channelId) {
        return thread.getLocalPort(channelId);
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.system.net.SelectorCallBack#newConnection(int, java.nio.channels.SocketChannel,
     * java.lang.Object)
     */
    @Override
    public void newConnection(final int channelIdOfServer, final SocketChannel newChannel, final Object attachment) {
        final ChannelAttachment channelAttachment = (ChannelAttachment) attachment;
        channelAttachment.callBack.newConnection(channelIdOfServer, newChannel, channelAttachment.clientAttachment);
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.system.net.SelectorCallBack#receive(int, java.nio.ByteBuffer, int, java.lang.Object)
     */
    @Override
    public boolean receive(final int channelId, final ByteBuffer receivedData, final int valid, final Object attachment) {
        final ChannelAttachment channelState = (ChannelAttachment) attachment;
        final ByteArrayBuffer buffer = channelState.buffer;

        byte[] data;
        if (receivedData.hasArray()) {
            data = receivedData.array();
        } else {
            data = new byte[valid];
            receivedData.get(data, 0, valid);
        }

        buffer.write(data, 0, valid);

        boolean ret = true;

        while (true) {
            if (channelState.state == ChannelState.LENGTH && buffer.readableSize() >= 4) {
                channelState.dataSize = buffer.getInt();
                channelState.state = ChannelState.DATA;
            } else if (channelState.state == ChannelState.DATA && buffer.readableSize() >= channelState.dataSize) {
                Object obj = buffer.getObject(channelState.dataSize);
                ret = channelState.callBack.receive(channelId, obj, channelState.clientAttachment);
                channelState.state = ChannelState.LENGTH;
            } else {
                break;
            }
        }
        buffer.compact();
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.system.net.SelectorCallBack#send(int, java.lang.Object)
     */
    @Override
    public ByteBuffer send(final int channelId, final Object attachment) {
        final ChannelAttachment channelAttachment = (ChannelAttachment) attachment;
        Object obj = channelAttachment.callBack.send(channelId, channelAttachment.clientAttachment);

        if (obj == null) {
            return null;
        }

        byte[] data = SerializeUtil.serializeToBytes(obj);
        byte[] length = ByteConverter.getByteArrayOfInt(data.length);
        ByteBuffer ret = ByteBuffer.allocate(data.length + length.length);
        ret.put(length);
        ret.put(data);
        ret.rewind();
        return ret;
    }

    /**
     *
     */
    private enum ChannelState {
        LENGTH, DATA
    };

    /**
     *
     */
    private class ChannelAttachment {
        ByteArrayBuffer buffer = new ByteArrayBuffer();
        ChannelState state = ChannelState.LENGTH;
        int dataSize;

        ObjectSelectorCallBack callBack;
        volatile Object clientAttachment;

        ChannelAttachment(final ObjectSelectorCallBack callBack) {
            this.callBack = callBack;
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.net.SelectorCallBack#exceptionOccured(int, java.lang.Object)
     */
    @Override
    public void exceptionOccured(final int channelId, final Object attachment) {
        final ChannelAttachment info = (ChannelAttachment) attachment;
        info.callBack.exceptionOccured(channelId, info.clientAttachment);
    }

    /*
     * (non-Javadoc)
     * @see jp.queuelinker.system.net.SelectorCallBack#fatalError()
     */
    @Override
    public void fatalError() {

    }
}
