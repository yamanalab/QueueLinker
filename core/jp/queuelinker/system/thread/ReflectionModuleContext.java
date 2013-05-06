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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.module.base.ReflectionModule;
import jp.queuelinker.system.thread.router.DispatchRouteInformation;


public class ReflectionModuleContext extends ThreadContextBase implements InputContext, OutputContext {

	private final Method[] methods;

	private final ReflectionModule instance;

	public ReflectionModuleContext(final ThreadUnit unit, final ReflectionModule instance, final Method[] method) {
		super(unit);
		this.instance = instance;
		this.methods = method;
	}

	@Override
	public void dispatchAccept(final Object element, final DispatchRouteInformation route) {
		// TODO Auto-generated method stub

	}

	@Override
	void initialize() {
		instance.initialize();
	}

	@Override
	void execute() {
		try {
			methods[0].invoke(null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void snapShotModule(final ObjectOutputStream output) throws InvalidClassException, IOException, NotSerializableException {
		output.writeObject(instance);
	}

	@Override
	public ModuleBase getModuleBase() {
		return instance;
	}
}
