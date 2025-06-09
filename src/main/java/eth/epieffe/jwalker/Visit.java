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

import java.util.List;
import java.util.function.Consumer;

/**
 * A pathfinding algorithm that traverses a {@link Graph} to find a path from a
 * provided node to a target node.<p>
 *
 * Some {@code Visit} implementations are guaranteed to find a path with the lowest
 * cost possible, while others sacrifice optimality for efficiency. Some implementations
 * use a {@link Heuristic} to decide which nodes to explore with a higher priority.<p>
 *
 * Optionally, users can provide a {@link Consumer} that will be called on each
 * explored node when it is visited.<p>
 *
 * The {@link Visits} class provides factory methods for various pathfinding
 * algorithms such as <i>Dijkstra</i>, <i>A*</i>, <i>Best-first Search</i>
 * and <i>Breadth-first Search</i>.
 *
 * @param <N> the type of nodes in the graph traversed by this visit
 *
 * @see Graph
 * @see Edge
 * @see Heuristic
 * @see Visits
 * @author Epifanio Ferrari
 */
public interface Visit<N> {

    /**
     * Traverses the underlying {@link Graph} and returns a list of
     * edges that bring from the specified node to a target node.<p>
     *
     * If no valid path is found returns {@code null}.
     *
     * @param node a node in the underlying graph
     * @return a path from the specified node to a target node, or
     * {@code null} if no valid path is found
     * @throws NullPointerException if node is {@code null}
     */
    default List<Edge<N>> run(N node) {
        return run(node, null);
    }

    /**
     * Traverses the underlying {@link Graph} and returns a list of
     * edges that bring from the specified node to a target node.<p>
     *
     * If no valid path is found returns {@code null}.<p>
     *
     * If a {@link Consumer} is provided, it will be executed on each
     * explored node when it is visited.
     *
     * @param node a node in the underlying graph
     * @param onVisit a callback that will be executed on each visited node
     * @return a path from the specified node to a target node, or
     * {@code null} if no valid path is found
     * @throws NullPointerException if node is {@code null}
     */
    List<Edge<N>> run(N node, Consumer<N> onVisit);

    /**
     * Returns the {@link Graph} that is traversed by this visit.
     *
     * @return the graph that is traversed by this visit
     */
    Graph<N> getGraph();
}
