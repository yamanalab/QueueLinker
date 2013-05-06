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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jp.queuelinker.system.job.JobDescriptor;
import jp.queuelinker.system.job.SnapShotDescripter;

/**
 * The SnapShotManager maintains information about snap shots.
 */
public final class SnapShotManager {

    /**
     * The singleton instance of this class.
     */
    private static final SnapShotManager INSTANCE = new SnapShotManager();

    /**
     * This HashMap maintains all instances of SnapShotDescripter.
     */
    private HashMap<String, SnapShotDescripter> snapShots = new HashMap<>();

    /**
     * The next ID of a new snapshot.
     */
    private int nextId;

    /**
     * Returns the singleton instance of this class.
     * @return The singleton instance of this class.
     */
    public static SnapShotManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new SnapShotDescripter.
     * @param jobDesc The JobDescripter of a job to be took a snap shot.
     * @return A new SnapShotDescripter.
     */
    public SnapShotDescripter createNewDescripter(final JobDescriptor jobDesc) {
        SnapShotDescripter desc = new SnapShotDescripter(
                    String.valueOf(nextId++)
                );
        snapShots.put(desc.getSnapShotName(), desc);
        return desc;
    }

    /**
     * Returns a unmodifiable collection including all snap shot descriptors.
     * @return A unmodifiable collection including all snap shot descriptors.
     */
    public Collection<SnapShotDescripter> getAllSnapShots() {
        return Collections.unmodifiableCollection(snapShots.values());
    }

    /**
     * @param name
     * @return
     */
    public SnapShotDescripter findSnapShotDesc(final String name) {
        return snapShots.get(name);
    }

    /**
     * @param descs
     */
    public void addDescripters(List<SnapShotDescripter> descs) {
        for (SnapShotDescripter desc : descs) {
            snapShots.put(desc.getSnapShotName(), desc);
        }
    }
}
