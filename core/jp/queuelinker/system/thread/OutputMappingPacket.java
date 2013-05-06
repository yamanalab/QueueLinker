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

public class OutputMappingPacket implements ThreadManagementPacket {
	public final OutputContext<?> context;

	public final int outputLogicalId;

	public final int outputThreadLocalId;

	public OutputMappingPacket(final OutputContext<?> context, final int outputLogicalId, final int outputThreadLocalId) {
		this.context = context;
		this.outputLogicalId = outputLogicalId;
		this.outputThreadLocalId = outputThreadLocalId;
	}

	@Override
	public void manipulate(final ThreadUnit unit) {
		unit.getThreadLocalOutputRouter().setOutputMapping(context.getThreadLocalId(), outputLogicalId, outputThreadLocalId);
	}
}
