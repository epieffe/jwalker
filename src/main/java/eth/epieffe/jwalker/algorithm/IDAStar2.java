package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IDAStar2<N> implements Visit<N> {

    private static final double FOUND = 0;

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    public IDAStar2(Graph<N> graph, Heuristic<N> heuristic) {
        this.graph = graph;
        this.heuristic = heuristic;
    }

    @Override
    public List<Edge<N>> run(N node, Consumer<N> onVisit) {
        double bound = heuristic.eval(node);
        List<IDANode<N>> stack = new ArrayList<>();
        while (bound < Double.MAX_VALUE) {
            stack.clear();
            stack.add(new IDANode<>(null, null, node, 0));
            IDANode<N> t = search(stack, bound);
            if (t.f == FOUND) {
                return Util.buildPath(t);
            };
            bound = t.f;
        }
        // No solution found
        return null;
    }

    private IDANode<N> search(List<IDANode<N>> stack, double bound) {
        IDANode<N> min = null;
        while (!stack.isEmpty()) {
            IDANode<N> current = stack.remove(stack.size() - 1);
            current.f = current.g + heuristic.eval(current.value);
            if (current.f <= bound) {
                if (graph.isTarget(current.value)) {
                    current.f = FOUND;
                    return current;
                }
                for (Edge<N> edge : graph.outgoingEdges(current.value)) {
                    if (!contains(stack, edge.destination)) {
                        double g = current.g + edge.weight;
                        stack.add(new IDANode<>(current, edge, edge.destination, g));
                    }
                }
            } else if (min == null || current.f < min.f) {
                min = current;
            }
        }
        return min;
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }

    private static <T> boolean contains(List<IDANode<T>> stack, Object node) {
        for (IDANode<T> n : stack) {
            if (node.equals(n.value)) {
                return true;
            }
        }
        return false;
    }

    private static <T> List<Edge<T>> buildPath(List<IDANode<T>> stack) {
        List<Edge<T>> path = new ArrayList<>();
        for (int i = stack.size() - 1; i > 0; --i) {
            path.add(stack.get(i).edge);
        }
        return path;
    }

    private static class IDANode<T> extends Node<T> {
        final T value;
        final double g;
        double f;

        public IDANode(IDANode<T> parent, Edge<T> edge, T value, double g) {
            super(parent, edge);
            this.value = value;
            this.g = g;
        }
    }
}
