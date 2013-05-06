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

package jp.queuelinker.client;

import java.io.Serializable;

/**
 * Handle representing a snap shot.
 */
public final class SnapShotHandle implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5967856469463974554L;

    /**
     * The name of this snapshot.
     */
    private final String name;

    /**
     * Constructor.
     * @param name The name of this snapshot.
     */
    SnapShotHandle(final String name) {
        this.name = name;
    }

    /**
     * Get the name of this snap shot.
     * @return The name of this snap shot.
     */
    public String getName() {
        return name;
    }
}
