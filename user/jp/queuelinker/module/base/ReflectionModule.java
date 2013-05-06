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
 * This is the base class for reflection module.
 */
public abstract class ReflectionModule extends ModuleBase {
    /**
     *
     */
    public ReflectionModule() {
        // Default constructor must be here for the serialization mechanism.
    }

    /**
     * Constructor.
     * @param arguments The constructor argument.
     */
    public ReflectionModule(final ConstructorArguments arguments) {
    }
}
