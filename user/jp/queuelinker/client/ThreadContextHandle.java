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
 * This class represents a thread of vertex context.
 */
public class ThreadContextHandle implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1782708202097745875L;

    /**
     *
     */
    private final String threadName;

    /**
     *
     */
    private final int threadId;

    /**
     * Creates a thread context handle.
     * @param threadName The name of this thread.
     * @param threadId The ID of this thread.
     */
    ThreadContextHandle(final String threadName, final int threadId) {
        this.threadName = threadName;
        this.threadId = threadId;
    }

    /**
     * Returns the name of this thread.
     * @return The name of this thread.
     */
    public final String getThreadName() {
        return threadName;
    }

    /**
     * Returns The ID of this thread.
     * @return The ID of this thread.
     */
    public final int getThreadId() {
        return threadId;
    }
}
