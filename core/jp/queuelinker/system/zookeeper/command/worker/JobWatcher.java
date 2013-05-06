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
import java.util.HashSet;
import java.util.Iterator;

import jp.queuelinker.system.graphs.GlobalPhysicalEdge;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalEdge;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalVertex;
import jp.queuelinker.system.graphs.LocalSelectorVertex;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.JobStatus;
import jp.queuelinker.system.job.JobStatus.Status;
import jp.queuelinker.system.net.ZookeeperConnection;
import jp.queuelinker.system.sched.VertexRunningInformation;
import jp.queuelinker.system.zookeeper.WorkerByZooKeeper;
import jp.queuelinker.system.zookeeper.ZookeeperStrings;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 *
 */
public class JobWatcher extends WorkerWatchHandler {

    /**
     * The Job ID.
     */
    private final int jobId;

    /**
     * The job descriptor.
     */
    private JobDescriptor jobDesc;

    /**
     * @param jobId
     * @param worker
     * @param zookeeper
     */
    public JobWatcher(final int jobId, final WorkerByZooKeeper worker, final ZookeeperConnection zookeeper) {
        super(worker, zookeeper);
        this.jobId = jobId;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.zookeeper.command.worker.WorkerWatchHandler#initialExecute()
     */
    @Override
    public void initialExecute() throws KeeperException, InterruptedException, IOException {
        if (zookeeper.exists(ZookeeperStrings.jobDescPath(jobId), this) != null) {
            execute();
        }
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.zookeeper.command.worker.WorkerWatchHandler#execute()
     */
    @Override
    public void execute() throws KeeperException, InterruptedException, IOException {
        if (event == null) {
            deployJob();
        } else if (event.getPath().equals(ZookeeperStrings.jobDescPath(jobId))) {
            deployJob();
        } else if (event.getPath().equals(ZookeeperStrings.jobStatusPath(jobId))) {
            checkStatus();
        }
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    private void deployJob() throws KeeperException, InterruptedException, IOException {
        this.jobDesc = (JobDescriptor) zookeeper.getObject(ZookeeperStrings.jobDescPath(jobId), this, null);
        final GlobalPhysicalGraph graph
                = (GlobalPhysicalGraph) zookeeper.getObject(ZookeeperStrings.jobScheduledGraphPath(jobId), false, null);
        final LocalPhysicalGraph lpg = worker.deployJob(jobDesc, graph);

        Iterator<LocalVertex> iter = lpg.iteratorOfVertex();
        HashSet<Integer> done = new HashSet<>();
        while (iter.hasNext()) {
            final LocalVertex lv = iter.next();

            if (lv instanceof LocalPhysicalVertex) {
                final LocalPhysicalVertex lpv = (LocalPhysicalVertex) lv;
                if (done.contains(lpv.getGlobalVertexId())) {
                    continue;
                }

                done.add(lpv.getGlobalVertexId());

                VertexRunningInformation info = new VertexRunningInformation(lpv.getGlobalVertexId(),
                                                                             worker.getInetAddress());

                for (int i = 0; i < lpv.getInputQueueCount(); i++) {
                    if (lpv.hasRemoteSource(i)) {
                        final LocalEdge selectorEdge = lpv.getSourceSelectorEdge(i);
                        final LocalSelectorVertex selectorVertex = (LocalSelectorVertex) selectorEdge.getSrcVertex();
                        info.addAcceptAddress(i, selectorVertex.getAcceptAddress(selectorEdge.getSrcQueueId()));
                        final LocalEdge edge = lpv.getInputEdges(i).get(0);
                        final GlobalPhysicalEdge gpe = edge.getGlobalPhysicalEdge();

                        assert (gpe.getLogicalEdge().getDestQueueHandle().getQueueId() == i);

//                        System.out.printf("A vertex %d (queue %d) will connect to a vertex %d (queue %d) on %s\n",
//                                          gpe.getSrcGlobalVertex().getGlobalPhysicalVertexId(),
//                                          gpe.getSrcQueueLogicalId(),
//                                          lpv.getGlobalVertexId(), i,
//                                          selectorVertex.getAcceptAddress(selectorEdge.getSrcQueueId()).toString());
                    }
                }

                zookeeper.create(ZookeeperStrings.jobVertexInfoPath(jobId, lpv.getGlobalVertexId()),
                                 info, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }

        checkStatus();
    }

    /**
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    private void checkStatus() throws KeeperException, InterruptedException, IOException {
        if (zookeeper.exists(ZookeeperStrings.jobStatusPath(jobId), this) == null) {
            return;
        }

        final JobStatus status = (JobStatus) zookeeper.getObject(ZookeeperStrings.jobStatusPath(jobId), this, null);
        if (status.getStatus() == Status.CONNECTING) {
            worker.startConnecting(jobDesc);
            zookeeper.getAndIncrementAtomicInt(ZookeeperStrings.jobReadyCountdownLatch(jobId));
        } else if (status.getStatus() == Status.RUNNING) {
            worker.startVertices(jobDesc);
        } else if (status.getStatus() == Status.TERMINATING) {
            worker.stopJob(jobDesc);
        }
    }
}
