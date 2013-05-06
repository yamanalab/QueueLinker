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

package jp.queuelinker.module.base;

/**
 * @param <T>
 */
public interface ModuleInstanceManager<T> {
    /**
     * Finds an available instance.
     * @return An instance of this module.
     */
    T getInstance();

    /**
     * @param instance An instance to be released.
     */
    void putInstance(T instance);

    /**
     * @return The memory size consumed by this module.
     */
    long getTotalMemoryConsumption();
}
