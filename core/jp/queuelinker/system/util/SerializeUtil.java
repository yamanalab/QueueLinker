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

package jp.queuelinker.system.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The SerializeUtil class provides several basic serialization mechanism.
 */
public class SerializeUtil {
    /**
     *
     */
    private static final Log logger = LogFactory.getLog(SerializeUtil.class);

    /**
     * Serializes an object to a ByteBuffer.
     * @param obj An object to be serialized.
     * @return A ByteBuffer maintains the serialized data.
     */
    public static ByteBuffer serializeToByteBuffer(final Object obj) {
        return ByteBuffer.wrap(serializeToBytes(obj));
    }

    /**
     * Serializes an object to a byte array.
     * @param obj
     * @return
     */
    public static byte[] serializeToBytes(final Object obj) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
            objStream.writeObject(obj);
            objStream.close();
        } catch (IOException e) {
            logger.error("Serialization Error. The class " + obj.getClass().getName()
                         + " may not implement Serializable interface");
        }
        return byteStream.toByteArray();
    }

    /**
     * @param data
     * @return
     */
    public static Object deserialize(final byte[] data) {
        return deserialize(data, 0, data.length);
    }

    /**
     * Deserializes an object from a byte array by using the Java standard serialization mechanism.
     * @param data
     * @param offset
     * @param length
     * @return
     */
    public static Object deserialize(final byte[] data, final int offset, final int length) {
        Object ret = null;
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data, offset, length);

        try {
            ObjectInputStream objStream = new ObjectInputStream(byteStream);
            ret = objStream.readObject();
            objStream.close();
        } catch (IOException e) {
            logger.error("Exception: " + e);
        } catch (ClassNotFoundException e) {
            logger.error("Exception: " + e);
        }
        return ret;
    }

    /**
     * @param file
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object restoreObject(final File file) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
        return input.readObject();
    }

    /**
     * @param obj
     * @param file
     * @throws IOException
     */
    public static void storeObject(final Object obj, final File file) throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
        output.writeObject(obj);
    }
}
