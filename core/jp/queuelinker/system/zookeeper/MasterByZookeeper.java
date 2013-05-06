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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.client.ClientSignature;
import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.server.WorkerSystemInfo;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.exception.SnapShotException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.server.LocalJobStatistics;
import jp.queuelinker.system.server.MasterCore;
import jp.queuelinker.system.server.MasterServer;
import jp.queuelinker.system.zookeeper.command.ClientRequest;
import jp.queuelinker.system.zookeeper.command.ClientRequestAck;
import jp.queuelinker.system.zookeeper.command.client.JobSnapShotRequest;
import jp.queuelinker.system.zookeeper.command.client.JobStartRequest;
import jp.queuelinker.system.zookeeper.command.client.JobStartRequestAck;
import jp.queuelinker.system.zookeeper.command.client.JobStopRequest;
import jp.queuelinker.system.zookeeper.command.client.JobSuspendRequest;
import jp.queuelinker.system.zookeeper.command.master.JobStartProcedure;
import jp.queuelinker.system.zookeeper.command.master.JobStopProcedure;
import jp.queuelinker.system.zookeeper.command.master.JobSuspendProcedure;
import jp.queuelinker.system.zookeeper.command.master.MasterWatchHandler;
import jp.queuelinker.system.zookeeper.command.master.NewClientWatcher;
import jp.queuelinker.system.zookeeper.command.master.NewWorkerWatcher;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * This is a simple implementation of master server by using ZooKeeper.
 */
public final class MasterByZookeeper implements MasterServer, Watcher {

    /**
     * The ZookeeperConnection instance.
     */
    private final ZookeeperConnection zookeeper;

    /**
     * The MasterCore instance.
     */
    private final MasterCore master;

    /**
     * A queue to maintain events.
     */
    private final LinkedBlockingQueue<MasterWatchHandler> events = new LinkedBlockingQueue<>();

    /**
     * This flag will become true when this master has to stop.
     */
    private volatile boolean stopRequested;

    /**
     * @param connectString
     * @param sessionTimeout
     * @throws IOException
     */
    public MasterByZookeeper(final String connectString, final int sessionTimeout) throws IOException {
        this.zookeeper = new ZookeeperConnection(connectString, sessionTimeout, this);
        this.master = new MasterCore(InetAddress.getLocalHost().getHostName(), this);
    }

