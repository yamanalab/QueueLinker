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

package jp.queuelinker.system.net;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import jp.queuelinker.system.util.ByteConverter;
import jp.queuelinker.system.util.SerializeUtil;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 *
 */
public class ZookeeperConnection extends ZooKeeper {

    /**
     * @param connectString
     * @param sessionTimeout
     * @param watcher
     * @throws IOException
     */
    public ZookeeperConnection(final String connectString, final int sessionTimeout, final Watcher watcher)
            throws IOException {
        super(connectString, sessionTimeout, watcher);
    }

    /**
     * @param path
     * @param obj
     * @param acl
     * @param createMode
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String create(final String path, final Object obj, final List<ACL> acl, final CreateMode createMode)
            throws KeeperException, InterruptedException {
        byte[] data = SerializeUtil.serializeToBytes(obj);
        return super.create(path, data, acl, createMode);
    }

    /**
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void deleteRecursive(final String path) throws KeeperException, InterruptedException {
        _deleteRecursive(path);
    }

    /**
     * Deletes all path under the given path.
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void _deleteRecursive(final String path) throws KeeperException, InterruptedException {
        List<String> children = super.getChildren(path, null);
        for (String child : children) {
            _deleteRecursive(path + "/" + child);
        }
        delete(path, -1);
    }

    /**
     * @param path
     * @param obj
     * @param version
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void setObject(final String path, final Object obj, final int version) throws KeeperException, InterruptedException {
        byte[] data = SerializeUtil.serializeToBytes(obj);
        super.setData(path, data, version);
    }

    /**
     * @param path
     * @param watch
     * @param stat
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Object getObject(final String path, final Watcher watch, final Stat stat) throws KeeperException, InterruptedException {
        byte[] data = getData(path, watch, stat);
        return SerializeUtil.deserialize(data);
    }

    /**
     * @param path
     * @param watch
     * @param stat
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Object getObject(final String path, final boolean watch, final Stat stat) throws KeeperException, InterruptedException {
        byte[] data = getData(path, watch, stat);
        return SerializeUtil.deserialize(data);
    }

    /**
     * @param path
     * @param acl
     * @param createMode
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void createAtomicInt(final String path, final List<ACL> acl, final CreateMode createMode) throws KeeperException,
            InterruptedException {
        createAtomicInt(path, 0, acl, createMode);
    }

    /**
     * @param path
     * @param value
     * @param acl
     * @param createMode
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void createAtomicInt(final String path, final int value, final List<ACL> acl, final CreateMode createMode) throws KeeperException,
            InterruptedException {
        super.create(path, ByteConverter.getByteArrayOfInt(value), acl, createMode);
    }

    /**
     * @param path
     * @param watch
     * @param stat
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public int getAtomicInt(final String path, final Watcher watch, final Stat stat) throws KeeperException, InterruptedException {
        final byte[] data = super.getData(path, watch, stat);
        return ByteConverter.getIntValue(data);
    }

    /**
     * @param path
     * @param watch
     * @param stat
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public int getAtomicInt(final String path, final boolean watch, final Stat stat) throws KeeperException, InterruptedException {
        final byte[] data = super.getData(path, watch, stat);
        return ByteConverter.getIntValue(data);
    }

    /**
     * @param path
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public int getAndIncrementAtomicInt(final String path) throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        while (true) {
            byte[] data;
            try {
                data = super.getData(path, false, stat);
            } catch (KeeperException e) {
                throw e;
            }
            assert (data.length == 4);
            final int oldValue = ByteConverter.getIntValue(data);
            data = ByteConverter.getByteArrayOfInt(oldValue + 1);
            try {
                super.setData(path, data, stat.getVersion());
            } catch (KeeperException e) {
                if (e.code() == Code.BADVERSION) {
                    continue;
                } else {
                    throw e;
                }
            }
            return oldValue;
        }
    }

    /**
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void waitCreation(final String path) throws KeeperException, InterruptedException {
        // TODO This code is dangerous.
        SyncWatcher watcher = new SyncWatcher();
        while (super.exists(path, watcher) == null) {
            watcher.latch.await();
            watcher = new SyncWatcher();
        }
    }

    /**
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void waitDeletion(final String path) throws KeeperException, InterruptedException {
        // TODO This code is dangerous.
        SyncWatcher watcher = new SyncWatcher();
        while (super.exists(path, watcher) != null) {
            watcher.latch.await();
            watcher = new SyncWatcher();
        }
    }

    /**
     *
     */
    private class SyncWatcher implements Watcher {
        /**
         *
         */
        CountDownLatch latch = new CountDownLatch(1);

        /* (non-Javadoc)
         * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
         */
        @Override
        public void process(final WatchedEvent event) {
            latch.countDown();
        }
    }
}
