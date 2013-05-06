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

import java.io.Serializable;

/**
 * @param <I>
 */
public abstract class FlowSwitcherModule<I extends Serializable> extends ModuleBase {

    /**
     * Default constructor must be here for the serialization mechanism.
     */
    public FlowSwitcherModule() {
    }

    /**
     * @param input The object to be routed.
     * @return The queue ID that the object will be transferred to.
     */
    public abstract int execute(I input);
}
