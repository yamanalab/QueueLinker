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

import java.io.Serializable;

public class VertexStat implements Serializable {
	private final long cpuCycles;

	private final long inputCount;

	private final long outputCount;

	public VertexStat(final long cpuCycles, final long inputCount, final long outputCount) {
		this.cpuCycles = cpuCycles;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
	}

	public long getCpuCycles() {
		return cpuCycles;
	}

	public long getInputCount() {
		return inputCount;
	}

	public long getOutputCount() {
		return outputCount;
	}

	@Override
	public String toString() {
		return "CPU: " + cpuCycles + " Input: " + inputCount + " Output: " + outputCount;
	}
}
