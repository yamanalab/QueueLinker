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
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.system.util.ByteArrayBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelectorThread implements Runnable {
    private final static Log logger = LogFactory.getLog(SelectorThread.class);

    private final static int BUFFER_SIZE = 65536;
    private final static int WRITE_SIZE = 128;
    private final static int HEURISTIC_WAIT = 10000;

    private final Selector selector;

    private final CopyOnWriteArrayList<ChannelState> channels = new CopyOnWriteArrayList<ChannelState>();

    private final LinkedBlockingQueue<ReActivationInfo> requests = new LinkedBlockingQueue<ReActivationInfo>();

    private final Thread thread;

    private volatile boolean stopRequested;

    public SelectorThread() throws IOException {
        this.selector = Selector.open();
        this.thread = new Thread(this);
    }

    public SelectorThread(final String threadName) throws IOException {
        this.selector = Selector.open();
        this.thread = new Thread(this, threadName);
    }

    /***
     * Start this selector's thread as a daemon thread.
     */
    public void start() {
        start(true);
    }

    /***
     * Start this selector's thread.
     * @param daemnon true if the thread should be a daemon, otherwise false.
     */
    public void start(final boolean daemnon) {
        this.thread.setDaemon(daemnon);
        this.thread.start();
    }

    /***
     * Start a channel.
     * @param channelId
     * @param interestOps
     * @throws ClosedChannelException
     */
    public void startChannel(final int channelId, final int interestOps) throws ClosedChannelException {
        final ChannelState state = channels.get(channelId);
        requests.add(new ReActivationInfo(state, interestOps, ActivationMode.REGISTER));
        selector.wakeup();
    }

    public long getThreadId() {
        return thread.getId();
    }

    public int openServerSocket(final InetAddress bindAddress, final SelectorCallBack callBack) throws IOException {
        return openServerSocket(bindAddress, 0, callBack);
    }

    public int openServerSocket(final InetAddress bindAddress, final int port, final SelectorCallBack callBack) throws IOException {
        return openServerSocket(new InetSocketAddress(bindAddress, port), callBack);
    }

    public int openServerSocket(final InetSocketAddress bindAddress, final SelectorCallBack callBack) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().setReuseAddress(true);
        serverChannel.socket().bind(bindAddress);
        return addSocketChannel(serverChannel, callBack);
    }

    public int connectChannel(final SocketAddress remoteAddress, final SelectorCallBack callBack) throws IOException {
        SocketChannel socketChannel = SocketChannel.open(remoteAddress);
        socketChannel.configureBlocking(false);
        // socketChannel.socket().setTcpNoDelay(true);
        socketChannel.socket().setReuseAddress(true);
        return addSocketChannel(socketChannel, callBack);
    }

    public void closeChannel(final int channelId) {
        cleanUpChannel(channelId);
    }

    /***
     * The channel must be configured non blocking mode.
     * @param channel
     * @param callBack
     * @return channel id
     */
    public int addSocketChannel(final SelectableChannel channel, final SelectorCallBack callBack) {
        int nextChannelId;
        synchronized (channels) {
            nextChannelId = channels.indexOf(null);
            if (nextChannelId != -1) {
                channels.set(nextChannelId, new ChannelState(nextChannelId, channel, callBack));
            } else {
                nextChannelId = channels.size();
                channels.add(new ChannelState(nextChannelId, channel, callBack));
            }
        }
        return nextChannelId;
    }

    public void attach(final int channelId, final Object attachement) {
        channels.get(channelId).attachment = attachement;
    }

    public Object getAttachment(final int channelId) {
        return channels.get(channelId).attachment;
    }

    public void activate(final int channelId, final int interestOps) {
        requests.add(new ReActivationInfo(getChannelState(channelId), interestOps, ActivationMode.NEWOPS));
        selector.wakeup();
    }

    public void activateAll(final int interestOps) {
        Iterator<ChannelState> iter = channels.iterator();
        while (iter.hasNext()) {
            final ChannelState state = iter.next();
            requests.add(new ReActivationInfo(state, interestOps, ActivationMode.NEWOPS));
        }
        selector.wakeup();
    }

    public void deactivate(final int channelId, final int interestOps) {
        requests.add(new ReActivationInfo(getChannelState(channelId), interestOps, ActivationMode.CLEAROPS));
    }

    public boolean isServerSocketChannel(final int channelId) {
        return channels.get(channelId).channel instanceof ServerSocketChannel;
    }

    public boolean isSocketChannel(final int channelId) {
        return channels.get(channelId).channel instanceof SocketChannel;
    }

    public int getLocalPort(final int channelId) {
        final SelectableChannel channel = channels.get(channelId).channel;
        if (channel instanceof ServerSocketChannel) {
            return ((ServerSocketChannel) channel).socket().getLocalPort();
        } else {
            return ((SocketChannel) channel).socket().getLocalPort();
        }
    }

    public InetAddress getInetAddress(final int channelId) {
        final SelectableChannel channel = channels.get(channelId).channel;
        if (channel instanceof ServerSocketChannel) {
            return ((ServerSocketChannel) channel).socket().getInetAddress();
        } else {
            return ((SocketChannel) channel).socket().getInetAddress();
        }
    }

    public SocketAddress getLocalSocketAddress(final int channelId) {
        final SelectableChannel channel = channels.get(channelId).channel;
        if (channel instanceof ServerSocketChannel) {
            return ((ServerSocketChannel) channel).socket().getLocalSocketAddress();
        } else {
            return ((SocketChannel) channel).socket().getLocalSocketAddress();
        }
    }

    public void shutdown() {
        stopRequested = true;
        selector.wakeup();

        while (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        finalCleanUp();
    }

    private void finalCleanUp() {
        Iterator<SelectionKey> iter = selector.keys().iterator();
        while (iter.hasNext()) {
            SelectionKey key = iter.next();
            try {
                key.channel().close();
            } catch (IOException e) {
                logger.info("An IOException happened while closing a channel: " + key.channel().toString());
            }
        }

        try {
            selector.close();
        } catch (IOException e) {
            logger.info("An IOException happened while closing a selector");
        }
    }

    private void cleanUpChannel(final int channelId) {
        final ChannelState state = getChannelState(channelId);

        state.key.cancel();
        try {
            state.channel.close();
        } catch (IOException e) {
            // Ah, what's this? Just continue.
            logger.debug("An IOException happened while closing a socket: " + state.channel.toString());
        }

        // The channel will no longer be used. Delete it.
        channels.set(channelId, null);
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        // I believe the inner try-catches does not cause overhead.
        // http://stackoverflow.com/questions/141560/
        long sendCount = 0;

        SocketChannel currentChannel;
        SelectionKey key = null;
        while (true) {
            try {
                selector.select();
                // selector.selectNow();
            } catch (ClosedSelectorException e) {
                logger.fatal("BUG: The selector is closed.");
                return;
            } catch (IOException e) {
                logger.fatal("An IOException occured while calling select().");
                // Fatal Error. Notify the error to the users and leave the matter to them.
                for (ChannelState state : channels) {
                    state.callBack.fatalError();
                }
                return;
            }

            if (!requests.isEmpty()) {
                handleRequest();
                continue;
            }

            if (stopRequested) {
                return;
            }

            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iter = keys.iterator();
            iter_loop: while (iter.hasNext()) {
                key = iter.next();
                iter.remove();  // Required. Don't remove.

                if (key.isReadable()) {
                    currentChannel = (SocketChannel) key.channel();
                    final ChannelState state = (ChannelState) key.attachment();

                    int valid;
                    try {
                        valid = currentChannel.read(buffer);
                    } catch (IOException e) {
                        logger.warn("An IOException happened while reading from a channel.");
                        state.callBack.exceptionOccured(state.channelId, state.attachment);
                        key.cancel();
                        continue;
                    }
                    if (valid == -1) {
                        // Normal socket close?
                        state.callBack.exceptionOccured(state.channelId, state.attachment);
                        // cleanUpChannel(state.channelId);
                        key.cancel();
                        continue;
                    }

                    buffer.rewind();
                    if (state.callBack.receive(state.channelId, buffer, valid, state.attachment) == false) {
                        state.key.interestOps(state.key.interestOps() & ~SelectionKey.OP_READ);
                    }
                    buffer.clear();
                } else if (key.isWritable()) {
                    currentChannel = (SocketChannel) key.channel();
                    final ChannelState state = (ChannelState) key.attachment();

                    while (state.sendBuffer.readableSize() < WRITE_SIZE) {
                        ByteBuffer newBuffer = state.callBack.send(state.channelId, state.attachment);
                        if (newBuffer != null) {
                            state.sendBuffer.write(newBuffer);
                            if (++sendCount % 50000 == 0) {
                                logger.info("Send Count: " + sendCount);
                            }
                        } else if (state.sendBuffer.readableSize() == 0) {
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                            continue iter_loop;
                        } else {
                            break;
                        }
                    }

                    final int available = state.sendBuffer.readableSize();
                    if (available >= WRITE_SIZE || ++state.noopCount >= HEURISTIC_WAIT) {
                        int done;
                        try {
                            done = currentChannel.write(state.sendBuffer.getByteBuffer());
                        } catch (IOException e) {
                            logger.warn("An IOException occured while writing to a channel.");
                            state.callBack.exceptionOccured(state.channelId, state.attachment);
                            key.cancel();
                            continue;
                        }
                        if (done < available) {
                            state.sendBuffer.rollback(available - done);
                        }
                        state.sendBuffer.compact();
                        state.noopCount = 0;
                    }
                } else if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    ChannelState state = (ChannelState) key.attachment();
                    SocketChannel socketChannel;
                    try {
                        socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                    } catch (IOException e) {
                        continue; // Do nothing.
                    }
                    state.callBack.newConnection(state.channelId, socketChannel, state.attachment);
                }
            }
        }
    }

    private void handleRequest() {
        while (!requests.isEmpty()) {
            final ReActivationInfo info = requests.poll();
            if (info.mode == ActivationMode.NEWOPS) {
                try {
                    info.state.key.interestOps(info.state.key.interestOps() | info.newInterestOps);
                } catch (CancelledKeyException e) { }
            } else if (info.mode == ActivationMode.REGISTER) {
                try {
                    info.state.key = info.state.channel.register(selector, info.newInterestOps, info.state);
                } catch (ClosedChannelException e) { }
            } else if (info.mode == ActivationMode.CLOSE) {
                info.state.key.cancel();
                try {
                    info.state.channel.close();
                } catch (IOException e) { }
            } else if (info.mode == ActivationMode.CLEAROPS) {
                try {
                    info.state.key.interestOps(info.state.key.interestOps() & ~info.newInterestOps);
                } catch (CancelledKeyException e) { }
            }
        }
    }

    private ChannelState getChannelState(final int channelId) {
        ChannelState state = channels.get(channelId);
        if (state == null) {
            new IllegalArgumentException("The channelId is not valid: " + channelId);
        }
        return state;
    }

    private class ChannelState {
        final int channelId;
        final SelectableChannel channel;
        final SelectorCallBack callBack;
        SelectionKey key;
        ByteArrayBuffer sendBuffer = new ByteArrayBuffer();
        int noopCount;
        volatile Object attachment;

        ChannelState(final int channelId, final SelectableChannel channel, final SelectorCallBack callBack) {
            this.channelId = channelId;
            this.channel = channel;
            this.callBack = callBack;
        }
    }

    private enum ActivationMode {
        REGISTER, NEWOPS, CLEAROPS, CLOSE
    }

    private class ReActivationInfo {
        final ChannelState state;
        final int newInterestOps;
        final ActivationMode mode;

        ReActivationInfo(final ChannelState state, final int newInterestOps, final ActivationMode mode) {
            this.state = state;
            this.newInterestOps = newInterestOps;
            this.mode = mode;
        }
    }
}
