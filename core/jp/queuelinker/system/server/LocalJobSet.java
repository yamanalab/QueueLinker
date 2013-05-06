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

package jp.queuelinker.system.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.SnapShottable;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalVertex;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.JobCollection;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.sched.LocalSchedulerManager;
import jp.queuelinker.system.thread.ThreadContextBase;
import jp.queuelinker.system.thread.ThreadUnit;
import jp.queuelinker.system.util.ReflectionUtil;

/**
 * The LocalJobSet maintains the jobs existing in a worker server.
 */
final class LocalJobSet {

    /**
     * The instance of JobCollection.
     */
    private final JobCollection jobCollection = JobCollection.getInstance();

    /**
     * The instance of LocalJobInfo.
     */
    private final LocalJobInfo jobInfo = LocalJobInfo.getInstance();

    /**
     * The instance of LocalSchedulerManager.
     */
    private final LocalSchedulerManager scheduler;

    /**
     * The instance of SnapShotFileManager.
     */
    private final SnapShotFileManager snapShotManager = new SnapShotFileManager("tmp/standalone");

    /**
     * @param workerCore
     */
    public LocalJobSet(final WorkerCore workerCore) {
        this.scheduler = new LocalSchedulerManager(workerCore);
    }

    /**
     * @param jobDesc
     * @param graph
     * @return
     * @throws IOException
     */
    public LocalPhysicalGraph deployJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph graph)
            throws IOException {
        jobCollection.addNewJob(jobDesc);
        LocalPhysicalGraph localGraph = scheduler.deployJob(jobDesc, graph);
        return localGraph;
    }

    /**
     * @param jobDesc
     * @param gpg
     * @param snapShot
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RestoreException
     */
    public void restoreJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph gpg, final SnapShotDescripter snapShot)
            throws IOException, ClassNotFoundException, RestoreException {
        FileInputStream fileIn = new FileInputStream(snapShotManager.getGraphFile(snapShot));
        ObjectInputStream input = new ObjectInputStream(fileIn);
        LocalPhysicalGraph lpg = (LocalPhysicalGraph) input.readObject();
        scheduler.restoreJob(jobDesc, gpg, lpg, snapShot, snapShotManager);
    }

    /**
     * @param job The JobDescriptor of a job to be started.
     */
    public void startJob(final JobDescriptor job) {
        scheduler.startJob(job);
    }

    /**
     * @param job The JobDescriptor of a job to be stopped.
     */
    public void stopJob(final JobDescriptor job) {
        scheduler.stopJob(job);
    }

    /**
     * @param job The JobDescriptor of a job to be suspended.
     */
    public void suspendJob(final JobDescriptor job) {
        scheduler.suspend(job);
    }

    /**
     * @param job The JobDescriptor of a job to be resumed.
     */
    public void resumeJob(final JobDescriptor job) {
        scheduler.resumeJob(job);
    }

    /**
     * @param job
     * @param snapShotDesc
     * @throws IOException
     */
    public void snapShotJob(final JobDescriptor job, final SnapShotDescripter snapShotDesc) throws IOException {
        scheduler.suspend(job);

        final LocalPhysicalGraph graph = jobInfo.getLocalPhysicalGraph(job);
        final Iterator<LocalVertex> iter = graph.iteratorOfVertex();
        while (iter.hasNext()) {
            final LocalVertex lv = iter.next();

            if (!(lv instanceof LocalPhysicalVertex)) {
                continue;
            }

            final LocalPhysicalVertex lpv = (LocalPhysicalVertex) lv;
            if (lpv.isVirtual()) {
                continue;
            }

            final ThreadContextBase context = lpv.getContext();
            final ModuleBase base = context.getModuleBase();

            if (ReflectionUtil.isImplemented(base.getClass(), SnapShottable.class)) {
                FileOutputStream fileOut = new FileOutputStream(snapShotManager.getVertexFile(snapShotDesc, lpv));
                ObjectOutputStream output = new ObjectOutputStream(fileOut);
                context.snapShotModule(output);
                output.close();
            }
        }

        FileOutputStream fileOut = new FileOutputStream(snapShotManager.getGraphFile(snapShotDesc));
        ObjectOutputStream output = new ObjectOutputStream(fileOut);
        output.writeObject(graph);
        output.close();

        scheduler.resumeJob(job);
    }

    /**
     * @param jobDesc
     * @return
     */
    public LocalPhysicalGraph getLocalPhysicalGraph(final JobDescriptor jobDesc) {
        return jobInfo.getLocalPhysicalGraph(jobDesc);
    }

    /**
     * @param jobDesc
     * @return
     */
    public LocalJobStatistics getJobStatistics(final JobDescriptor jobDesc) {
        LocalPhysicalGraph lpg = jobInfo.getLocalPhysicalGraph(jobDesc);
        Iterator<LocalVertex> iter = lpg.iteratorOfVertex();

        LocalJobStatistics stats = new LocalJobStatistics();

        while (iter.hasNext()) {
            final LocalVertex lv = iter.next();

            if (!(lv instanceof LocalPhysicalVertex)) {
                continue;
            }

            final ThreadContextBase context = lv.getContext();
            final ThreadUnit unit = lv.getThreadUnit();
            final LocalPhysicalVertex lpv = (LocalPhysicalVertex) lv;

            stats.addStat(lpv.getGlobalVertexId(), lpv.getLocalPartitionId(), unit.getInputQueueTotalSize(),
                          context.getWaitingItemCount(), context.getInputCount(), context.getOutputCount(), 0);
        }

        return stats;
    }
}
