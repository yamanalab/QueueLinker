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

package jp.queuelinker.system.util;

import java.util.ArrayList;

public class ReflectionUtil {
	public static ArrayList<Class<?>> getAllInterfaces(Class<?> clazz) {
		ArrayList<Class<?>> ret = new ArrayList<>();
		_getAllInterfaces(clazz, ret);
		return ret;
	}
	
	private static void _getAllInterfaces(Class<?> clazz, ArrayList<Class<?>> result) {
		if (clazz == null)
			return;
		
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interf : interfaces) {
			result.add(interf);
			_getAllInterfaces(interf, result);
		}
		
		_getAllInterfaces(clazz.getSuperclass(), result);
	}


	public static ArrayList<Class<?>> getAllSuperClasses(Class<?> clazz) {
		ArrayList<Class<?>> ret = new ArrayList<>();
		_getAllSuperClasses(clazz, ret);
		return ret;
	}
	
	private static void _getAllSuperClasses(Class<?> clazz, ArrayList<Class<?>> result) {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			result.add(superClass);
			_getAllSuperClasses(superClass, result);
		}
	}
	
	public static boolean isImplemented(Class<?> clazz, Class<?> implemented) {
		return getAllInterfaces(clazz).contains(implemented);
	}
	
	public static boolean isExtended(Class<?> clazz, Class<?> extended) {
		return getAllSuperClasses(clazz).contains(extended);
	}
}
