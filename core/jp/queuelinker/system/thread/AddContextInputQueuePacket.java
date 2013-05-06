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

/**
 *
 */
public class AddContextInputQueuePacket implements ThreadManagementPacket {
	/**
	 *
	 */
	private final MultipleInputQueuesPushModuleContext<?, ?> context;

	/**
	 *
	 */
	private final int logicalId;

	/**
	 * @param context
	 * @param logicalId
	 */
	public AddContextInputQueuePacket(final MultipleInputQueuesPushModuleContext<?, ?> context,
	                                  final int logicalId) {
		this.context = context;
		this.logicalId = logicalId;
	}

	/* (non-Javadoc)
	 * @see jp.queuelinker.system.thread.ThreadManagementPacket#manipulate(jp.queuelinker.system.thread.ThreadUnit)
	 */
	@Override
	public void manipulate(final ThreadUnit unit) {
		context.addNewInputQueue(logicalId);
	}
}
