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

package jp.queuelinker.system.zookeeper.command;

import java.io.Serializable;

/**
 *
 */
public abstract class ClientRequestAck implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 549209497735187039L;

    /**
     * The ID of this request.
     */
    private final long requestId;

    /**
     * @param requestId
     */
    public ClientRequestAck(final long requestId) {
        this.requestId = requestId;
    }

    /**
     * @return
     */
    public long getRequestId() {
        return requestId;
    }
}
