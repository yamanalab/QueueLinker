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

package jp.queuelinker.server.main;

import java.io.IOException;
import java.net.InetAddress;

import jp.queuelinker.system.zookeeper.WorkerByZooKeeper;

import org.apache.zookeeper.KeeperException;

/**
 * This class executes a worker.
 */
public final class WorkerServerMain {
    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            // TODO This may return the loop back address
            // if we use Linux and the primary IP address in the /etc/hosts file is the loop back address.
            InetAddress address = InetAddress.getLocalHost();
            WorkerByZooKeeper worker = new WorkerByZooKeeper("localhost:2181", 30000,
                                                             address.getHostName(), address);
            worker.start();
        } catch (IOException | KeeperException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
