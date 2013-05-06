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

package jp.queuelinker.system.sched;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.queuelinker.client.Vertex;
import jp.queuelinker.module.base.ConstructorArguments;
import jp.queuelinker.module.base.ModuleBase;
import jp.queuelinker.system.graphs.LocalVertex;

public class InstanceManager {
	private static InstanceManager instance;

	private final HashMap<Vertex, ArrayList<ModuleBase>> instances = new HashMap<>();

	private InstanceManager() { }

	public static synchronized InstanceManager getInstance() {
		if (instance == null) {
			instance = new InstanceManager();
		}
		return instance;
	}

	public ModuleBase createNewInstance(final Vertex v) {
		ArrayList<ModuleBase> list = instances.get(v);
		ModuleBase ret = createInstance(v);
		if (list == null) {
			list = new ArrayList<>();
			instances.put(v, list);
		}
		list.add(ret);
		return ret;
	}

	private ModuleBase createInstance(final Vertex v) {
		final Class<? extends ModuleBase> moduleClass = v.getModuleClass();
		Constructor<?>[] constructors = moduleClass.getConstructors();
		try {
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					return (ModuleBase)constructor.newInstance();
				}
			}
			for (Constructor<?> constructor : constructors) {
				Class<?>[] parameters = constructor.getParameterTypes();
				if (parameters.length == 1
				    && parameters[0].isAssignableFrom(ConstructorArguments.class)) {
					return (ModuleBase)constructor.newInstance(v.getConstructorArguments());
				}
			}
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ModuleBase restore(final LocalVertex v, final File file) throws IOException, ClassNotFoundException {
		FileInputStream  inputFile = new FileInputStream(file);
		ObjectInputStream stream = new ObjectInputStream(inputFile);
		ModuleBase instance = (ModuleBase)stream.readObject();
		return instance;
	}
}
