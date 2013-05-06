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

package jp.queuelinker.system.serializer;

import jp.queuelinker.system.util.ByteConverter;

/**
 *
 */
public final class IntegerSerializer implements Serializer {

    /* (non-Javadoc)
     * @see jp.queuelinker.system.serializer.Serializer#deserialize(byte[])
     */
    @Override
    public Object deserialize(final byte[] data) {
        return ByteConverter.getIntValue(data);
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.serializer.Serializer#serialize(java.lang.Object)
     */
    @Override
    public byte[] serialize(final Object object) {
        byte[] ret = new byte[4];
        ByteConverter.assignIntValue(ret, 0, (Integer) object);
        return ret;
    }

    /* (non-Javadoc)
     * @see jp.queuelinker.system.serializer.Serializer#deserialize(byte[], int, int)
     */
    @Override
    public Object deserialize(final byte[] data, final int offset, final int length) {
        assert (length == 4);
        return ByteConverter.getIntValue(data, offset);
    }
}
