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

import jp.queuelinker.module.OutputStaff;
import jp.queuelinker.module.QueueLinkerService;

/**
 * This is a base class for source modules.
 * @param <O>
 */
public abstract class SourceModule<O extends Serializable>
extends LogicalModule implements OutputModule<O> {
    /**
     *
     */
    public SourceModule() {
        // Default constructor must be here for the serialization mechanism.
    }

    /**
     * Constructor.
     * @param argument The constructor arguments.
     */
    public SourceModule(final ConstructorArguments argument) {
        super(argument);
    }

    /**
     * @param staff The staff managing inputs.
     * @param service The class for QueueLinker service.
     */
    public abstract void execute(OutputStaff<O> staff, QueueLinkerService service);
}
