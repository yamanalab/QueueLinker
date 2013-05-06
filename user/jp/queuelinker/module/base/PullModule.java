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

import jp.queuelinker.module.InputStaff;
import jp.queuelinker.module.OutputStaff;
import jp.queuelinker.module.QueueLinkerService;

/**
 * @param <I>
 * @param <O>
 */
public abstract class PullModule<I extends Serializable, O extends Serializable>
extends LogicalModule implements PullInputModule<I>, OutputModule<O> {
    /**
     * Default constructor must be here for the serialization mechanism.
     */
    public PullModule() {
    }

    /**
     * @param arguments The constructor argument.
     */
    public PullModule(final ConstructorArguments arguments) {
        super(arguments);
    }

    /**
     * This method is called once when this module is executed.
     * @param inputStaff The input staff for this module.
     * @param outputStaff The output staff for this module.
     * @param service The service of QueueLinker.
     */
    public abstract void execute(InputStaff<I> inputStaff, OutputStaff<O> outputStaff, QueueLinkerService service);
}
