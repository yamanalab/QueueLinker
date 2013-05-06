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

import jp.queuelinker.system.util.SerializeUtil;

/**
 * This is a default serializer using Java starndard serializer mechanism.
 */
public final class JavaDefaultSerializer implements Serializer {
    @Override
    public byte[] serialize(final Object object) {
        return SerializeUtil.serializeToBytes(object);
    }

    @Override
    public Object deserialize(final byte[] data) {
        return SerializeUtil.deserialize(data);
    }

    @Override
    public Object deserialize(final byte[] data, final int offset, final int length) {
        return SerializeUtil.deserialize(data, offset, length);
    }
}
