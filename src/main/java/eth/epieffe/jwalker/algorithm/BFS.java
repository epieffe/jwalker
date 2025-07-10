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

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static eth.epieffe.jwalker.algorithm.Util.buildPath;

/**
 * A {@link Visit} that implements the <i>Breadth-first search</i> (BFS) algorithm.
 * <p>
 * BFS returns a path with the lowest number of edges possible, without considering
 * the weight of the edges.
 *
 * @param <N> the type of nodes in the graph traversed by this visit
 *
 * @see Visit
 * @see Graph
 * @author Epifanio Ferrari
 */
public final class BFS<N> implements Visit<N> {

    private final Graph<N> graph;

    private final Predicate<N> targetPredicate;

    /**
     * Constructs a new BFS instance with the specified graph and target predicate.
     *
     * @param graph a {@link Graph} instance
     * @param targetPredicate a predicate to identify target nodes
     * @throws NullPointerException if {@code graph} or {@code targetPredicate} is {@code null}
     */
    public BFS(Graph<N> graph, Predicate<N> targetPredicate) {
        this.graph = Objects.requireNonNull(graph);
        this.targetPredicate = Objects.requireNonNull(targetPredicate);
    }

    @Override
    public List<Edge<N>> run(N start, Consumer<N> onVisit) {
        Queue<N> frontier = new ArrayDeque<>();
        Map<N, Node<N>> nodes = new HashMap<>();
        frontier.add(start);
        nodes.put(start, new Node<>(null, null));
        while (!frontier.isEmpty()) {
            N current = frontier.poll();
            Node<N> currentNode = nodes.get(current);
            if (onVisit != null) {
                onVisit.accept(current);
            }
            if (targetPredicate.test(current)) {
                return buildPath(currentNode);
            }
            for (Edge<N> edge : graph.outgoingEdges(current)) {
                Node<N> node = nodes.get(edge.destination);
                if (node == null) {
                    frontier.add(edge.destination);
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
