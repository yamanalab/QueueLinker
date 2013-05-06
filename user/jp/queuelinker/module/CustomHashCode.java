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
 * User can provide a custom hash coder by implementing this interface.
 */
public interface CustomHashCode extends Serializable {
    /**
     * @param obj An object to be calculated its hash value.
     * @return The hash code of this object.
     */
    int calcHashCode(Object obj);
}
