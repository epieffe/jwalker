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
import eth.epieffe.jwalker.LocalSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link LocalSearch} that implements the <i>Steepest descent</i> algorithm.
 *
 * @param <N> the type of nodes in the graph traversed by this local search
 *
 * @see LocalSearch
 * @see Heuristic
 * @see Graph
 * @author Epifanio Ferrari
 */
public final class SteepestDescent<N> implements LocalSearch<N> {

    private final Random random = new Random();

    private final Graph<N> graph;

    private final Supplier<N> randomNodeSupplier;

    private final Heuristic<N> heuristic;

    private final int maxSides;

    /**
     * Allocates a {@code SteepestDescent} object.
     *
     * @param graph a {@link Graph} instance
     * @param randomNodeSupplier a supplier that generates a random node
     * @param heuristic a {@link Heuristic} instance
     * @param maxSides maximum number of allowed side moves
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     */
    public SteepestDescent(
            Graph<N> graph,
            Supplier<N> randomNodeSupplier,
            Heuristic<N> heuristic,
            int maxSides
    ) {
        if (maxSides < 0) {
            throw new IllegalArgumentException("Argument maxSides must not be negative");
        }
        this.graph = Objects.requireNonNull(graph);
        this.randomNodeSupplier = randomNodeSupplier;
        this.heuristic = Objects.requireNonNull(heuristic);
        this.maxSides = maxSides;
    }

    @Override
    public N run(Consumer<N> onVisit) {
        N start = randomNodeSupplier.get();
        return run(start, onVisit);
    }

    /**
     * Run the local search starting from the provided node.
     *
     * @param node a node in the underlying graph
     * @return a node that minimizes the heuristic
     * @throws NullPointerException if node is {@code null}
     */
    public N run(N node) {
        return run(node, null);
    }

    /**
     * Run the local search starting from the provided node.
     *
     * @param node a node in the underlying graph
     * @param onVisit a callback that will be executed on each visited node
     * @return a node that minimizes the heuristic
     * @throws NullPointerException if node is {@code null}
     */
    public N run(N node, Consumer<N> onVisit) {
        N sol = null;
        N localBest = node;
        double localBestH = heuristic.eval(node);
        int countSides = 0;
        while (sol == null) {
            if (onVisit != null) {
                onVisit.accept(localBest);
            }
            double oldBestH = localBestH;
            List<Edge<N>> edges = graph.outgoingEdges(localBest);
            List<N> bestMoveList = new ArrayList<>();
            for (Edge<N> edge : edges) {
                double newH = heuristic.eval(edge.destination);
                if (newH <= localBestH) {
                    if (newH < localBestH) {
                        localBestH = newH;
                        bestMoveList.clear();
                    }
                    bestMoveList.add(edge.destination);
                }
            }
            if (!bestMoveList.isEmpty()) {
                int randomIndex = random.nextInt(bestMoveList.size());
                localBest = bestMoveList.get(randomIndex);
                if (localBestH == oldBestH) {
                    countSides++;
                    if (countSides >= maxSides) {
                        sol = localBest;
                    }
                }
            } else {
                sol = localBest;
            }
        }
        return sol;
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }

    @Override
    public Heuristic<N> getHeuristic() {
        return heuristic;
    }
}