    /**
     * Initializes and creates a new top node on ZooKeeper.
     * @throws KeeperException Thrown if an error occurs.
     * @throws InterruptedException
     */
    private void createTopNodes() throws KeeperException, InterruptedException {
        handleRemainingData();
        zookeeper.create(ZookeeperStrings.topPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.registrationPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zookeeper.create(ZookeeperStrings.clientTopPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.clientRegistrationPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zookeeper.create(ZookeeperStrings.workerTopPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.workerRegistrationPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zookeeper.create(ZookeeperStrings.jobPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.jobTopPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.finishedJobTopPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zookeeper.create(ZookeeperStrings.atomicPath(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.createAtomicInt(ZookeeperStrings.nextJobIdPath(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void handleRemainingData() throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists("/QueueLinker", false);
        // TODO Currently just drop and recreate it.
        if (stat != null) {
            zookeeper.deleteRecursive("/QueueLinker");
        }
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void registerWatchers() throws KeeperException, InterruptedException {
        zookeeper.getChildren(ZookeeperStrings.workerRegistrationPath(), new NewWorkerWatcher(this));
        zookeeper.getChildren(ZookeeperStrings.clientRegistrationPath(), new NewClientWatcher(this));
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void accept() throws KeeperException, InterruptedException {
        zookeeper.create(ZookeeperStrings.masterSignaturePath(),
                         master.getSignature(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void start() throws KeeperException, InterruptedException {
        createTopNodes();
        registerWatchers();
        accept();

        while (!stopRequested) {
            try {
                final MasterWatchHandler event = events.take();
                System.err.println(event.getClass().getName());
                event.execute();
            } catch (InterruptedException through) {
                assert (stopRequested);
                return;
            }
        }
    }

    /**
     *
     */
    public void shutdown() {
        stopRequested = true;
    }

    /**
     *
     */
    public void join() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void deployLocalModules(final JobDescriptor job, final GlobalPhysicalGraph graph) {
        JobStartProcedure proc = new JobStartProcedure(this, job, graph);
        try {
            proc.initialExecute();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeeperException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void prepareAccepting(final GlobalPhysicalGraph graph) {
        // TODO Auto-generated method stub
    }

    @Override
    public void connectRemoteWorkers(final GlobalPhysicalGraph graph) {
        // TODO Auto-generated method stub
    }

    @Override
    public void execute(final JobDescriptor jobDesc) {
        // TODO Auto-generated method stub

    }

    public void addNewEvent(final MasterWatchHandler event) {
        events.add(event);
    }

    /**
     * @return
     */
    public ZookeeperConnection getZookeeperConnection() {
        return zookeeper;
    }

    /**
     * @param signature
     * @param systemInfo
     */
    public void joinWorkerServer(final WorkerSignature signature, final WorkerSystemInfo systemInfo) {
        master.joinWorkerServer(signature, systemInfo);
    }

    /**
     * @param signature
     */
    public void leaveWorkerServer(final WorkerSignature signature) {
        master.leaveWorkerServer(signature);
    }

    @Override
    public void process(final WatchedEvent event) {
    }

    /**
     * @param signature
     */
    public void joinClient(final ClientSignature signature) {
        master.clientJoin(signature);
    }

    /**
     * @param signature
     */
    public void clientLeft(final ClientSignature signature) {
        master.clientLeft(signature);
    }

    /**
     * Processes a request from a client.
     * @param request The request set by a client.
     */
    public void clientRequest(final ClientRequest request, final int clientId, final long requestId) {
        if (request instanceof JobStartRequest) {
            final JobDescriptor jobDesc = master.startJob(((JobStartRequest) request).newJob);
            sendRequestAck(new JobStartRequestAck(requestId, jobDesc.getJobId()), clientId, requestId);
        } else if (request instanceof JobStopRequest) {
            master.stopJob(((JobStopRequest) request).jobId);
        } else if (request instanceof JobSuspendRequest) {
            master.suspendJob(((JobSuspendRequest) request).jobId);
        } else if (request instanceof JobSnapShotRequest) {
            try {
                master.snapShotJob(((JobSnapShotRequest) request).getJobId());
            } catch (SnapShotException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an ack to a client.
     * @param ack The ack message.
     * @param clientId The client that receives the ack.
     * @param requestId The ID of a request that is just completed.
     */
    private void sendRequestAck(final ClientRequestAck ack, final int clientId, final long requestId) {
        try {
            zookeeper.create(ZookeeperStrings.clientRequestAckPath(clientId, requestId),
                             ack, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            // TODO What should we do?
            e.printStackTrace();
        }
    }

    @Override
    public List<LocalJobStatistics> getStatistics(final JobDescriptor jobDesc,
                                                  final Collection<WorkerSignature> workers) {
        return null;
    }

    @Override
    public List<SnapShotDescripter> getSnapShotDescripters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueueLinkerJob restoreQueueLinkerJob(final SnapShotDescripter snapShotDesc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GlobalPhysicalGraph restoreGlobalGraph(final SnapShotDescripter snapShotDesc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doSnapShot(final SnapShotDescripter snapDesc, final JobDescriptor jobDesc,
                           final GlobalPhysicalGraph graph) throws SnapShotException {
        // TODO Auto-generated method stub
    }

    @Override
    public void doRestore(final SnapShotDescripter snapShotDesc, final JobDescriptor jobDesc,
                          final GlobalPhysicalGraph graph) throws RestoreException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendStopJobCommand(final JobDescriptor jobDesc) {
        JobStopProcedure proc = new JobStopProcedure(this, jobDesc.getJobId());
        try {
            proc.execute();
        } catch (KeeperException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void sendSuspendJobCommand(final JobDescriptor jobDesc) {
        JobSuspendProcedure order = new JobSuspendProcedure(this, jobDesc.getJobId());
    }

    @Override
    public void joinJob(final JobDescriptor jobDesc) {
        // TODO Auto-generated method stub

    }
}
