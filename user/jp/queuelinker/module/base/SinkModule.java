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

import jp.queuelinker.module.QueueLinkerService;

/**
 * @param <I>
 */
public abstract class SinkModule<I extends Serializable>
extends LogicalModule implements PushInputModule<I> {
    /**
     *
     */
    public SinkModule() {
        // Default constructor must be here for the serialization mechanism.
    }

    /**
     * Constructor.
     * @param arguments The constructor argument.
     */
    public SinkModule(final ConstructorArguments arguments) {
        super(arguments);
    }

    /**
     * This method is called every time an item arrives.
     * @param item An item to be processed.
     * @param service A service of QueueLinker.
     */
    public abstract void execute(I item, QueueLinkerService service);
}
