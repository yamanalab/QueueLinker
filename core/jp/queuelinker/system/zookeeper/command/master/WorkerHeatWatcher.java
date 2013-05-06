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

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;
import jp.queuelinker.system.zookeeper.MasterByZookeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * This class watches the status of a worker.
 */
public class WorkerHeatWatcher extends MasterWatchHandler {
    /**
     * The Worker ID.
     */
    private final int workerId;

    /**
     * @param master
     * @param workerId
     */
    public WorkerHeatWatcher(final MasterByZookeeper master, final int workerId) {
        super(master);
        this.workerId = workerId;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.zookeeper.command.master.MasterWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException {
        final WorkerSignature signature = (WorkerSignature) zookeeper.getObject(ZookeeperStrings
                .workerSignaturePath(workerId), false, null);
        final WorkerSystemInfo systemInfo = (WorkerSystemInfo) zookeeper.getObject(ZookeeperStrings
                .workerSystemInfoPath(workerId), false, null);
        if (event == null || event.getType() == EventType.NodeCreated) {
            master.joinWorkerServer(signature, systemInfo);
            if (event != null) {
                // We have to re-register the watcher again.
                if (zookeeper.exists(ZookeeperStrings.workerHeartPath(workerId), this) == null) {
                    master.leaveWorkerServer(signature);
                }
            }
        } else if (event.getType() == EventType.NodeDeleted) {
            master.leaveWorkerServer(signature);
        }
    }
}
