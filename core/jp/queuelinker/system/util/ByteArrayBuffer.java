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

import java.nio.ByteBuffer;
import java.util.Arrays;

import jp.queuelinker.system.serializer.JavaDefaultSerializer;
import jp.queuelinker.system.serializer.Serializer;

/**
 *
 */
public class ByteArrayBuffer {

    /**
     *
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 1024;

    /**
     *
     */
    private static final float SCALE = 1.7F;

    /**
     *
     */
    private byte[] array;

    /**
     *
     */
    private int readPos;

    /**
     *
     */
    private int writePos;

    /**
     *
     */
    private final Serializer serializer;

    /**
     *
     */
    public ByteArrayBuffer() {
        this(DEFAULT_INITIAL_CAPACITY, new JavaDefaultSerializer());
    }

    /**
     * @param initialCapacity
     */
    public ByteArrayBuffer(final int initialCapacity) {
        this(initialCapacity, new JavaDefaultSerializer());
    }

    /**
     * @param serializer
     */
    public ByteArrayBuffer(final Serializer serializer) {
        this(DEFAULT_INITIAL_CAPACITY, serializer);
    }

    /**
     * @param initialCapacity
     * @param serializer
     */
    public ByteArrayBuffer(final int initialCapacity, final Serializer serializer) {
        this.array = new byte[initialCapacity];
        this.serializer = serializer;
    }

    /**
     * @return
     */
    public int getInt() {
        int ret = ByteConverter.getIntValue(array, readPos);
        readPos += 4;
        return ret;
    }

    /**
     * @param length
     * @return
     */
    public Object getObject(final int length) {
        Object ret = serializer.deserialize(array, readPos, length);
        readPos += length;
        return ret;
    }

    /**
     * @return
     */
    public int size() {
        return array.length;
    }

    /**
     * @return
     */
    public int readableSize() {
        return writePos - readPos;
    }

    /**
     * @return
     */
    public int limit() {
        return writePos;
    }

    /**
     * @return
     */
    public int nextWritePosition() {
        return writePos;
    }

    /**
     *
     */
    public void compact() {
        array = Arrays.copyOfRange(array, readPos, writePos);
        writePos -= readPos;
        readPos = 0;
    }

    /**
     * @param startPos
     */
    public void trancate(final int startPos) {
        if (startPos == 0)
            return;

        array = Arrays.copyOfRange(array, startPos, writePos);
        writePos = writePos - startPos;
        readPos = 0;
    }

    /**
     * @return
     */
    public ByteBuffer getByteBuffer() {
        ByteBuffer ret = ByteBuffer.wrap(array, readPos, readableSize());
        readPos = writePos;
        return ret;
    }

    /**
     * @param buffer
     */
    public void write(final ByteBuffer buffer) {
        // The buffer must have array.
        write(buffer.array(), buffer.position(),
                buffer.limit() - buffer.position());
    }

    /**
     * @param data
     * @param offset
     * @param length
     */
    public void write(final byte[] data, final int offset, final int length) {
        if (writePos + length > array.length) {
            array = Arrays.copyOf(array, (int) (array.length * SCALE + length));
        }

        // TODO use more effective way.
        for (int i = 0; i < length; i++) {
            array[writePos++] = data[offset + i];
        }
    }

    /**
     * @param readPos
     */
    public void setReadPos(final int readPos) {
        this.readPos = readPos;
    }

    /**
     * @return
     */
    public int getReadPos() {
        return readPos;
    }

    /**
     * @return
     */
    public byte[] array() {
        return array;
    }

    /**
     * @param size
     */
    public void rollback(final int size) {
        assert (readPos >= size);
        readPos -= size;
    }
}
