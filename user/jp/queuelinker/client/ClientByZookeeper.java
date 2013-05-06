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

package jp.queuelinker.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.server.MasterSignature;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.server.LocalJobStatistics;
import jp.queuelinker.system.server.LocalJobStatistics.LocalVertexStat;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;
import jp.queuelinker.system.zookeeper.command.ClientRequest;
import jp.queuelinker.system.zookeeper.command.ClientRequestAck;
import jp.queuelinker.system.zookeeper.command.client.JobStartRequest;
import jp.queuelinker.system.zookeeper.command.client.JobStartRequestAck;
import jp.queuelinker.system.zookeeper.command.client.JobStopRequest;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * A client implementation by using Zookeeper.
 */
class ClientByZookeeper implements Client, Watcher {

    /**
     * The unique ID of this client.
     */
    private final int clientId;

    /**
     * The signature of the master.
     */
    private final MasterSignature masterSignature;

    /**
     * The signature of this client.
     */
    private final ClientSignature signature;

    /**
     * The connection to the zookeeper server.
     */
    private final ZookeeperConnection zookeeper;

    /**
     * Initializes a client and registers this client to the master.
     * @param connectString connectString for zookeeper.
     * @param sessionTimeout timeout.
     * @throws ClientException thrown if an error occurs.
     */
    ClientByZookeeper(final String connectString, final int sessionTimeout) throws ClientException {
        try {
            zookeeper = new ZookeeperConnection(connectString, sessionTimeout, this);
            masterSignature = (MasterSignature) zookeeper.getObject(
                                                    ZookeeperStrings.masterSignaturePath(), true, null
                                                );
            final String name = zookeeper.create(ZookeeperStrings.clientRegistrationPath() + "/", null,
                                                 Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            clientId = Integer.valueOf(name.substring(name.lastIndexOf('/') + 1));
            signature = new ClientSignature(clientId);

            zookeeper.waitCreation(ZookeeperStrings.clientPath(clientId));
            zookeeper.create(ZookeeperStrings.clientSignaturePath(clientId),
                             signature, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zookeeper.create(ZookeeperStrings.clientHeartPath(clientId),
                             signature, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        } catch (IOException | KeeperException | InterruptedException | NumberFormatException e) {
            throw new ClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getAllWorkers()
     */
    @Override
    public List<WorkerSignature> getAllWorkers() throws ClientException {
        try {
            List<String> paths = zookeeper.getChildren(ZookeeperStrings.workerTopPath(), false);
            ArrayList<WorkerSignature> ret = new ArrayList<>();
            for (String path : paths) {
                final int workerId = Integer.valueOf(path);
                ret.add((WorkerSignature) zookeeper.getObject(
                                              ZookeeperStrings.workerSignaturePath(workerId), false, null)
                                          );
            }
            return ret;
        } catch (KeeperException | InterruptedException | NumberFormatException e) {
            throw new ClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#startJob(
     * jp.queuelinker.client.QueueLinkerJob )
     */
    @Override
    public JobHandle startJob(final QueueLinkerJob job) throws ClientException {
        JobStartRequestAck ack = (JobStartRequestAck) sendRequestAndWait(new JobStartRequest(job));
        return new JobHandle(ack.getJobId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.queuelinker.client.Client#suspendJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void suspendJob(final JobHandle job) throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#resumeJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void resumeJob(final JobHandle handle) throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.queuelinker.client.Client#stopJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void stopJob(final JobHandle job) throws ClientException {
        sendRequestAndWait(new JobStopRequest(job.getJobId()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.queuelinker.client.Client#snapShot(jp.queuelinker.client.JobHandle)
     */
    @Override
    public SnapShotHandle snapShot(final JobHandle job) throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.queuelinker.client.Client#joinJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void joinJob(final JobHandle job) {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getClientSignature()
     */
    @Override
    public ClientSignature getClientSignature() {
        return signature;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getMasterServerSignature()
     */
    @Override
    public MasterSignature getMasterServerSignature() {
        return masterSignature;
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
     * Sends a request and waits until the master processes it.
     * @param request A request to be sent.
     * @return An ack for this request.
     * @throws ClientException Thrown if an error occurred.
     */
    private ClientRequestAck sendRequestAndWait(final ClientRequest request) throws ClientException {
        try {
            String path = zookeeper.create(ZookeeperStrings.clientRequestPath(clientId) + "/", request,
                                           Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            final long requestId = Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
            zookeeper.waitDeletion(path);
            zookeeper.waitCreation(ZookeeperStrings.clientRequestAckPath(clientId, requestId));
            ClientRequestAck ack = (ClientRequestAck) zookeeper.getObject(
                                       ZookeeperStrings.clientRequestAckPath(clientId, requestId), false, null
                                   );
            zookeeper.delete(ZookeeperStrings.clientRequestAckPath(clientId, requestId), -1);
            return ack;
        } catch (KeeperException | InterruptedException e) {
            throw new ClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.queuelinker.client.Client#restore(jp.queuelinker.client.SnapShotHandle
     * )
     */
    @Override
    public JobHandle restore(final SnapShotHandle snapShot) throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getAllSnapShots()
     */
    @Override
    public List<SnapShotHandle> getAllSnapShots() throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getAllJobs()
     */
    @Override
    public List<JobHandle> getAllJobs() throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getJobStatistics(
     * jp.queuelinker.client.JobHandle )
     */
    @Override
    public JobStatistics getJobStatistics(final JobHandle jobHandle) throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getPhysicalGraph(
     * jp.queuelinker.client.JobHandle )
     */
    @Override
    public PhysicalGraph getPhysicalGraph(final JobHandle job) throws ClientException {
        try {
            GlobalPhysicalGraph gpg = (GlobalPhysicalGraph) zookeeper.getObject(
                                        ZookeeperStrings.jobScheduledGraphPath(job.getJobId()), false, null
                                    );
            return new PhysicalGraph(job.getJobId(), gpg);
        } catch (KeeperException | InterruptedException e) {
            throw new ClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#updateStats(
     * jp.queuelinker.client.PhysicalGraph )
     */
    @Override
    public void updateStats(final PhysicalGraph graph) throws ClientException {
        List<WorkerSignature> workers = getAllWorkers();
        HashMap<Integer, LocalJobStatistics> statsMap = new HashMap<>();
        for (WorkerSignature worker : workers) {
            try {
                final LocalJobStatistics localStat = (LocalJobStatistics) zookeeper.getObject(ZookeeperStrings
                        .jobStatsPath(graph.getJobId(), worker.getServerId()), false, null);
                if (localStat == null) {
                    return;
                }
                statsMap.put(worker.getServerId(), localStat);
            } catch (KeeperException | InterruptedException e) {
                throw new ClientException(e);
            }
        }

        Iterator<PhysicalVertex> iter = graph.iteratorOfVertex();
        while (iter.hasNext()) {
            final PhysicalVertex pv = iter.next();
            final LocalJobStatistics stats = statsMap.get(pv.getWorkerSignature().getServerId());
            final List<LocalVertexStat> list = stats.getLocalVertexStat(pv.getPhysicalId());
            long inputCount = 0, outputCount = 0;
            for (LocalVertexStat stat : list) {
                inputCount += stat.getInputCount();
                outputCount += stat.getOutputCount();
            }
            pv.setInputCountStat(inputCount);
            pv.setOutputCountStat(outputCount);
        }
    }
}
