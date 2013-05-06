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

package jp.queuelinker.system.exception;

/**
 *
 */
public class SystemExceptionBase extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1015168268133414737L;

    /**
     *
     */
    private final Exception exception;

    /**
     * @param exception
     */
    public SystemExceptionBase(final Exception exception) {
        this.exception = exception;
    }

    /**
     * @return
     */
    public Exception getOriginalException() {
        return exception;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        return exception.toString();
    }
}
