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
 * The root class of every modules for QueueLinker.
 */
public abstract class ModuleBase {

    /**
     *
     */
    public ModuleBase() {
    }

    /**
     * This is called every times when an instance is created.
     */
    public void initialize() {
    }

    /**
     * This is called only once shortly after the first instance of this class
     * is created.
     */
    public void onceInitialize() {
    }

    /**
     * This is called when the module is being terminated.
     */
    public void terminate() {
    }
}
