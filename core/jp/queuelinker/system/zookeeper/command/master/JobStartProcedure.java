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

import java.util.Collection;
import java.util.List;

import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.JobStatus;
import jp.queuelinker.system.job.JobStatus.Status;
import jp.queuelinker.system.zookeeper.MasterByZookeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * The JobStartProcedure class executes a job. This is a little bit tricky.
 */
public final class JobStartProcedure extends MasterWatchHandler {
    /**
     * The JobDescriptor of the job.
     */
    private final JobDescriptor job;

    /**
     * The GlobalPhysicalGraph representing the deployment of this job.
     */
    private final GlobalPhysicalGraph graph;

    /**
     * The status of the job.
     */
    private final JobStatus jobStatus;

    /**
     * The number of workers that will involve in this job.
     */
    private final int workerCount;

    /**
     * The job Id.
     */
    private int jobId;   // TODO this must be long.

    /**
     * @param master The instance of the master.
     * @param job
     * @param graph
     */
    public JobStartProcedure(final MasterByZookeeper master, final JobDescriptor job, final GlobalPhysicalGraph graph) {
        super(master);
        this.job = job;
        this.graph = graph;
        this.jobStatus = new JobStatus();
        this.workerCount = graph.getWorkerCount();
    }

    /**
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void initialExecute() throws InterruptedException, KeeperException {
        jobId = zookeeper.getAndIncrementAtomicInt(ZookeeperStrings.nextJobIdPath());

        if (jobId != job.getJobId()) {
            throw new AssertionError(String.format("BUG: The job ID is inconsistent. %d %d", jobId, job.getJobId()));
        }

        zookeeper.create(ZookeeperStrings.jobPath(jobId), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.createAtomicInt(ZookeeperStrings.jobReadyCountdownLatch(jobId),
                                  Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.jobVerticesPath(jobId), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.jobScheduledGraphPath(jobId),
                         graph, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.jobStatusPath(jobId), jobStatus, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.jobDescPath(jobId), job, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zookeeper.create(ZookeeperStrings.jobStatsTopPath(jobId), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        Collection<WorkerSignature> workers = graph.getAllWorkers();
        for (WorkerSignature worker : workers) {
            zookeeper.create(ZookeeperStrings.jobStatsPath(jobId, worker.getServerId()),
                             null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        List<String> children = zookeeper.getChildren(ZookeeperStrings.jobVerticesPath(jobId), this);
        if (children.size() != 0) {
            execute();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see jp.queuelinker.server.zookeeper.MasterWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException {
        if (event == null || event.getPath().equals(ZookeeperStrings.jobVerticesPath(jobId))) {
            connect();
        } else if (event.getPath().equals(ZookeeperStrings.jobReadyCountdownLatch(jobId))) {
            fire();
        }
    }

    /**
     * Starts the network connection.
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void connect() throws KeeperException, InterruptedException {
        List<String> children = zookeeper.getChildren(ZookeeperStrings.jobVerticesPath(jobId), this);
        if (children.size() == graph.nodeCount()) {
            jobStatus.setStatus(Status.CONNECTING);
            zookeeper.setObject(ZookeeperStrings.jobStatusPath(jobId), jobStatus, -1);
        }
        fire();
    }

    /**
     * Executes the job.
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void fire() throws KeeperException, InterruptedException {
        final int value = zookeeper.getAtomicInt(ZookeeperStrings.jobReadyCountdownLatch(jobId), this, null);
        if (value == workerCount) {
            jobStatus.setStatus(Status.RUNNING);
            zookeeper.setObject(ZookeeperStrings.jobStatusPath(jobId), jobStatus, -1);
        }
    }
}
