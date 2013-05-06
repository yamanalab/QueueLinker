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

package jp.queuelinker.system.sched;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalVertex;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.JobCollection;
import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.JobStatus;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.server.LocalJobInfo;
import jp.queuelinker.system.server.SnapShotFileManager;
import jp.queuelinker.system.server.WorkerCore;
import jp.queuelinker.system.thread.ThreadUnit;
import jp.queuelinker.system.thread.ThreadUnitController;
import jp.queuelinker.system.thread.ThreadUnitSet;

public class LocalSchedulerManager {

	private final WorkerCore worker;

	private final LocalJobInfo jobInfo = LocalJobInfo.getInstance();

	private final JobCollection jobCollection = JobCollection.getInstance();

	private final VertexDeploymentManager deploymentor = new VertexDeploymentManager();

	private final SchedulingThread thread;

	private volatile boolean stopRequested;

	public LocalSchedulerManager(final WorkerCore worker) {
		this.worker = worker;
		this.thread = new SchedulingThread();
		this.thread.start();
	}

	public LocalPhysicalGraph deployJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph graph) throws IOException {
		LocalScheduler scheduler = new GeneralLocalScheduler(jobDesc.getJob(), worker.getSignature(), graph);
		LocalPhysicalGraph lpg = scheduler.initialSchedule();
		jobInfo.addNewJob(jobDesc, graph, lpg);
		jobInfo.setJobScheduler(jobDesc, scheduler);
		deploymentor.deploy(jobDesc, lpg);
		this.thread.addStatusChangedJob(jobDesc);
		return lpg;
	}

	public void restoreJob(final JobDescriptor jobDesc, final GlobalPhysicalGraph gpg, final LocalPhysicalGraph lpg,
			final SnapShotDescripter snapShot, final SnapShotFileManager snapShotManager) throws RestoreException, IOException {
		LocalScheduler scheduler = new GeneralLocalScheduler(jobDesc.getJob(), worker.getSignature(), gpg);
		scheduler.restore(lpg, snapShot, snapShotManager);
		jobInfo.addNewJob(jobDesc, gpg, lpg);
		jobInfo.setJobScheduler(jobDesc, scheduler);
		deploymentor.deploy(jobDesc, lpg);
		this.thread.addStatusChangedJob(jobDesc);

		scheduler.jobStarting();
		ThreadUnitSet units = deploymentor.getThreadUnits(jobDesc);
		for (ThreadUnit unit : units.getAllUnits()) {
			ThreadUnitController.getInstance().startThreadUnit(unit);
		}
		scheduler.jobStarted();
	}

	public void startJob(final JobDescriptor job) {
		LocalScheduler scheduler = jobInfo.getJobScheduler(job);
		scheduler.jobStarting();
		ThreadUnitSet units = deploymentor.getThreadUnits(job);
		for (ThreadUnit unit : units.getAllUnits()) {
			ThreadUnitController.getInstance().startThreadUnit(unit);
		}
		scheduler.jobStarted();
	}

	public void stopJob(final JobDescriptor job) {
		LocalScheduler scheduler = jobInfo.getJobScheduler(job);
		scheduler.jobStopping();
		ThreadUnitSet units = deploymentor.getThreadUnits(job);
		for (ThreadUnit unit : units.getAllUnits()) {
			ThreadUnitController.getInstance().stopThreadUnit(unit);
		}
		scheduler.jobStopped();
	}

	public void suspend(final JobDescriptor job) {
		LocalScheduler scheduler = jobInfo.getJobScheduler(job);
		scheduler.jobSuspending();
		ThreadUnitSet units = deploymentor.getThreadUnits(job);
		for (ThreadUnit unit : units.getAllUnits()) {
			ThreadUnitController.getInstance().suspendThreadUnit(unit);
		}
		scheduler.jobSuspended();
	}

	public void resumeJob(final JobDescriptor job) {
		LocalScheduler scheduler = jobInfo.getJobScheduler(job);
		scheduler.jobResuming();
		ThreadUnitSet units = deploymentor.getThreadUnits(job);
		for (ThreadUnit unit : units.getAllUnits()) {
			ThreadUnitController.getInstance().resumeThreadUnit(unit);
		}
		scheduler.jobResumed();
	}

	public void terminate() {
		this.stopRequested = true;
		this.thread.interrupt();
		while (this.thread.isAlive()) {
			try {
				this.thread.join();
			} catch (InterruptedException ignore) {	}
		}
	}

	public class SchedulingThread extends Thread {

		private final static int LOCAL_TICK = 1000;

		private final static int GLOBAL_TICK = 1000 * 1000 * 1000;  // 1s by nano second

		private final LinkedBlockingQueue<JobDescriptor> statusChangedJobs = new LinkedBlockingQueue<>();

		private final ArrayList<JobCache> targetJobs = new ArrayList<>();

		private class JobCache {
			final JobDescriptor jobDesc;
			final LocalPhysicalVertex[] vertices;
			final int[] localVertexCount;
			final LocalScheduler scheduler;
			final LocalPhysicalGraph graph;
			long lastScheduledTime;

			JobCache(final JobDescriptor jobDesc, final LocalPhysicalVertex[] vertices, final int[] localVertexCount, final LocalScheduler scheduler, final LocalPhysicalGraph graph) {
				this.jobDesc = jobDesc;
				this.vertices = vertices;
				this.localVertexCount = localVertexCount;
				this.scheduler = scheduler;
				this.graph = graph;
			}
		}

		private class LocalVertexOrder implements Comparator<LocalPhysicalVertex> {
			@Override
			public int compare(final LocalPhysicalVertex o1, final LocalPhysicalVertex o2) {
				if (o1.getGlobalVertexId() != o2.getGlobalVertexId())
					return o1.getGlobalVertexId() - o2.getGlobalVertexId();
				return o1.getLocalPartitionId() - o2.getLocalPartitionId();
			}
		}

		@Override
		public void run() {
			while (!stopRequested) {
				checkJobStatus();

				for (JobCache job : targetJobs) {
//					final LocalScheduler scheduler = job.scheduler;
//					final VertexStat[] stats = new VertexStat[job.vertices.length];
//					for (int i = 0; i < job.vertices.length; i++) {
//						final LocalPhysicalVertex lv = job.vertices[i];
//						final ThreadContextBase context = lv.getContext();
//						final long cpuCycles = context.getCpuCycles();
//						final long inputCount = context.getInputCount();
//						final long outputCount = context.getOutputCount();
//						context.resetCpuCyelces();
//						context.resetInputCount();
//						context.resetOutputCount();
//
//						stats[i] = new VertexStat(cpuCycles, inputCount, outputCount);
//						scheduler.setOperatorStat(lv, stats[i]);
//					}
//
//					LocalPhysicalGraph newGraph = scheduler.schedule();
//					if (job.graph != newGraph) {
//						deploymentor.changeDeploy(job.graph, newGraph);
//						buildJobCache(job.jobDesc);
//					}

					final long currentNano = System.nanoTime();
					if (currentNano - job.lastScheduledTime >= GLOBAL_TICK) {
//						sendStats(job, stats);
						worker.sendStat(job.jobDesc);
						job.lastScheduledTime = currentNano;
					}
				}

				try {
					Thread.sleep(LOCAL_TICK);
				} catch (InterruptedException ignore) {	}
			}
		}

//		private void sendStats(JobCache jobCache, VertexStat[] stats) {
//			assert(stats.length != 0);
//
//			int lastGlobalId = jobCache.vertices[0].getGlobalVertexId();
//			int localVertexCountIndex = 0;
//			VertexStatInformation info = new VertexStatInformation(
//							lastGlobalId, jobCache.localVertexCount[localVertexCountIndex]
//					);
//
//			for (int i = 0; i < stats.length; i++) {
//				final int globalId = jobCache.vertices[i].getGlobalVertexId();
//				if (lastGlobalId != globalId) {
//					worker.sendStat(jobCache.jobDesc, info);
//					info = new VertexStatInformation(globalId, jobCache.localVertexCount[++localVertexCountIndex]);
//					lastGlobalId = globalId;
//				}
//				info.setStat(jobCache.vertices[i].getLocalPartitionId(), stats[i]);
//			}
//			worker.sendStat(jobCache.jobDesc, info);
//		}

		private void checkJobStatus() {
			while (!statusChangedJobs.isEmpty()) {
				final JobDescriptor jobDesc = statusChangedJobs.poll();
				if (jobCollection.getJobStatus(jobDesc) == JobStatus.Status.RUNNING) {
					final JobCache jobCache = buildJobCache(jobDesc);
					targetJobs.add(jobCache);
				}
				else if (jobCollection.getJobStatus(jobDesc) == JobStatus.Status.TERMINATED) {
					targetJobs.remove(jobDesc);
				}
			}
		}

		private JobCache buildJobCache(final JobDescriptor job) {
			LocalPhysicalGraph graph = jobInfo.getLocalPhysicalGraph(job);
			Collection<LocalVertex> vertices = graph.vertices();
			ArrayList<LocalPhysicalVertex> targetList = new ArrayList<>();
			for (LocalVertex v : vertices) {
				if (v instanceof LocalPhysicalVertex)
					targetList.add((LocalPhysicalVertex)v);
			}

			Collections.sort(targetList, new LocalVertexOrder());

			LocalPhysicalVertex[] lpvs = new LocalPhysicalVertex[targetList.size()];
			ArrayList<Integer> localVertexCounts = new ArrayList<>();
			int lastGlobalid = targetList.get(0).getGlobalVertexId();
			int localVertexCount = 0;
			for (int i = 0; i < targetList.size(); i++) {
				lpvs[i] = targetList.get(i);
				if (lpvs[i].getGlobalVertexId() != lastGlobalid) {
					localVertexCounts.add(localVertexCount);
					lastGlobalid = lpvs[i].getGlobalVertexId();
					localVertexCount = 0;
				}
				localVertexCount++;
			}
			localVertexCounts.add(localVertexCount);

			int[] localVertexCountArray = new int[localVertexCounts.size()];
			for (int i = 0; i < localVertexCountArray.length; i++)
				localVertexCountArray[i] = localVertexCounts.get(i);

			JobCache ret = new JobCache(job, lpvs, localVertexCountArray, jobInfo.getJobScheduler(job), jobInfo.getLocalPhysicalGraph(job));
			return ret;
		}

		public void addStatusChangedJob(final JobDescriptor jobDesc) {
			statusChangedJobs.add(jobDesc);
		}
	}

}
