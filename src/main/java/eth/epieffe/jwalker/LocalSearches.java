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
package eth.epieffe.jwalker;

import eth.epieffe.jwalker.algorithm.SteepestDescent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Factory methods for {@link LocalSearch} classes.
 *
 * @see LocalSearch
 * @see Graph
 * @author Epifanio Ferrari
 */
public final class LocalSearches {

    private LocalSearches() {}

    /**
     * Creates a {@link LocalSearch} that implements the <i>Steepest descent</i> algorithm.
     * <p>
     * Steepest descent starts from a random node in the {@link Graph}, selects one of its
     * neighbours with the lowest heuristic evaluation, if the heuristic for that neighbour
     * is less then or equal the heuristic for the current node, then the neighbour is
     * selected as the current node and the process is repeated, until a local optimal node
     * is found.
     * <p>
     * To avoid infinite loops, selecting a node with the heuristic evaluation equal to the
     * current node can only be done a certain amount of times, regulated by the {@code maxSides}
     * parameter.
     *
     * @param graph a {@link Graph} instance
     * @param randomNodeSupplier a supplier that generates a random node in graph
     * @param heuristic a heuristic for the nodes in graph
     * @param maxSides maximum number of times that selecting a node with the same
     *                 heuristic as the current node is allowed
     * @return a local search that traverses the provided graph with the Steepest descent algorithm
     * @throws NullPointerException if {@code graph} or {@code heuristic} or {@code randomNodeSupplier} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> LocalSearch<N> steepestDescent(
            Graph<N> graph,
            Supplier<N> randomNodeSupplier,
            Heuristic<N> heuristic,
            int maxSides
    ) {
        Objects.requireNonNull(randomNodeSupplier);
        return new SteepestDescent<>(graph, randomNodeSupplier, heuristic, maxSides);
    }
}
