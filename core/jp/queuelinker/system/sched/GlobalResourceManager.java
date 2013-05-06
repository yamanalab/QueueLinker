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

import jp.queuelinker.system.server.WorkerServerInstance;
import jp.queuelinker.system.server.WorkerServerManager;

public class GlobalResourceManager {
	private final WorkerServerManager worker = WorkerServerManager.getInstance();

	public void newWorkerStat(final WorkerServerInstance instance, final int value) {

	}

	public void getFreeWorkerServer() {

	}

	public WorkerServerManager assignWorker(final WorkerServerInstance instance) {
		return null;
	}
}
