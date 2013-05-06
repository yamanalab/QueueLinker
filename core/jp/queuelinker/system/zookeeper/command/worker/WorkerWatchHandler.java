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

package jp.queuelinker.system.zookeeper.command.worker;

import java.io.IOException;

import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.zookeeper.WorkerByZooKeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 *
 */
public abstract class WorkerWatchHandler implements Watcher {
    /**
     *
     */
    protected final WorkerByZooKeeper worker;

    /**
     *
     */
    protected final ZookeeperConnection zookeeper;

    /**
     *
     */
    protected WatchedEvent event;

    /**
     * @param worker
     * @param zookeeper
     */
    public WorkerWatchHandler(WorkerByZooKeeper worker, ZookeeperConnection zookeeper) {
        this.worker = worker;
        this.zookeeper = zookeeper;
    }

    /* (non-Javadoc)
     * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    @Override
    public void process(WatchedEvent event) {
        this.event = event;
        worker.addNewEvent(this);
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    public void initialExecute() throws KeeperException, InterruptedException, IOException {
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    public abstract void execute() throws KeeperException, InterruptedException, IOException;
}
