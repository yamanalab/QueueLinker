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

package jp.queuelinker.system.zookeeper.command.master;

import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.zookeeper.MasterByZookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * The MasterWatchHandler watches master orders.
 */
public abstract class MasterWatchHandler implements Watcher {
    /**
     *
     */
    protected final MasterByZookeeper master;

    /**
     *
     */
    protected final ZookeeperConnection zookeeper;

    /**
     *
     */
    protected WatchedEvent event;

    /**
     * @param master
     */
    public MasterWatchHandler(final MasterByZookeeper master) {
        this.master = master;
        this.zookeeper = master.getZookeeperConnection();
    }

    /* (non-Javadoc)
     * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    @Override
    public final void process(final WatchedEvent event) {
        this.event = event;
        master.addNewEvent(this);
    }

    /**
     * Processes a new event.
     * @throws KeeperException
     * @throws InterruptedException
     */
    public abstract void execute() throws KeeperException, InterruptedException;
}
