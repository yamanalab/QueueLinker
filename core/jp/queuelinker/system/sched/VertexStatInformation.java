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

public class VertexStatInformation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int globalVertexId;

    private final VertexStat[] stats;

    public VertexStatInformation(final int globalVertexId, final int localVertexCount) {
        this.globalVertexId = globalVertexId;
        this.stats = new VertexStat[localVertexCount];
    }

    public int getGlobalVertexId() {
        return globalVertexId;
    }

    public int getLocalVertexCount() {
        return stats.length;
    }

    public VertexStat getStat(final int localPartitionId) {
        return stats[localPartitionId];
    }

    public void setStat(final int localPartitionId, final VertexStat stat) {
        stats[localPartitionId] = stat;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(globalVertexId + "\n");
        for (VertexStat stat : stats) {
            builder.append(stat.toString() + " ");
        }
        return builder.toString();
    }
}
