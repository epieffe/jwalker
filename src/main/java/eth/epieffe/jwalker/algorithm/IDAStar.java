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
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link Visit} that implements IDA* or IDDFS, depending on the parameters
 * provided in the constructor.
 * <p>
 * If no {@link Heuristic} is provided, the behavior is equivalent to a standard
 * iterative deepening depth-first search (IDDFS), while if a non-trivial heuristic
 * function is provided, the behavior corresponds to the IDA* search.
 *
 * @param <N> the type of nodes in the graph traversed by this visit
 *
 * @see Visit
 * @see Heuristic
 * @see Graph
 * @author Epifanio Ferrari
 */
public class IDAStar<N> implements Visit<N> {

    private static final double FOUND = 0;

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    private final Predicate<N> targetPredicate;

    /**
     * Constructs a new instance with the behaviour of the IDA* algorithm.
     * <p>
     * Nodes for which the provided {@link Heuristic} evaluates to zero are
     * considered target nodes.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a {@link Heuristic} instance
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     */
    public IDAStar(Graph<N> graph, Heuristic<N> heuristic) {
        this(graph, heuristic, null);
    }

    /**
     * Constructs a new instance with the behaviour of the IDDFS algorithm.
     * <p>
     * Nodes satisfying the {@code targetPredicate} are considered target nodes.
     *
     * @param graph a {@link Graph} instance
     * @param targetPredicate a predicate that identifies the target nodes
     * @throws NullPointerException if {@code graph} or {@code targetPredicate} is {@code null}
     */
    public IDAStar(Graph<N> graph, Predicate<N> targetPredicate) {
        this(graph, n -> 0, Objects.requireNonNull(targetPredicate));
    }

    /**
     * Constructs a new instance with fully customizable parameters.
     * <p>
     * If {@code targetPredicate} is not {@code null}, it is used to identify target nodes.
     * Otherwise, nodes for which the provided {@link Heuristic} evaluates to zero are
     * considered target nodes.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a {@link Heuristic} function to estimate the cost to the target
     * @param targetPredicate a predicate that identifies target nodes
     * @throws NullPointerException if {@code graph} or {@code heuristic} are {@code null}
     */
    public IDAStar(Graph<N> graph, Heuristic<N> heuristic, Predicate<N> targetPredicate) {
        this.graph = Objects.requireNonNull(graph);
        this.heuristic = Objects.requireNonNull(heuristic);
        this.targetPredicate = targetPredicate;
    }

    @Override
    public List<Edge<N>> run(N node, Consumer<N> onVisit) {
        List<IDANode<N>> stack = new ArrayList<>();
        double nextBound = heuristic.eval(node);
        while (nextBound < Double.MAX_VALUE) {
            double bound = nextBound;
            nextBound = Double.MAX_VALUE;
            stack.add(new IDANode<>(null, null, node, 0));
            while (!stack.isEmpty()) {
                IDANode<N> current = stack.remove(stack.size() - 1);
                double h = heuristic.eval(current.value);
                double f = current.g + h;
                if (f <= bound) {
                    if (onVisit != null) {
                        onVisit.accept(current.value);
                    }
                    if (targetPredicate == null ? h == 0 : targetPredicate.test(current.value)) {
                        return Util.buildPath(current);
                    }
                    for (Edge<N> edge : graph.outgoingEdges(current.value)) {
                        if (!current.hasInPath(edge.destination)) {
                            stack.add(new IDANode<>(current, edge, edge.destination, current.g + edge.weight));
                        }
                    }
                } else {
                    nextBound = Math.min(f, nextBound);
                }
            }
        }
        return null;
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }

    static class IDANode<N> extends Node<N> {
        N value;
        double g;

        public IDANode(IDANode<N> parent, Edge<N> edge, N value, double g) {
            super(parent, edge);
            this.value = value;
            this.g = g;
        }

        boolean hasInPath(N value) {
            IDANode<N> current = this;
            while (current != null) {
                if (current.value.equals(value)) {
                    return true;
                }
                current = (IDANode<N>) current.parent;
            }
            return false;
        }
    }
}
