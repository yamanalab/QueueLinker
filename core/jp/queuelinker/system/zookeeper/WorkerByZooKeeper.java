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

package jp.queuelinker.system.zookeeper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.server.MasterSignature;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;
import jp.queuelinker.system.exception.WorkerException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.sched.VertexRunningInformation;
import jp.queuelinker.system.server.LocalJobStatistics;
import jp.queuelinker.system.server.WorkerCore;
import jp.queuelinker.system.server.WorkerServer;
import jp.queuelinker.system.zookeeper.command.worker.NewJobWatcher;
import jp.queuelinker.system.zookeeper.command.worker.WorkerWatchHandler;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * An implementation of WorkerServer by using ZooKeeper. This class performs
 * only network communication and uses WorkerCore to perform actual worker
 * processing.
 */
public class WorkerByZooKeeper implements WorkerServer, Watcher {

    /**
     * The connection of Zookeeper.
     */
    private final ZookeeperConnection zookeeper;

    /**
     * The WorkerCore that this server uses.
     */
    private final WorkerCore worker;

    /**
     *
     */
    private final LinkedBlockingQueue<WorkerWatchHandler> events = new LinkedBlockingQueue<>();

    /**
     * Creates a worker server with the specified parameters.
     * @param connectString
     * @param sessionTimeout
     * @param hostName The host name of this master.
     * @param address The InetAddress of this master.
     * @throws IOException Thrown if an error occurs.
     * @throws KeeperException
     * @throws InterruptedException
     */
    public WorkerByZooKeeper(final String connectString, final int sessionTimeout,
                             final String hostName, final InetAddress address)
                                     throws IOException, KeeperException, InterruptedException {
        this.zookeeper = new ZookeeperConnection(connectString, sessionTimeout, this);
        this.worker = register(hostName, address);
    }

    /**
     * Registers this worker to the master.
     * @param hostName The host name of the master.
     * @param address
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    private WorkerCore register(final String hostName, final InetAddress address)
                                        throws KeeperException, InterruptedException {
        MasterSignature masterSignature = (MasterSignature) zookeeper.getObject(ZookeeperStrings.masterSignaturePath(),
                                                                                true, null);

        String name = zookeeper.create(ZookeeperStrings.workerRegistrationPath() + "/", null, Ids.OPEN_ACL_UNSAFE,
                                       CreateMode.EPHEMERAL_SEQUENTIAL);
        final int workerId = Integer.valueOf(name.substring(name.lastIndexOf('/') + 1));
        WorkerCore ret = new WorkerCore(workerId, hostName, address, this);

        final WorkerSignature signature = ret.getSignature();
        final WorkerSystemInfo systemInfo = ret.getSystemInfo();

        zookeeper.waitCreation(ZookeeperStrings.workerPath(workerId));

        registerWatcher();

        zookeeper.create(ZookeeperStrings.workerSignaturePath(workerId), signature, Ids.OPEN_ACL_UNSAFE,
                         CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.workerSystemInfoPath(workerId), systemInfo, Ids.OPEN_ACL_UNSAFE,
                         CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.workerHeartPath(workerId), signature, Ids.OPEN_ACL_UNSAFE,
                         CreateMode.EPHEMERAL);

        return ret;
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void registerWatcher() throws KeeperException, InterruptedException {
        zookeeper.getChildren(ZookeeperStrings.jobTopPath(), new NewJobWatcher(this, zookeeper));
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    public void start() throws KeeperException, InterruptedException, IOException {
        while (true) {
            WorkerWatchHandler handler = events.take();
            handler.execute();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    @Override
    public void process(final WatchedEvent event) {
    }

    /**
     * @param event
     */
    public void addNewEvent(final WorkerWatchHandler event) {
        events.add(event);
    }

    /**
     * @param jobDesc
     * @param graph
     * @return
     * @throws IOException
     */
    public LocalPhysicalGraph deployJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph graph)
            throws IOException {
        return worker.deployJob(jobDesc, graph);
    }

    /**
     * @param jobDesc
     * @throws IOException
     */
    public void startConnecting(final JobDescriptor jobDesc) throws IOException {
        worker.startConnecting(jobDesc);
    }

    /**
     * @param jobDesc
     */
    public void startVertices(final JobDescriptor jobDesc) {
        worker.startJob(jobDesc);
    }


    public void stopJob(final JobDescriptor jobDesc) {
        worker.stopJob(jobDesc);
    }


    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.server.WorkerServer#getRemoteAddress(long, int, int)
     */
    @Override
    public SocketAddress getRemoteAddress(final long jobId, final int globalVertexId, final int logicalQueueId) {
        VertexRunningInformation runningInfo;
        // TODO: Handle the exception correctly.
        try {
            runningInfo = (VertexRunningInformation) zookeeper.getObject(ZookeeperStrings
                    .jobVertexInfoPath(jobId, globalVertexId), null, null);
        } catch (KeeperException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return runningInfo.getAcceptSocketAddress(logicalQueueId);
    }

    /**
     * @return
     */
    public InetAddress getInetAddress() {
        return worker.getSignature().getAddress();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.queuelinker.server.WorkerServer#sendStat(jp.queuelinker.system.job
     * .JobDescripter, jp.queuelinker.server.LocalJobStatistics)
     */
    @Override
    public void sendStat(final JobDescriptor jobDesc, final LocalJobStatistics stat) throws WorkerException {
        try {
            zookeeper.setObject(ZookeeperStrings.jobStatsPath(jobDesc.getJobId(), worker.getSignature().getServerId()),
                                stat, -1);
        } catch (KeeperException | InterruptedException e) {
            throw new WorkerException(e);
        }
    }

}
