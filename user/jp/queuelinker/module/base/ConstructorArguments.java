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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * This class holds arguments for a constructor of a module.
 */
public class ConstructorArguments implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1765710061929823421L;

    /**
     *
     */
    private final HashMap<String, Object> arguments = new HashMap<String, Object>();

    /**
     * Adds an argument with a name.
     * @param argName The name of the argument object to be added.
     * @param arg The argument object.
     */
    public final void addArgument(final String argName, final Object arg) {
        arguments.put(argName, arg);
    }

    /**
     * Returns an argument associated to the name.
     * @param argName Name of an argument object to be retrieved.
     * @return An argument object associated to the name.
     */
    public final Object getArgument(final String argName) {
        return arguments.get(argName);
    }

    /**
     * Returns the number of arguments.
     * @return The number of arguments.
     */
    public final int getArgumentsCount() {
        return arguments.size();
    }

    /**
     * Returns all the arguments.
     * @return An unmodifiable collection containing all the arguments.
     */
    public final Collection<Object> getAllArguments() {
        return Collections.unmodifiableCollection(arguments.values());
    }
}
