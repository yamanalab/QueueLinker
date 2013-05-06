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

package jp.queuelinker.system.thread;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import jp.queuelinker.module.QueueLinkerService;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.SinkModule;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;
import jp.queuelinker.system.unsafe.UnsafeUtil;

public class SinkModuleContext<I extends Serializable> extends ThreadContextBase implements InputPushContext<I> {
    private final SinkModule<I> instance;

    private final QueueLinkerService service;

    private final LinkedList<I> inputQueue = new LinkedList<I>();

    private final ThreadLocalScheduler sched;

    private boolean executable;

    public SinkModuleContext(final PushModulesThreadUnit unit, final SinkModule<I> instance,
            final QueueLinkerService service) {
        super(unit);

        this.sched = unit.getThreadLocalScheduler();
        this.instance = instance;
        this.service = service;
    }

    @Override
    public void dispatchAccept(final I element, final DispatchRouteInformation route) {
        inputQueue.add(element);
        waitingCount++;

        if (!executable) {
            sched.executable(this);
            executable = true;
        }
    }

    @Override
    void execute() {
        assert (executable);
        final I item = inputQueue.poll();
        inputCount++;
        long start = UnsafeUtil.rdtsc();
        instance.execute(item, service);
        cpuCycles += UnsafeUtil.rdtsc() - start;
        waitingCount--;

        if (inputQueue.isEmpty()) {
            sched.unExecutable(this);
            executable = false;
        }
    }

    @Override
    void initialize() {
        instance.initialize();
    }

    @Override
    public String toString() {
        return String.format("SinkModuleContext (%s)", instance.getClass().getCanonicalName());
    }

    @Override
    public void snapShotModule(final ObjectOutputStream output)
            throws InvalidClassException, IOException, NotSerializableException {
        output.writeObject(instance);
    }

    @Override
    public ModuleBase getModuleBase() {
        return instance;
    }
}
