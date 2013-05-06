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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.server.MasterSignature;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.exception.SnapShotException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.server.LocalJobStatistics;
import jp.queuelinker.system.server.MasterCore;
import jp.queuelinker.system.server.MasterServer;
import jp.queuelinker.system.server.WorkerCore;
import jp.queuelinker.system.server.WorkerServer;
import jp.queuelinker.system.server.LocalJobStatistics.LocalVertexStat;
import jp.queuelinker.system.util.SerializeUtil;

/**
 * This is a client for stand alone mode.
 */
final class StandaloneModeClient implements Client, MasterServer, WorkerServer {

    /**
     * MasterCore instance.
     */
    private final MasterCore masterCore;

    /**
     * WorkerCore instance.
     */
    private final WorkerCore workerCore;

    /**
     * ClientSignature of this client.
     */
    private final ClientSignature clientSignature;

    /**
     * HashMap maintaining GlobalPhysicalGraphs of all jobs. The keys are job IDs.
     */
    private final HashMap<Long, GlobalPhysicalGraph> graphs = new HashMap<>();

    /**
     *
     */
    private LocalJobStatistics lastStat;

    /**
     *
     */
    private final ArrayList<SnapShotDescripter> snapshots = new ArrayList<>();

    /**
     *
     */
    private static final String standAloneSnapshotDir = "tmp/standalone";

