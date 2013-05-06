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
 * @param <I>
 * @param <O>
 */
public abstract class MultipleOutputQueuesPushModule<I extends Serializable, O extends Serializable> {
    /**
     * This methid is called every time when an item arrives.
     * @param item An item to be processed.
     * @param staff The output staff of this module.
     * @param service The service provider of QueueLinker.
     */
    public abstract void execute(I item, OutputStaff<O> staff, QueueLinkerService service);
}
