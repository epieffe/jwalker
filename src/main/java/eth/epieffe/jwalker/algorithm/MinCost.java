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

import eth.epieffe.jwalker.Visit;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static eth.epieffe.jwalker.algorithm.Util.buildPath;

/**
 * A {@link Visit} that implements A* or Dijkstra, depending on the parameters
 * provided in the constructor.
 * <p>
 * If no {@link Heuristic} is provided, the behavior is equivalent to Dijkstra's
 * algorithm, while if a non-trivial heuristic function is provided, the behavior
 * corresponds to A* search.
 *
 * @param <N> the type of nodes in the graph traversed by this visit
 *
 * @see Visit
 * @see Heuristic
 * @see Graph
 * @author Epifanio Ferrari
 */
public final class MinCost<N> implements Visit<N> {

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    private final Predicate<N> targetPredicate;

    private final double hMul;

    /**
     * Constructs a {@code MinCost} instance with the behaviour of the A* algorithm.
     * <p>
     * Nodes for which the provided {@link Heuristic} evaluates to zero are
     * considered target nodes.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a {@link Heuristic} instance
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     */
    public MinCost(Graph<N> graph, Heuristic<N> heuristic) {
        this(graph, heuristic, 1);
    }

    /**
     * Constructs a {@code MinCost} instance with the behaviour of the A* algorithm.
     * <p>
     * The heuristic value is multiplied by {@code hMul}. The higher the value
     * of {@code hMul}, the more greedy the search becomes.
     * <p>
     * Nodes for which the provided {@link Heuristic} evaluates to zero are
     * considered target nodes.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a {@link Heuristic} instance
     * @param hMul the heuristic multiplier
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     */
    public MinCost(Graph<N> graph, Heuristic<N> heuristic, int hMul) {
        this(graph, heuristic, null, hMul);
    }

    /**
     * Constructs a {@code MinCost} instance with the behaviour of the Dijkstra's algorithm.
     * <p>
     * Nodes satisfying the {@code targetPredicate} are considered target nodes.
     *
     * @param graph a {@link Graph} instance
     * @param targetPredicate a predicate that identifies the target nodes
     * @throws NullPointerException if {@code graph} or {@code targetPredicate} is {@code null}
     */
    public MinCost(Graph<N> graph, Predicate<N> targetPredicate) {
        this(graph, n -> 0, Objects.requireNonNull(targetPredicate), 1);
    }

    /**
     * Constructs a {@code MinCost} instance with fully customizable parameters.
     * <p>
     * If {@code targetPredicate} is not {@code null}, it is used to identify target nodes.
     * Otherwise, nodes for which the provided {@link Heuristic} evaluates to zero are
     * considered target nodes. The heuristic value is multiplied by {@code hMul}; higher
     * values of {@code hMul} make the search more greedy.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a {@link Heuristic} function to estimate the cost to the target
     * @param targetPredicate a predicate that identifies target nodes, or {@code null}
     * @param hMul the heuristic multiplier
     * @throws NullPointerException if {@code graph} or {@code heuristic} are {@code null}
     * @throws IllegalArgumentException if {@code hMul} is less than 1
     */
    public MinCost(Graph<N> graph, Heuristic<N> heuristic, Predicate<N> targetPredicate, double hMul) {
        if (hMul < 1) {
            throw new IllegalArgumentException("Argument hMul must be >= 1");
        }
        this.graph = Objects.requireNonNull(graph);
        this.heuristic = Objects.requireNonNull(heuristic);
        this.targetPredicate = targetPredicate;
        this.hMul = hMul;
    }

    @Override
    public List<Edge<N>> run(N start, Consumer<N> onVisit) {
        FibonacciHeap<N> openSet = new FibonacciHeap<>();
        Map<N, ANode<N>> nodes = new HashMap<>();
        FibonacciHeap.Handle<N> startHandle = openSet.insert(0, start);
        ANode<N> startNode = new ANode<>(null, null, startHandle, 0, heuristic.eval(start));
        nodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            N current = openSet.deleteMin().getValue();
            ANode<N> currentNode = nodes.get(current);
            currentNode.expand();
            if (onVisit != null) {
                onVisit.accept(current);
            }
            if (targetPredicate == null ? currentNode.h == 0 : targetPredicate.test(current)) {
                return buildPath(currentNode);
            }
            for (Edge<N> edge : graph.outgoingEdges(current)) {
                double g = currentNode.g + edge.weight;
                ANode<N> node = nodes.get(edge.destination);
                if (node == null) {
                    double h = heuristic.eval(edge.destination);
                    double f = g + (h * hMul);
                    FibonacciHeap.Handle<N> handle = openSet.insert(f, edge.destination);
                    node = new ANode<>(currentNode, edge, handle, g, h);
                    nodes.put(edge.destination, node);
                } else if (!node.isExpanded() && g < node.g) {
                    double f = g + (node.h * hMul);
                    node.g = g;
                    node.parent = currentNode;
                    node.edge = edge;
                    node.handle.decreaseKey(f);
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

    private static class ANode<T> extends Node<T> {
        FibonacciHeap.Handle<T> handle;
        double g;
        double h;

        ANode(ANode<T> parent, Edge<T> edge, FibonacciHeap.Handle<T> handle, double g, double h) {
            super(parent, edge);
            this.handle = handle;
            this.g = g;
            this.h = h;
        }

        void expand() {
            handle = null;
        }

        boolean isExpanded() {
            return handle == null;
        }
    }
}
