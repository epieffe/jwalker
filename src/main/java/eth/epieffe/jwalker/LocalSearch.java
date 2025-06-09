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

import java.util.function.Consumer;

/**
 * A local search algorithm that traverses a {@link Graph} to find a
 * target node.<p>
 *
 * A {@code LocalSearch} is used to solve a computationally hard optimization
 * problem. It starts from one or more randomly generated nodes and navigates the
 * {@link Graph} until a node deemed optimal is found or a time bound is elapsed.
 * A {@link Heuristic} is used to evaluate nodes and decide how to navigate the
 * {@link Graph} accordingly.<p>
 *
 * Unlike a {@link Visit}, a {@code LocalSearch} does not return a path, but
 * only one node is returned.<p>
 *
 * Optionally, users can provide a {@link Consumer} that will be called on each
 * explored node when it is visited.<p>
 *
 * The {@link LocalSearches} class provides factory methods for various local search
 * algorithms.
 *
 * @param <N> the type of nodes in the graph traversed by this local search
 *
 * @see Graph
 * @see Heuristic
 * @see LocalSearches
 * @author Epifanio Ferrari
 */
public interface LocalSearch<N> {

    /**
     * Randomly selects one or more starting nodes and navigates the
     * underlying {@link Graph} in search for a node that minimizes
     * the heuristic evaluation. The returned node is not guaranteed
     * to have the lowest heuristic possible. Subsequent runs might
     * find a better node.
     *
     * @return a node that minimizes the heuristic
     */
    default N run() {
        return run(null);
    }

    /**
     * Randomly selects one or more starting nodes and navigates the
     * underlying {@link Graph} in search for a node that minimizes
     * the heuristic evaluation. The returned node is not guaranteed
     * to have the lowest heuristic possible. Subsequent runs might
     * find a better node.<p>
     *
     * If a {@link Consumer} is provided, it will be executed on each
     * explored node when it is visited.
     *
     * @param onVisit a callback that will be executed on each visited node
     * @return a node that minimizes the heuristic
     */
    N run(Consumer<N> onVisit);

    /**
     * Returns the {@link Graph} that is traversed by this local search.
     *
     * @return the graph that is traversed by this local search
     */
    Graph<N> getGraph();

    /**
     * Returns the {@link Heuristic} that is used to evaluate nodes.
     *
     * @return the heuristic that is used to evaluate nodes
     */
    Heuristic<N> getHeuristic();
}
