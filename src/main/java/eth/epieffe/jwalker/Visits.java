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

import eth.epieffe.jwalker.algorithm.MinCost;
import eth.epieffe.jwalker.algorithm.BFS;
import eth.epieffe.jwalker.algorithm.BestFirst;
import eth.epieffe.jwalker.algorithm.IDAStar;
import eth.epieffe.jwalker.algorithm.ParallelIDAStar;

import java.util.function.Predicate;

/**
 * Factory methods for {@link Visit} classes.
 * <p>
 * This class offers static methods to instantiate different pathfinding algorithms,
 * such as A*, Best-First Search, IDA*, BFS, Dijkstra, and their parallel variant.
 *
 * @see Visit
 * @see Graph
 * @author Epifanio Ferrari
 */
public final class Visits {

    private Visits() {}

    /**
     * Creates a {@link Visit} that implements the <i>A*</i> algorithm.
     * <p>
     * A* uses a {@link Heuristic} to guide the search. If the provided heuristic is
     * consistent, A* is guaranteed to find a path with the lowest cost possible.
     * <p>
     * Nodes for which the heuristic evaluates to zero are considered target nodes.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param heuristic a heuristic for the nodes in the graph
     * @return a visit that traverses the provided graph using the A* algorithm
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> aStar(Graph<N> graph, Heuristic<N> heuristic) {
        return new MinCost<>(graph, heuristic);
    }

    /**
     * Creates a {@link Visit} that implements a variation of the <i>A*</i> algorithm
     * which can be configured to be more greedy and less optimal using a heuristic multiplier.
     * <p>
     * The heuristic value is multiplied by {@code hMul} to influence the search behavior. The
     * higher the value of {@code hMul}, the more greedy the search becomes. If the provided
     * {@link Heuristic} is consistent, this visit is guaranteed to find a path with a total cost
     * less than or equal to {@code min * hMul}, where {@code min} is the lowest cost possible
     * for a valid path.
     * <p>
     * Nodes for which the heuristic evaluates to zero are considered target nodes.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param heuristic a heuristic for the nodes in the graph
     * @param hMul multiplier for the heuristic value
     * @return a visit that traverses the provided graph using the A* algorithm
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     * @throws IllegalArgumentException if {@code hMul} is less than 1
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> aStar(Graph<N> graph, Heuristic<N> heuristic, double hMul) {
        return new MinCost<>(graph, heuristic, null, hMul);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Best-First Search</i> algorithm.
     * <p>
     * Best-First Search is similar to A*, but it always expands the node that appears to
     * be closest to the goal according to the provided {@link Heuristic}. This approach
     * is generally faster than A* but does not guarantee finding the shortest path.
     * <p>
     * Nodes for which the heuristic evaluates to zero are considered target nodes.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param heuristic a heuristic for the nodes in the graph
     * @return a visit that traverses the provided graph using the Best-First Search algorithm
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> bestFirst(Graph<N> graph, Heuristic<N> heuristic) {
        return new BestFirst<>(graph, heuristic);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Iterative Deepening A*</i> (IDA*) algorithm.
     * <p>
     * IDA* combines the space efficiency of depth-first search with the optimality of A*. It
     * repeatedly performs depth-limited searches, increasing the cost threshold at each iteration
     * based on the provided {@link Heuristic} until a solution is found. If the heuristic is
     * consistent, IDA* is guaranteed to find a path with the lowest possible cost.
     * <p>
     * Nodes for which the heuristic evaluates to zero are considered target nodes.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param heuristic a heuristic for the nodes in the graph
     * @return a visit that traverses the provided graph using the IDA* algorithm
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> idaStar(Graph<N> graph, Heuristic<N> heuristic) {
        return new IDAStar<>(graph, heuristic);
    }

    /**
     * Creates a {@link Visit} that implements a parallel version of the <i>Iterative Deepening A*</i> (IDA*) algorithm.
     * <p>
     * This algorithm repeatedly performs depth-limited searches, increasing the cost threshold
     * at each iteration based on the provided {@link Heuristic} until a solution is found. If
     * the heuristic is consistent, it is guaranteed to find a path with the lowest possible cost.
     * The work for each cost-bounded search iteration is distributed among multiple threads.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param heuristic a heuristic for the nodes in the graph
     * @param nThreads the number of threads to use for parallel search
     * @return a visit that traverses the provided graph using the parallel IDA* algorithm
     * @throws NullPointerException if {@code graph} or {@code heuristic} is {@code null}
     * @throws IllegalArgumentException if {@code nThreads} is less than or equal to zero
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> idaStarParallel(Graph<N> graph, Heuristic<N> heuristic, int nThreads) {
        return new ParallelIDAStar<>(graph, heuristic, nThreads);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Breadth-first search</i> (BFS) algorithm.
     * <p>
     * BFS returns a path with the lowest number of edges possible, without considering the
     * weight of the edges. It should be preferred to <i>Dijkstra</i> when all edges in the
     * provided {@link Graph} have the same weight.
     * <p>
     * The exploration ends when a node equal to {@code target} is found.
     *
     * @param graph a {@link Graph} instance
     * @param target the target node to reach
     * @return a visit that traverses the provided graph using the BFS algorithm
     * @throws NullPointerException if {@code graph} or {@code target} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> bfs(Graph<N> graph, N target) {
        return new BFS<>(graph, target::equals);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Breadth-first search</i> (BFS) algorithm.
     * <p>
     * BFS returns a path with the lowest number of edges possible, without considering the
     * weight of the edges. It should be preferred to <i>Dijkstra</i> when all edges in the
     * provided {@link Graph} have the same weight.
     * <p>
     * The exploration ends when a node satisfying the {@code targetPredicate} is found.
     *
     * @param graph a {@link Graph} instance
     * @param targetPredicate a predicate to identify target nodes
     * @return a visit that traverses the provided graph using the BFS algorithm
     * @throws NullPointerException if {@code graph} or {@code targetPredicate} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> bfs(Graph<N> graph, Predicate<N> targetPredicate) {
        return new BFS<>(graph, targetPredicate);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Dijkstra</i> algorithm.
     * <p>
     * Dijkstra is guaranteed to find a path with the lowest cost possible.
     * <p>
     * The exploration ends when a node equal to {@code target} is found.
     *
     * @param graph a {@link Graph} instance
     * @param target the target node to reach
     * @return a visit that traverses the provided graph with the Dijkstra algorithm
     * @throws NullPointerException if {@code graph} or {@code target} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> dijkstra(Graph<N> graph, N target) {
        return new MinCost<>(graph, target::equals);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Dijkstra</i> algorithm.
     * <p>
     * <i>Dijkstra</i> is guaranteed to find a path with the lowest cost possible.
     * <p>
     * The exploration ends when a node satisfying the {@code targetPredicate} is found.
     *
     * @param graph a {@link Graph} instance
     * @param targetPredicate a predicate to identify target nodes
     * @return a visit that traverses the provided graph with the <i>Dijkstra</i> algorithm
     * @throws NullPointerException if {@code graph} or {@code targetPredicate} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> dijkstra(Graph<N> graph, Predicate<N> targetPredicate) {
        return new MinCost<>(graph, targetPredicate);
    }

    /**
     * Creates a {@link Visit} that implements an iterative cost-bounded depth-first search (DFS).
     * <p>
     * This algorithm repeatedly runs a depth-first search, cutting off a branch when its total
     * cost exceeds a given threshold. The given threshold is increased at each iteration, until
     * a solution is found. This algorithm is guaranteed to find a path with the lowest cost possible.
     * <p>
     * The exploration ends when a node equal to {@code target} is found.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param target the target node to reach
     * @return a visit that traverses the provided graph using iterative cost-bounded DFS
     * @throws NullPointerException if {@code graph} or {@code target} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> iddfs(Graph<N> graph, N target) {
        return new IDAStar<>(graph, target::equals);
    }

    /**
     * Creates a {@link Visit} that implements an iterative deepening depth-first search (IDDFS).
     * <p>
     * This algorithm repeatedly runs a depth-first search, cutting off a branch when its total
     * cost exceeds a given threshold. The threshold is increased at each iteration until a
     * solution is found. This algorithm is guaranteed to find a path with the lowest cost possible.
     * <p>
     * The exploration ends when a node satisfying the {@code targetPredicate} is found.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param targetPredicate a predicate to identify target nodes
     * @return a visit that traverses the provided graph using iterative cost-bounded DFS
     * @throws NullPointerException if {@code graph} or {@code target} is {@code null}
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> iddfs(Graph<N> graph, Predicate<N> targetPredicate) {
        return new IDAStar<>(graph, targetPredicate);
    }

    /**
     * Creates a {@link Visit} that implements a parallel iterative deepening depth-first search (IDDFS).
     * <p>
     * This algorithm repeatedly runs a depth-first search, cutting off a branch when its total
     * cost exceeds a given threshold. The threshold is increased at each iteration until a
     * solution is found. This algorithm is guaranteed to find a path with the lowest cost possible.
     * The work for each cost-bounded search iteration is distributed among multiple threads.
     * <p>
     * The exploration ends when a node equal to {@code target} is found.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param target the target node to reach
     * @param nThreads the number of threads to use for parallel search
     * @return a visit that traverses the provided graph using parallel iterative cost-bounded DFS
     * @throws NullPointerException if {@code graph} or {@code target} is {@code null}
     * @throws IllegalArgumentException if {@code nThreads} is less than or equal to zero
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> iddfsParallel(Graph<N> graph, N target, int nThreads) {
        return new ParallelIDAStar<>(graph, target::equals, nThreads);
    }

    /**
     * Creates a {@link Visit} that implements a parallel iterative deepening depth-first search (IDDFS).
     * <p>
     * This algorithm repeatedly runs a depth-first search, cutting off a branch when its total
     * cost exceeds a given threshold. The threshold is increased at each iteration until a
     * solution is found. This algorithm is guaranteed to find a path with the lowest cost possible.
     * The work for each cost-bounded search iteration is distributed among multiple threads.
     * <p>
     * The exploration ends when a node equal to {@code target} is found.
     *
     * @param graph a {@link Graph} instance to traverse
     * @param targetPredicate a predicate to identify target nodes
     * @param nThreads the number of threads to use for parallel search
     * @return a visit that traverses the provided graph using parallel iterative cost-bounded DFS
     * @throws NullPointerException if {@code graph} or {@code target} is {@code null}
     * @throws IllegalArgumentException if {@code nThreads} is less than or equal to zero
     * @param <N> the type of nodes in the graph
     */
    public static <N> Visit<N> iddfsParallel(Graph<N> graph, Predicate<N> targetPredicate, int nThreads) {
        return new ParallelIDAStar<>(graph, targetPredicate, nThreads);
    }
}
