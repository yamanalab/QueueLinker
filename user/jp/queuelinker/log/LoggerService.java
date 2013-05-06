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

package jp.queuelinker.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a dummy implementation of a logger.
 */
public class LoggerService {

    /**
     *
     */
    private final String hostName;

    /**
     *
     */
    private final String moduleName;

    /**
     *
     */
    private final long threadId;

    /**
     *
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SS");

    /**
     *
     */
    private static FileWriter writer;

    static {
        try {
            writer = new FileWriter(new File("tmp/log"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @param hostName
     * @param moduleName
     * @param threadId
     */
    public LoggerService(final String hostName, final String moduleName, final long threadId) {
        this.hostName = hostName;
        this.moduleName = moduleName;
        this.threadId = threadId;
    }

    /**
     * @param message
     */
    public void error(final String message) {
        output(message);
    }

    /**
     * @param message
     */
    public void info(final String message) {
        output(message);
    }

    /**
     * @param message
     */
    public void fatal(final String message) {
        output(message);
    }

    /**
     * @param message
     */
    public void debug(final String message) {
        output(message);
    }

    /**
     * @return
     */
    public boolean isLogging() {
        return true;
    }

    /**
     * @param message
     */
    private void output(final String message) {
        Date date = new Date();
        try {
            writer.write(String.format("%s, %d, %d, %s, %s, %s\n", dateFormat.format(date), System.nanoTime(),
                                       threadId, moduleName, hostName, message));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
