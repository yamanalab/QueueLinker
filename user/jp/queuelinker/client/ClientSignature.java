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
 * This class represents a client signature.
 */
public final class ClientSignature implements Serializable {
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -1799025859798494262L;

    /**
     * The Global ID of this client.
     */
    private final int id;

    /**
     * Constructor.
     * @param id Id of this client.
     */
    ClientSignature(final int id) {
        this.id = id;
    }

    /**
     * Returns the ID of this client.
     * @return The ID of this client.
     */
    public int getClientId() {
        return id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ClientSignature)) {
            return false;
        }
        if (((ClientSignature) obj).id == id) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id;
    }
}
