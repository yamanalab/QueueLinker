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

package jp.queuelinker.module;

import jp.queuelinker.log.LoggerService;
import jp.queuelinker.module.base.ModuleBase;

/**
 * This class provides some methods to access to QueueLinker services.
 */
public interface QueueLinkerService {
    /**
     * Returns the name of this thread.
     * @return The name of this thread.
     */
    String getThreadContextName();

    /**
     * Returns the ID of this thread.
     * @return The ID of this thread.
     */
    int getThreadContextId();

    /**
     * @param e
     */
    void notifyException(Exception e);

    /**
     * @param check
     */
    void assertCheck(boolean check);

    /**
     * Returns a logger.
     * @return A logger.
     */
    LoggerService getLogger();

    /**
     * Returns a logger for the specified module.
     * @param base
     * @return A logger for the module.
     */
    LoggerService getLogger(ModuleBase base);

    /**
     *
     */
    void finish();

    /**
     * @return
     */
    boolean stopRequested();

    // public abstract QFileOutputStream getLocalFileOutputStream(String string)
    // throws FileNotFoundException;

    // public abstract QFileOutputStream getGlobalFileOutputStream(String
    // string) throws FileNotFoundException;

    // public abstract QFileInputStream getLocalFileInputStream(String filePath)
    // throws FileNotFoundException;

    // public abstract QFileInputStream getGlobalFileInputStream(String
    // filePath) throws FileNotFoundException;
}
