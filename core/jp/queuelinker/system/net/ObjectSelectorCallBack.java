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

import java.nio.channels.SocketChannel;

/**
 *
 */
public interface ObjectSelectorCallBack {
    /**
     * @param channelId
     * @param obj
     * @param attachment
     * @return
     */
    boolean receive(int channelId, Object obj, Object attachment);

    /**
     * @param channelId
     * @param attachment
     * @return
     */
    Object send(int channelId, Object attachment);

    /**
     * @param channelIdOfServer
     * @param newChannel
     * @param attachment
     */
    void newConnection(int channelIdOfServer, SocketChannel newChannel, Object attachment);

    /**
     * @param channelId
     * @param attachment
     */
    void exceptionOccured(int channelId, Object attachment);

    /**
     *
     */
    void fatalError();
}
