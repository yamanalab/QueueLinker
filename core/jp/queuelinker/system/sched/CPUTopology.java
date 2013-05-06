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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class CPUTopology {
	public final static String fileName = "config/affinity";

	private static CPUTopology instance;

	private int cpuTotalCoreCount;

	private int cpuChipCount;

	private int corePerChip;

	private int[][] cpuAffinity;

	private CPUTopology() {
		try {
			fileInitialize();
			return;
		} catch (Exception e) {
//			System.err.println("There is no CPU topology file or wrong file. Default setting is used.");
		}

		defaultInitialize();
	}

	public synchronized static CPUTopology getInstance() {
		if (instance == null) {
			instance = new CPUTopology();
		}
		return instance;
	}

	private void defaultInitialize() {
		this.cpuTotalCoreCount = Runtime.getRuntime().availableProcessors();
		this.cpuChipCount = this.cpuTotalCoreCount;
		this.corePerChip = 1;
		this.cpuAffinity = new int[cpuChipCount][corePerChip];

		for (int chip = 0; chip < cpuAffinity.length; chip++) {
			for (int core = 0; core < cpuAffinity[chip].length; core++) {
				cpuAffinity[chip][core] = -1;
			}
		}
	}

	private void fileInitialize() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileName));

		cpuChipCount = scanner.nextInt();
		corePerChip = scanner.nextInt();
		cpuTotalCoreCount = corePerChip * cpuChipCount;
		this.cpuAffinity = new int[cpuChipCount][corePerChip];

//		if (cpuTotalCoreCount != Runtime.getRuntime().availableProcessors())
//			throw new RuntimeException("The CPU topology file is wrong." + Runtime.getRuntime().availableProcessors());

		for (int cpu = 0; cpu < cpuChipCount; cpu++) {
			cpuAffinity[cpu] = new int[corePerChip];
			for (int core = 0; core < corePerChip; core++) {
				cpuAffinity[cpu][core] = scanner.nextInt();
			}
		}
	}

	public int getTotalCoreCount() {
		return  cpuTotalCoreCount;
	}

	public int getCPUChipCount() {
		return cpuChipCount;
	}

	public int getCorePerChip() {
		return corePerChip;
	}

	public int[] getCpuAffinityIds(final int cpu) {
		return Arrays.copyOf(cpuAffinity[cpu], cpuAffinity.length);
	}

	public int getCpuAffinityId(final int chipId, final int coreId) {
		return cpuAffinity[chipId][coreId];
	}
}
