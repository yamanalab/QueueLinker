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

package jp.queuelinker.system.thread.router;

public abstract class Dispatcher {
	protected final boolean mutable;

	public Dispatcher(final boolean mutable) {
		this.mutable = mutable;
	}

	/**
	 *
	 * @return true if the item will be modified. Otherwise false.
	 */
	public boolean isMutable() {
		return mutable;
	}

	/**
	 * Dispatch the element to an appropriate target.
	 * @param element
	 */
	abstract void dispatch(Object element);

	abstract void newAccepter(DispatchAccepter accepter);

	abstract void removeAccepter(DispatchAccepter accepter);
}
