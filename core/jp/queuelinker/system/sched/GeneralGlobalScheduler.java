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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jp.queuelinker.client.Edge;
import jp.queuelinker.client.LogicalGraph;
import jp.queuelinker.client.LogicalVertex;
import jp.queuelinker.client.QueueLinkerJob;
import jp.queuelinker.client.SwitcherVertex;
import jp.queuelinker.client.Vertex;
import jp.queuelinker.client.VertexDistributingMode;
import jp.queuelinker.client.VirtualVertex;
import jp.queuelinker.server.WorkerSignature;
import jp.queuelinker.system.graphs.GlobalPhysicalGraph;
import jp.queuelinker.system.graphs.GlobalPhysicalVertex;
import jp.queuelinker.system.server.WorkerServerInstance;
import jp.queuelinker.system.server.WorkerServerManager;

public class GeneralGlobalScheduler implements GlobalScheduler {

	private final WorkerServerManager workerManager = WorkerServerManager.getInstance();

	@Override
	public GlobalPhysicalGraph initialSchedule(final QueueLinkerJob job) {
		final LogicalGraph lg = job.getVertexGraph();
		final GlobalPhysicalGraph graph = new GlobalPhysicalGraph();

		Iterator<Vertex> vertexIter = lg.iteratorOfVertex();
		while (vertexIter.hasNext()) {
			final Vertex v = vertexIter.next();
			if (v instanceof LogicalVertex) {
				handleLogicalVertex((LogicalVertex)v, graph);
			}
		}

		vertexIter = lg.iteratorOfVertex();
		while (vertexIter.hasNext()) {
			final Vertex v = vertexIter.next();
			if (v instanceof VirtualVertex) {
				handleVirtualVertex((VirtualVertex)v, graph);
			}
		}

		vertexIter = lg.iteratorOfVertex();
		while (vertexIter.hasNext()) {
			final Vertex v = vertexIter.next();
			if (v instanceof SwitcherVertex) {
				handleSwitcherVertex((SwitcherVertex)v, graph);
			}
		}

		Iterator<Edge> iterEdge = lg.iteratorOfEdge();
		while (iterEdge.hasNext()) {
			final Edge edge = iterEdge.next();
			handleEdge(edge, graph);
		}

		return graph;
	}

	private void handleLogicalVertex(final LogicalVertex lv, final GlobalPhysicalGraph graph) {
		if (!lv.getPreferableWorkers().isEmpty() /*|| lv.getDistributingMode() == VertexDistributingMode.STATIC*/) {
			List<String> preferableWorkers = lv.getPreferableWorkers();
			assert (preferableWorkers != null);
			int id = 0;
			for (String hostName : preferableWorkers) {
				final WorkerSignature worker = workerManager.findWorkerSignature(hostName);
				graph.addVertex(lv, worker, id++, 1);
			}
		}
		else if (lv.getDistributingMode() == VertexDistributingMode.FULL ||
				lv.getDistributingMode() == VertexDistributingMode.HASH)
		{
			// We can deploy this vertex to any computers.
			Collection<WorkerServerInstance> workers = workerManager.getAllWorkers();
			int id = 0;
			for (WorkerServerInstance worker : workers) {
				graph.addVertex(lv, worker.getSignature(), id++, 1);
			}
		}
		else if (lv.getDistributingMode() == VertexDistributingMode.SINGLE) {
			List<String> preferableWorkers = lv.getPreferableWorkers();
			if (preferableWorkers != null && !preferableWorkers.isEmpty()) {
				final WorkerSignature worker = workerManager.findWorkerSignature(preferableWorkers.get(0));
				graph.addVertex(lv, worker, 0, 1);
			}
			else {
				graph.addVertex(lv, workerManager.getLowLoadInstance().getSignature(), 0, 1);
			}
		}
		else
			throw new RuntimeException("BUG: Unknown vertex");
	}

	private void handleVirtualVertex(final VirtualVertex v, final GlobalPhysicalGraph graph) {
		final LogicalVertex lv = v.getLogicalVertex();
		List<GlobalPhysicalVertex> gpvs = graph.findGlobalPhysicalVertex(lv);
		int id = 0;
		for (GlobalPhysicalVertex gpv : gpvs) {
			graph.addVertex(v, gpv.getWorkerServerSignature(), id++, 1);
		}
	}

	private void handleSwitcherVertex(final SwitcherVertex v, final GlobalPhysicalGraph graph) {
		final Vertex srcVertex = v.getSourceVertex();
		List<GlobalPhysicalVertex> gpvs = graph.findGlobalPhysicalVertex(srcVertex);
		int id = 0;
		for (GlobalPhysicalVertex gpv : gpvs) {
			graph.addVertex(v, gpv.getWorkerServerSignature(), id++, 1);
		}
	}

	private void handleEdge(final Edge edge, final GlobalPhysicalGraph graph) {
		List<GlobalPhysicalVertex> srcVertices = graph.findGlobalPhysicalVertex(edge.getSrcVertex());

		for (GlobalPhysicalVertex srcVertex : srcVertices) {
			if (edge.getDestDistributingMode() == VertexDistributingMode.LOCAL_FULL) {
				GlobalPhysicalVertex destVertex = graph.findGlobalPhysicalVertex(edge.getDestVertex(), srcVertex.getWorkerServerSignature());
				graph.addEdge(srcVertex, edge.getSrcQueueId(), destVertex, edge.getDestQueueId(), edge);
			}
			else {
				List<GlobalPhysicalVertex> destVertices = graph.findGlobalPhysicalVertex(edge.getDestVertex());
				for (GlobalPhysicalVertex destVertex : destVertices) {
					graph.addEdge(srcVertex, edge.getSrcQueueHandle().getQueueId(), destVertex, edge.getDestQueueHandle().getQueueId(), edge);
				}
			}
		}
	}

	@Override
	public GlobalPhysicalGraph schedule() {
		return null;
	}

	@Override
	public void restoreJob(final QueueLinkerJob job, final GlobalPhysicalGraph graph) {

	}
}
