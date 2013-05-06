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

import java.io.Serializable;

/**
 * @param <T>
 */
public interface CustomSerializer<T> extends Serializable {
    /**
     * Serializes an object.
     * @param object An object to be serialized.
     * @return Byte array contains this object.
     */
    byte[] customSerialize(T object);

    /**
     * Deserializes an object from a byte array.
     * @param data The data contains the object.
     * @return The desirialized object.
     */
    T customDeserialize(byte[] data);

    /**
     * Deserialized an object from a byte array with a specified offset.
     * @param data The data contains the object.
     * @param offset Offset from which to start desirializing.
     * @param length Number of bytes to be used to desirialize.
     * @return The desirialized object.
     */
    T customDeserialize(byte[] data, int offset, int length);
}
