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
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;

/**
 *
 */
public interface GlobalScheduler {
	/**
	 * @param job
	 * @return
	 */
	GlobalPhysicalGraph initialSchedule(QueueLinkerJob job);

	/**
	 * @param job
	 * @param graph
	 */
	void restoreJob(QueueLinkerJob job, GlobalPhysicalGraph graph);

	/**
	 * @return
	 */
	GlobalPhysicalGraph schedule();
}
