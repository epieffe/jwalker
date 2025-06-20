package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IDAStar<N> implements Visit<N> {

    private static final double FOUND = 0;

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    public IDAStar(Graph<N> graph, Heuristic<N> heuristic) {
        this.graph = graph;
        this.heuristic = heuristic;
    }


    @Override
    public List<Edge<N>> run(N node, Consumer<N> onVisit) {
        double bound = heuristic.eval(node);
        List<N> stack = new ArrayList<>();
        List<Edge<N>> path = new ArrayList<>();
        stack.add(node);
        while (bound < Double.MAX_VALUE) {
            System.out.println("NEW BOUND: " + bound);
            System.out.println("##################################");
            double t = search(stack, path, 0, bound);
            if (t == FOUND) return path;
            bound = t;
        }
        // No solution found
        return null;
    }

    private double search(List<N> stack, List<Edge<N>> path, double g, double bound) {
        N node = stack.get(stack.size() - 1);
        double f = g + heuristic.eval(node);
        if (f > bound) return f;
        if (graph.isTarget(node)) return FOUND;
        double min = Double.MAX_VALUE;
        for (Edge<N> edge : graph.outgoingEdges(node)) {
            if (!stack.contains(edge.destination)) {
                stack.add(edge.destination);
                path.add(edge);
                double t = search(stack, path, g + edge.weight, bound);
                if (t == FOUND) return FOUND;
                if (t < min) min = t;
                stack.remove(stack.size() - 1);
                path.remove(path.size() - 1);
            }
        }
        return min;
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }
}
