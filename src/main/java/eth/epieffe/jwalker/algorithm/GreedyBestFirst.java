/*
 * Copyright 2025 Epifanio Ferrari
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
package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Visit;
import eth.epieffe.jwalker.Heuristic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static eth.epieffe.jwalker.algorithm.Util.buildPath;

public final class GreedyBestFirst<N> implements Visit<N> {

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    public GreedyBestFirst(Graph<N> graph, Heuristic<N> heuristic) {
        this.graph = Objects.requireNonNull(graph);
        this.heuristic = Objects.requireNonNull(heuristic);
    }

    @Override
    public List<Edge<N>> run(N start, Consumer<N> onVisit) {
        FibonacciHeap<N> openSet = new FibonacciHeap<>();
        Map<N, Node<N>> nodes = new HashMap<>();
        openSet.insert(0, start);
        nodes.put(start, new Node<>(null, null));

        while (!openSet.isEmpty()) {
            N current = openSet.deleteMin().getValue();
            Node<N> currentNode = nodes.get(current);
            if (onVisit != null) {
                onVisit.accept(current);
            }
            if (graph.isTarget(current)) {
                return buildPath(currentNode);
            }
            for (Edge<N> edge : graph.outgoingEdges(current)) {
                Node<N> node = nodes.get(edge.destination);
                if (node == null) {
                    openSet.insert(heuristic.eval(edge.destination), edge.destination);
                    nodes.put(edge.destination, new Node<>(currentNode, edge));
                }
            }
        }
        // No solution found
        return null;
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }
}