    /**
     * @throws UnknownHostException If an error occurred during constructing this client.
     */
    StandaloneModeClient() throws UnknownHostException {
        this.masterCore = new MasterCore("localhost", this);
        this.workerCore = new WorkerCore(0, "localhost", InetAddress.getLocalHost(), this);
        this.masterCore.joinWorkerServer(workerCore.getSignature(), workerCore.getSystemInfo());
        this.clientSignature = new ClientSignature(0);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#startJob(jp.queuelinker.client.QueueLinkerJob)
     */
    @Override
    public JobHandle startJob(final QueueLinkerJob job) {
        JobDescriptor jobDesc = masterCore.startJob(job);
        return new JobHandle(jobDesc.getJobId());
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#stopJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void stopJob(final JobHandle jobHandle) {
        JobDescriptor jobDesc = masterCore.findJobDescripter(jobHandle.getJobId());
        masterCore.stopJob(jobDesc.getJobId());
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#suspend(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void suspendJob(final JobHandle jobHandle) {
        JobDescriptor jobDesc = masterCore.findJobDescripter(jobHandle.getJobId());
        masterCore.suspendJob(jobDesc.getJobId());
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#joinJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void joinJob(final JobHandle jobHandle) {
        JobDescriptor jobDesc = masterCore.findJobDescripter(jobHandle.getJobId());
        masterCore.joinJob(jobDesc.getJobId());
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#snapShot(jp.queuelinker.client.JobHandle)
     */
    @Override
    public SnapShotHandle snapShot(final JobHandle jobHandle) throws ClientException {
        JobDescriptor jobDesc = masterCore.findJobDescripter(jobHandle.getJobId());
        try {
            SnapShotDescripter snapShotDesc = masterCore.snapShotJob(jobDesc.getJobId());
            return new SnapShotHandle(snapShotDesc.getSnapShotName());
        } catch (SnapShotException e) {
            throw new ClientException(e);
        }
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#restore(jp.queuelinker.client.SnapShotHandle)
     */
    @Override
    public JobHandle restore(final SnapShotHandle snapShotHandle) throws ClientException {
        SnapShotDescripter snapShotDesc = masterCore.findSnapShotDescripter(snapShotHandle.getName());
        JobDescriptor jobDesc;
        try {
            jobDesc = masterCore.restoreJob(snapShotDesc);
        } catch (RestoreException e) {
            throw new ClientException(e);
        }
        return new JobHandle(jobDesc.getJobId());
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getAllSnapShots()
     */
    @Override
    public List<SnapShotHandle> getAllSnapShots() throws ClientException {
        Collection<SnapShotDescripter> descs = masterCore.getAllSnapShots();
        ArrayList<SnapShotHandle> ret = new ArrayList<>(descs.size());
        for (SnapShotDescripter desc : descs) {
            ret.add(new SnapShotHandle(desc.getSnapShotName()));
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.server.WorkerServer#getRemoteAddress(int, int, int)
     */
    @Override
    public SocketAddress getRemoteAddress(final long jobId, final int globalVertexId, final int logicalQueueId) {
        return null;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.WorkerServer#sendStat(jp.queuelinker.system.job.JobDescriptor,
     * jp.queuelinker.server.LocalJobStatistics)
     */
    @Override
    public void sendStat(final JobDescriptor jobDesc, final LocalJobStatistics stat) {
        this.lastStat = stat;
    }

    /**
     * @return
     */
    public LocalJobStatistics getStats() {
        return lastStat;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#deployLocalModules(jp.queuelinker.system.job.JobDescriptor,
     * jp.queuelinker.system.graphs.GlobalPhysicalGraph)
     */
    @Override
    public void deployLocalModules(final JobDescriptor jobDesc, final GlobalPhysicalGraph graph) {
        try {
            workerCore.deployJob(jobDesc, graph);
            workerCore.startJob(jobDesc);
            graphs.put(jobDesc.getJobId(), graph);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#prepareAccepting(jp.queuelinker.system.graphs.GlobalPhysicalGraph)
     */
    @Override
    public void prepareAccepting(final GlobalPhysicalGraph graph) {
        // Nothing to do.
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#connectRemoteWorkers(jp.queuelinker.system.graphs.GlobalPhysicalGraph)
     */
    @Override
    public void connectRemoteWorkers(final GlobalPhysicalGraph graph) {
        // Nothing to do.
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getClientSignature()
     */
    @Override
    public ClientSignature getClientSignature() {
        return clientSignature;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getMasterServerSignature()
     */
    @Override
    public MasterSignature getMasterServerSignature() {
        return masterCore.getSignature();
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getAllWorkers()
     */
    @Override
    public List<WorkerSignature> getAllWorkers() throws ClientException {
        ArrayList<WorkerSignature> ret = new ArrayList<>();
        ret.add(workerCore.getSignature());
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.client.Client#getAllJobs()
     */
    @Override
    public List<JobHandle> getAllJobs() throws ClientException {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#getJobStatistics(jp.queuelinker.client.JobHandle)
     */
    @Override
    public JobStatistics getJobStatistics(final JobHandle jobHandle) {
        final JobDescriptor jobDesc = masterCore.findJobDescripter(jobHandle.getJobId());
        return masterCore.getJobStatistics(jobDesc);
    }


    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#getStatistics(jp.queuelinker.system.job.JobDescriptor,
     * java.util.Collection)
     */
    @Override
    public List<LocalJobStatistics> getStatistics(final JobDescriptor jobDesc,
                                                  final Collection<WorkerSignature> workers) {
        assert (workers.size() == 1);
        ArrayList<LocalJobStatistics> ret = new ArrayList<>();
        ret.add(workerCore.getJobStatistics(jobDesc));
        return ret;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#getPhysicalGraph(jp.queuelinker.client.JobHandle)
     */
    @Override
    public PhysicalGraph getPhysicalGraph(final JobHandle job) throws ClientException {
        return new PhysicalGraph(job.getJobId(), masterCore.getGlobalPhysicalGraph(masterCore.findJobDescripter(job
                .getJobId())));
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#updateStats(jp.queuelinker.client.PhysicalGraph)
     */
    @Override
    public void updateStats(final PhysicalGraph graph) throws ClientException {
        List<WorkerSignature> workers = getAllWorkers();
        HashMap<Integer, LocalJobStatistics> statsMap = new HashMap<>();
        for (WorkerSignature worker : workers) {
            final LocalJobStatistics localStat = workerCore.getJobStatistics(masterCore.findJobDescripter(graph
                    .getJobId()));
            if (localStat == null) {
                return;
            }
            statsMap.put(worker.getServerId(), localStat);
        }

        Iterator<PhysicalVertex> iter = graph.iteratorOfVertex();
        while (iter.hasNext()) {
            final PhysicalVertex pv = iter.next();
            final LocalJobStatistics stats = statsMap.get(pv.getWorkerSignature().getServerId());
            final List<LocalVertexStat> list = stats.getLocalVertexStat(pv.getPhysicalId());
            long inputCount = pv.getInputCountStat(), outputCount = pv.getOutputCountStat();
            for (LocalVertexStat stat : list) {
                inputCount += stat.getInputCount();
                outputCount += stat.getOutputCount();
            }
            pv.setInputCountStat(inputCount);
            pv.setOutputCountStat(outputCount);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.server.MasterServer#getSnapShotDescripters()
     */
    @Override
    public List<SnapShotDescripter> getSnapShotDescripters() {
        return Collections.unmodifiableList(snapshots);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#doSnapShot(jp.queuelinker.system.job.SnapShotDescripter,
     * jp.queuelinker.system.job.JobDescriptor, jp.queuelinker.system.graphs.GlobalPhysicalGraph)
     */
    @Override
    public void doSnapShot(final SnapShotDescripter snapDesc, final JobDescriptor jobDesc,
                           final GlobalPhysicalGraph graph) throws SnapShotException {
        File file = new File(getSnapShotDir(snapDesc));
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new SnapShotException(null);
            }
        }

        try {
            SerializeUtil.storeObject(snapDesc, new File(getSnapShotDescDir() + "/" + snapDesc.getSnapShotName()));
            SerializeUtil.storeObject(jobDesc.getJob(), getQueueLinkerJobFile(snapDesc));
            SerializeUtil.storeObject(graph, getGlobalPhysicalGraphFile(snapDesc));
        } catch (IOException e) {
            throw new SnapShotException(e);
        }

        workerCore.snapShotJob(jobDesc, snapDesc);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#restoreQueueLinkerJob(jp.queuelinker.system.job.SnapShotDescripter)
     */
    @Override
    public QueueLinkerJob restoreQueueLinkerJob(final SnapShotDescripter snapShotDesc) throws RestoreException {
        try {
            return (QueueLinkerJob) SerializeUtil.restoreObject(getQueueLinkerJobFile(snapShotDesc));
        } catch (ClassNotFoundException | IOException e) {
            throw new RestoreException(e);
        }
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#restoreGlobalGraph(jp.queuelinker.system.job.SnapShotDescripter)
     */
    @Override
    public GlobalPhysicalGraph restoreGlobalGraph(final SnapShotDescripter snapShotDesc) throws RestoreException {
        try {
            return (GlobalPhysicalGraph) SerializeUtil.restoreObject(getGlobalPhysicalGraphFile(snapShotDesc));
        } catch (ClassNotFoundException | IOException e) {
            throw new RestoreException(e);
        }
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#doRestore(jp.queuelinker.system.job.SnapShotDescripter,
     * jp.queuelinker.system.job.JobDescriptor, jp.queuelinker.system.graphs.GlobalPhysicalGraph)
     */
    @Override
    public void doRestore(final SnapShotDescripter snapShotDesc, final JobDescriptor jobDesc,
                          final GlobalPhysicalGraph graph) throws RestoreException {
        try {
            workerCore.restoreJob(jobDesc, graphs.get(jobDesc.getJobId()), snapShotDesc);
        } catch (ClassNotFoundException | IOException e) {
            throw new RestoreException(e);
        }
    }

    /**
     * @param snapShotDesc
     * @return
     */
    private File getQueueLinkerJobFile(final SnapShotDescripter snapShotDesc) {
        return new File(getSnapShotDir(snapShotDesc) + "/job");
    }

    /**
     * @param snapShotDesc
     * @return
     */
    private File getGlobalPhysicalGraphFile(final SnapShotDescripter snapShotDesc) {
        return new File(getSnapShotDir(snapShotDesc) + "/global_graph");
    }

    /**
     * @param snapShotDesc
     * @return
     */
    private String getSnapShotDir(final SnapShotDescripter snapShotDesc) {
        return standAloneSnapshotDir + "/" + snapShotDesc.getSnapShotName();
    }

    /**
     * @return
     */
    private String getSnapShotDescDir() {
        return standAloneSnapshotDir + "/descs";
    }


    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#execute(jp.queuelinker.system.job.JobDescriptor)
     */
    @Override
    public void execute(final JobDescriptor jobDesc) {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#sendStopJobCommand(jp.queuelinker.system.job.JobDescriptor)
     */
    @Override
    public void sendStopJobCommand(final JobDescriptor jobDesc) {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#sendSuspendJobCommand(jp.queuelinker.system.job.JobDescriptor)
     */
    @Override
    public void sendSuspendJobCommand(final JobDescriptor jobDesc) {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.server.MasterServer#joinJob(jp.queuelinker.system.job.JobDescriptor)
     */
    @Override
    public void joinJob(final JobDescriptor jobDesc) {
        throw new RuntimeException("Now being ported from a previous version.");
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.client.Client#resumeJob(jp.queuelinker.client.JobHandle)
     */
    @Override
    public void resumeJob(final JobHandle handle) throws ClientException {
        throw new RuntimeException("Now being ported from a previous version.");
    }
}
