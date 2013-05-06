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

import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.exception.RestoreException;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalPhysicalGraph;
import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.SnapShotDescripter;
import jp.queuelinker.system.server.SnapShotFileManager;

public abstract class LocalScheduler {

    protected final QueueLinkerJob job;

    protected final WorkerSignature signature;

    protected final GlobalPhysicalGraph gpg;

    public LocalScheduler(final QueueLinkerJob job, final WorkerSignature signature, final GlobalPhysicalGraph gpg) {
        this.job = job;
        this.signature = signature;
        this.gpg = gpg;
    }

    public abstract LocalPhysicalGraph initialSchedule();

    public abstract LocalPhysicalGraph schedule();

    public abstract void restore(LocalPhysicalGraph lpg, SnapShotDescripter snapShot, SnapShotFileManager fileManager)
            throws RestoreException;

    public abstract void setOperatorStat(LocalVertex vertex, VertexStat stat);

    public void jobStarting() {
    }

    public void jobStarted() {
    }

    public void jobStopping() {
    }

    public void jobStopped() {
    }

    public void jobSuspending() {
    }

    public void jobSuspended() {
    }

    public void jobResuming() {
    }

    public void jobResumed() {
    }
}
