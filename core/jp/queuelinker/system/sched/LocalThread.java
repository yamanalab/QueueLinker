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

/**
 *
 */
public class LocalThread {
    /**
     *
     */
    private final CPU cpu;

    /**
     *
     */
    private final boolean busyLoopMode;

    /**
     * @param busyLoopMode
     */
    public LocalThread(final boolean busyLoopMode) {
        this(null, busyLoopMode);
    }

    /**
     * @param cpu
     * @param busyLoopMode
     */
    public LocalThread(final CPU cpu, final boolean busyLoopMode) {
        this.cpu = cpu;
        this.busyLoopMode = busyLoopMode;
    }

    /**
     * @return
     */
    public CPU getCPU() {
        return cpu;
    }

    /**
     * @return
     */
    public boolean isFixedThread() {
        return cpu != null;
    }

    /**
     * @return
     */
    public boolean isBusyLoopMode() {
        return busyLoopMode;
    }

    /**
     * @return
     */
    public int getAffinityId() {
        if (cpu == null) {
            return -1;
        } else {
            return cpu.getAffinityId();
        }
    }
}
