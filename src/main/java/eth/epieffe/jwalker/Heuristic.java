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

/**
 * A heuristic function for nodes in a {@link Graph}.
 * <p>
 * A {@code Heuristic} provides a rough estimate of the lowest path cost from a provided
 * node in a {@link Graph} to a target node. The cost of a path is the sum of the weights
 * of its constituent edges.
 * <p>
 * Although a heuristic may be inaccurate, it should be quick to evaluate, and it is used
 * by informed search algorithms, such as A* or Best-first Search, to decide which nodes to
 * explore with a higher priority.
 * <p>
 * A heuristic estimate must never be less than zero. If the provided node is a target
 * node, the estimate for that node should be zero.
 *
 * <h2>Consistent heuristics</h2><p>
 * A heuristic is said to be <i>consistent</i> if its estimate is always less than or equal
 * to the estimate for any neighbour of the provided node, plus the cost of reaching that
 * neighbour from the provided node.
 * <p>
 * Consistency is a desirable property for heuristics. When the A* algorithm uses a consistent
 * heuristic, it is guaranteed to always return a path with the lowest cost possible.
 *
 * @param <N> the type of graph nodes this heuristic evaluates
 *
 * @see Graph
 * @see Visits#aStar(Graph, Heuristic)
 * @author Epifanio Ferrari
 */
@FunctionalInterface
public interface Heuristic<N> {

    /**
     * Returns a rough estimate of the lowest path cost from the specified
     * node to a target node.
     * <p>
     * The result is always greater than or equal to zero.
     *
     * @param node a node in a {@link Graph}
     * @return the heuristic evaluation for the provided node
     */
    double eval(N node);
}
