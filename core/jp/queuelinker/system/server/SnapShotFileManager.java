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

import java.io.File;

import jp.queuelinker.system.graphs.LocalVertex;
import jp.queuelinker.system.job.SnapShotDescripter;

/**
 * The SnapShotFileManager class manages snap shot files.
 */
public class SnapShotFileManager {

    /**
     * The top directory storing snap shot files.
     */
    private final String topDirPath;

    /**
     * Creates the instance of SnapShotFileManager with the specified path.
     * @param topDirPath The top directory storing snap shot files.
     */
    public SnapShotFileManager(final String topDirPath) {
        this.topDirPath = topDirPath;
    }

    /**
     * @param desc
     */
    public void createSnapShotDir(final SnapShotDescripter desc) {
        File newDir = new File(desc.getSnapShotName());
        if (!newDir.mkdirs()) {
            throw new RuntimeException("Directory Creation Error.");
        }
    }

    /**
     * @param desc
     * @param lv
     * @return
     */
    public File getVertexFile(final SnapShotDescripter desc, final LocalVertex lv) {
        return new File(topDirPath + "/" + desc.getSnapShotName() + "/" + lv.getName() + "-" + lv.getVertexId());
    }

    /**
     * @param desc
     * @return
     */
    public File getGraphFile(final SnapShotDescripter desc) {
        return new File(topDirPath + "/" + desc.getSnapShotName() + "/graph");
    }

    /**
     * Scans the files in the direcroty.
     */
    private void findSnapShots() {
        final File topDir = new File(topDirPath);
        if (!topDir.isDirectory()) {
            throw new RuntimeException("BUG: The path is not a directory");
        }

        File[] snapShotDirs = topDir.listFiles();
        for (File snapShotDir : snapShotDirs) {
            if (!snapShotDir.isDirectory()) {
                continue;
            }

            SnapShotDescripter desc = new SnapShotDescripter(snapShotDir.getName());
            // snapShots.add(desc);
        }
    }
}
