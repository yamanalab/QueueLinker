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

package jp.queuelinker.system.thread;

import jp.queuelinker.log.LoggerService;
import jp.queuelinker.module.QueueLinkerService;
import jp.queuelinker.module.base.ModuleBase;

class QueueLinkerServiceImpl implements QueueLinkerService {

    private final String name;

    private final LoggerService logger;

    private volatile boolean stopRequested;

    public QueueLinkerServiceImpl(final String name, final long threadId) {
        this.name = name;
        this.logger = new LoggerService("hostname", name, threadId);
    }

    public QueueLinkerServiceImpl(final ModuleBase moduleBase, final long threadId) {
        this(moduleBase.getClass().getName(), threadId);
    }

    @Override
    public String getThreadContextName() {
        return null;
    }

    @Override
    public int getThreadContextId() {
        return 0;
    }

    @Override
    public void notifyException(final Exception e) {

    }

    @Override
    public void assertCheck(final boolean check) {

    }

    void stopRequest() {
        stopRequested = true;
    }

    void cancelStopRequest() {
        stopRequested = false;
    }

    @Override
    public boolean stopRequested() {
        return stopRequested;
    }

    @Override
    public LoggerService getLogger() {
        return logger;
    }

    @Override
    public LoggerService getLogger(final ModuleBase base) {
        return logger;
    }

    @Override
    public void finish() {

    }

    // public QFileOutputStream getLocalFileOutputStream(String string) throws
    // FileNotFoundException {
    // return null;
    // }
    //
    // public QFileOutputStream getGlobalFileOutputStream(String string) throws
    // FileNotFoundException {
    // return null;
    // }
    //
    // public QFileInputStream getLocalFileInputStream(String filePath) throws
    // FileNotFoundException {
    // return null;
    // }
    //
    // public QFileInputStream getGlobalFileInputStream(String filePath) throws
    // FileNotFoundException {
    // return null;
    // }

}
