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

/**
 * This class represents exception of a client.
 */
public final class ClientException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1620233898064981926L;

    /**
     *
     */
    private final Exception exception;

    /**
     * Constructs a new exception.
     * @param exception Original exception.
     */
    ClientException(final Exception exception) {
        super(exception);
        this.exception = exception;
    }

    /**
     * Returns the exception that is the cause of this exception.
     * @return The original exception.
     */
    public Exception getException() {
        return exception;
    }
}
