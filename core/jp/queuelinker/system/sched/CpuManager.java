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

import jp.queuelinker.system.annotation.ThreadSafe;
import jp.queuelinker.system.util.ObjectLinkedList;

@ThreadSafe
public class CpuManager {
	private static CpuManager instance;

	private final ObjectLinkedList<CPU> idleCPUs = new ObjectLinkedList<CPU>();

	private final ObjectLinkedList<CPU> usedCPUs = new ObjectLinkedList<CPU>();

	private final CPUTopology topology;

	private CpuManager() {
		this.topology = CPUTopology.getInstance();

		initialize();
	}

	private synchronized void initialize() {
		for (int chip = 0; chip < topology.getCPUChipCount(); chip++) {
			for (int core = 0; core < topology.getCorePerChip(); core++) {
				int affinityId = topology.getCpuAffinityId(chip, core);
				CPU cpu = new CPU(chip, core, affinityId);
				idleCPUs.addFirst(cpu);
			}
		}
	}

	public synchronized static CpuManager getInstance() {
		if (instance == null) {
			instance = new CpuManager();
		}
		return instance;
	}

	public synchronized CPU reserveIdleCPU() {
		CPU ret = idleCPUs.poolFirst();
		usedCPUs.addFirst(ret);
		return ret;
	}

	public synchronized int getCoreCount(final int chipId) {
		int ret = 0;
		for (CPU cpu : idleCPUs) {
			if (cpu.getCPUChipId() == chipId) {
				ret++;
			}
		}

		for (CPU cpu : usedCPUs) {
			if (cpu.getCPUChipId() == chipId) {
				ret++;
			}
		}

		return ret;
	}

	public synchronized CPU reserveIdleCPU(final int chipId) {
		for (CPU cpu : idleCPUs) {
			if (cpu.getCPUChipId() == chipId) {
				CPU ret = idleCPUs.poolFirst();
				usedCPUs.addFirst(ret);
				return ret;
			}
		}

		throw new RuntimeException("There is no idle CPU.");
	}

	public synchronized CPU reserveIdleCPUonSameDie(final CPU targetCPU) {
		for (CPU cpu : idleCPUs) {
			if (cpu.getCPUChipId() == targetCPU.getCPUChipId()) {
				return cpu;
			}
		}
		return null;
	}

	public synchronized void returnCPU(final CPU cpu) {
		usedCPUs.remove(cpu.getListElement());
		idleCPUs.addFirst(cpu.getListElement());
	}
}
