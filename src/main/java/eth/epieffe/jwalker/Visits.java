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

import eth.epieffe.jwalker.algorithm.AStar;
import eth.epieffe.jwalker.algorithm.BFS;
import eth.epieffe.jwalker.algorithm.GreedyBestFirst;
import eth.epieffe.jwalker.algorithm.IDAStar;
import eth.epieffe.jwalker.algorithm.ParallelIDAStar;

/**
 * Factory methods for {@link Visit} classes.
 *
 * @see Visit
 * @see Graph
 * @author Epifanio Ferrari
 */
public final class Visits {

    private Visits() {}

    /**
     * Creates a {@link Visit} that implements the <i>A*</i> algorithm.<p>
     *
     * <i>A*</i> uses a {@link Heuristic} to determine which node to explore during
     * each iteration. Specifically, it selects the node {@code n} that minimizes
     * {@code g + h}, where {@code g} is the cost of the path from the start node
     * to {@code n}, and {@code h} is the heuristic estimate for {@code n}.<p>
     *
     * When <i>A*</i> uses a consistent heuristic, it is guaranteed to always return
     * a path with the lowest cost possible. The cost of a path is the sum of the
     * weights of its constituent edges.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a heuristic for the nodes in graph
     * @return a visit that traverses the provided graph with the <i>A*</i> algorithm
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> aStar(Graph<N> graph, Heuristic<N> heuristic) {
        return new AStar<>(graph, heuristic);
    }

    /**
     * Creates a {@link Visit} that implements a variation of the <i>A*</i> algorithm that
     * can be configured to be more greedy and less optimal.<p>
     *
     * This algorithm uses a {@link Heuristic} to determine which node to explore
     * during each iteration. Specifically, it selects the node {@code n} that minimizes
     * {@code g + (h * hMul)}, where {@code g} is the cost of the path from the start node
     * to {@code n}, and {@code h} is the heuristic estimate for {@code n}.<p>
     *
     * The higher {@code hMul} is, the more greedy this algorithm becomes. When {@code hMul}
     * is equal to 1,  this algorithm behaves exactly like the standard <i>A*</i>.
     * {@code hMul} cannot be lower than 1.<p>
     *
     * When this algorithm uses a consistent heuristic, it is guaranteed to always return
     * a path with a cost less than or equal {@code min * hMul}, where {@code min} is the
     * lowest cost possible for a valid path. The cost of a path is the sum of the
     * weights of its constituent edges.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a heuristic for the nodes in graph
     * @param hMul the heuristic multiplier
     * @return a visit that traverses the provided graph with the <i>A*</i> algorithm
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     * @throws IllegalArgumentException if hMul is less then 1
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> aStar(Graph<N> graph, Heuristic<N> heuristic, double hMul) {
        return new AStar<>(graph, heuristic, hMul);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Best-first Search</i> algorithm.<p>
     *
     * <i>Best-first Search</i> uses a {@link Heuristic} to determine which node to explore
     * during each iteration. Specifically, it selects the node with the lowest heuristic
     * estimate.<p>
     *
     * <i>Best-first Search</i> is generally very fast, but it does not offer any guarantee
     * on the cost of the returned path
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a heuristic for the nodes in graph
     * @return a visit that traverses the provided graph with the <i>Best-first Search</i> algorithm
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> greedyBestFirst(Graph<N> graph, Heuristic<N> heuristic) {
        return new GreedyBestFirst<>(graph, heuristic);
    }

    /**
     * Creates a {@link Visit} that implements the <i>IDA*</i> algorithm.<p>
     *
     * <i>IDA*</i> repeatedly runs a depth-first search, cutting off a branch when its
     * total cost exceeds a given threshold. The given threshold is increased at each
     * iteration, until a goal is found.<p>
     *
     * While the standard iterative deepening depth-first search uses search depth as
     * the cutoff for each iteration, <i>IDA*</i> uses {@code g + h}, where {@code g}
     * is the cost of travelling from the root node to the current node {@code n}, and
     * {@code h} is the heuristic estimate for {@code n}.<p>
     *
     * Compared to <i>A*</i>, <i>IDA*</i> uses much less memory, but often ends up exploring
     * the same nodes many times.<p>
     *
     * When <i>IDA*</i> uses a consistent heuristic, it is guaranteed to always return
     * a path with the lowest cost possible. The cost of a path is the sum of the
     * weights of its constituent edges.
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a heuristic for the nodes in graph
     * @return a visit that traverses the provided graph with the <i>IDA*</i> algorithm
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> idaStar(Graph<N> graph, Heuristic<N> heuristic) {
        return new IDAStar<>(graph, heuristic);
    }

    /**
     * Creates a {@link Visit} that implements a parallel version of the <i>IDA*</i> algorithm.<p>
     *
     * The work done in each cost-bounded search iteration is shared among a number of threads.
     * When a thread has finished its work, it tries to steal some work from the other threads.
     * When all the work in the current iteration is done, the threads detect termination and a
     * new iteration is started, until a solution is found.
     *
     * @see Visits#idaStar(Graph, Heuristic)
     *
     * @param graph a {@link Graph} instance
     * @param heuristic a heuristic for the nodes in graph
     * @param nThreads number of threads to use
     * @return a visit that traverses the provided graph with the parallel <i>IDA*</i> algorithm
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     * @throws IllegalArgumentException if nThreads is less then or equal to zero
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> parallelIDAStar(Graph<N> graph, Heuristic<N> heuristic, int nThreads) {
        return new ParallelIDAStar<>(graph, heuristic, nThreads);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Breadth-first search</i> algorithm,
     * also known as <i>BFS</i>.<p>
     *
     * <i>BFS</i> is guaranteed to return a path with the lowest number of edges possible.
     * It does not consider the weight of the edges.<p>
     *
     * When all the edges in the provided {@link Graph} have the same weight, <i>BFS</i>
     * behaves like <i>Dijkstra</i>, but it is more efficient.
     *
     * @param graph a {@link Graph} instance
     * @return a visit that traverses the provided graph with the <i>BFS</i> algorithm
     * @throws NullPointerException if graph is {@code null}
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> bfs(Graph<N> graph) {
        return new BFS<>(graph);
    }

    /**
     * Creates a {@link Visit} that implements the <i>Dijkstra</i> algorithm.<p>
     *
     * <i>Dijkstra</i> is guaranteed to always return a path with the lowest cost possible.
     * The cost of a path is the sum of the weights of its constituent edges.
     *
     * @param graph a {@link Graph} instance
     * @return a visit that traverses the provided graph with the <i>Dijkstra</i> algorithm
     * @throws NullPointerException if graph is {@code null}
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> dijkstra(Graph<N> graph) {
        return new AStar<>(graph, n -> 0);
    }

    /**
     * Creates a {@link Visit} that implements an iterative cost-bounded <i>DFS</i> algorithm.<p>
     *
     * This algorithm repeatedly runs a depth-first search, cutting off a branch when its
     * total cost exceeds a given threshold. The given threshold is increased at each
     * iteration, until a goal is found.<p>
     *
     * Compared to <i>Dijkstra</i>, this visit uses much less memory, but often ends up
     * exploring the same nodes many times.<p>
     *
     * This visit is guaranteed to always return a path with the lowest cost possible.
     * The cost of a path is the sum of the weights of its constituent edges.
     *
     * @param graph a {@link Graph} instance
     * @return a visit that traverses the provided graph with an iterative cost-bounded <i>DFS</i> algorithm
     * @throws NullPointerException if graph is {@code null}
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> iterativeBoundedDFS(Graph<N> graph) {
        return new IDAStar<>(graph, n -> 0);
    }

    /**
     * Creates a {@link Visit} that implements a parallel iterative cost-bounded <i>DFS</i>.<p>
     *
     * The work done in each cost-bounded search iteration is shared among a number of threads.
     * When a thread has finished its work, it tries to steal some work from the other threads.
     * When all the work in the current iteration is done, the threads detect termination and a
     * new iteration is started, until a solution is found.
     *
     * @see Visits#iterativeBoundedDFS(Graph)
     *
     * @param graph a {@link Graph} instance
     * @param nThreads number of threads to use
     * @return a visit that traverses the provided graph with a parallel iterative cost-bounded <i>DFS</i> algorithm
     * @throws NullPointerException if graph is {@code null} or heuristic is {@code null}
     * @throws IllegalArgumentException if nThreads is less then or equal to zero
     * @param <N> the type of nodes in graph
     */
    public static <N> Visit<N> parallelIterativeBoundedDFS(Graph<N> graph, int nThreads) {
        return new ParallelIDAStar<>(graph, n -> 0, nThreads);
    }
}
