package eth.epieffe.jwalker;

import java.util.List;

/**
 * A graph that can be traversed by a search algorithm such as <i>A*</i> or
 * <i>Dijkstra</i>. Methods are provided to retrieve the outgoing edges of a
 * specified node, and to check if a specified node is a target.<p>
 *
 * A {@code Graph} describes a search problem, and can be traversed with a
 * {@link Visit} or a {@link LocalSearch}.<p>
 *
 * Nodes of a {@code Graph} can be of any type, but they should implement
 * the {@code equals} and {@code hashCode} methods correctly.
 *
 * @param <N> the type of nodes in this graph
 *
 * @see Visit
 * @see LocalSearch
 * @see Edge
 * @author Epifanio Ferrari
 */
public interface Graph<N> {

    /**
     * Returns the edges that bring from the specified node to its neighbours.
     *
     * @param node a node in this graph
     * @return the outgoing edges of the specified node
     * @throws NullPointerException if node is {@code null}
     */
    List<Edge<N>> outgoingEdges(N node);

    /**
     * Returns {@code true} if the specified node is a target.
     *
     * @param node a node in this graph
     * @return {@code true} if the specified node is a target
     * @throws NullPointerException if node is {@code null}
     */
    boolean isTarget(N node);
}
