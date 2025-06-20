package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

public class IDAStar3<N> implements Visit<N> {

    private static final double FOUND = Double.MIN_VALUE;

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    public IDAStar3(Graph<N> graph, Heuristic<N> heuristic) {
        this.graph = graph;
        this.heuristic = heuristic;
    }

    @Override
    public List<Edge<N>> run(N node, Consumer<N> onVisit) {
        double bound = heuristic.eval(node);
        List<N> nodes = new ArrayList<>();
        List<Edge<N>> path = new ArrayList<>();
        nodes.add(node);
        while (bound < Double.MAX_VALUE) {
            System.out.println("NEW BOUND: " + bound);
            System.out.println("##################################");
            RecursiveTask<SearchResult<N>> task  = new RecursiveSearchTask(0, bound, nodes, path);
            SearchResult<N> result = forkJoinPool.invoke(task);
            if (result.f == FOUND) return result.path;
            bound = result.f;
        }
        // No solution found
        return null;
    }

    private class RecursiveSearchTask extends RecursiveTask<SearchResult<N>> {

        private final double g;
        private final double bound;
        private final List<N> nodes;
        private final List<Edge<N>> path;

        public RecursiveSearchTask(double g, double bound, List<N> nodes, List<Edge<N>> path) {
            this.g = g;
            this.bound = bound;
            this.nodes = nodes;
            this.path = path;
        }

        @Override
        protected SearchResult<N> compute() {
            N node = nodes.get(nodes.size() - 1);
            double f = g + heuristic.eval(node);
            if (f > bound) {
                return new SearchResult<>(f, path);
            }
            if (graph.isTarget(node)) {
                return new SearchResult<>(FOUND, path);
            }
            if (nodes.size() >= 3) {
                double t = search(nodes, path, g, bound);
                return new SearchResult<>(t, path);
            }
            SearchResult<N> min = null;
            List<ForkJoinTask<SearchResult<N>>> tasks = new ArrayList<>();
            for (Edge<N> edge : graph.outgoingEdges(node)) {
                if (!nodes.contains(edge.destination)) {
                    List<N> nextNodes = new ArrayList<>(nodes);
                    List<Edge<N>> nextPath = new ArrayList<>(path);
                    nextNodes.add(edge.destination);
                    nextPath.add(edge);
                    ForkJoinTask<SearchResult<N>> task = new RecursiveSearchTask(
                            g + edge.weight,
                            bound,
                            nextNodes,
                            nextPath);
                    tasks.add(task.fork());
                }
            }
            for (ForkJoinTask<SearchResult<N>> task : tasks) {
                SearchResult<N> result = task.join();
                if (result.f == FOUND) {
                    return result;
                }
                if (min == null || result.f < min.f) {
                    min = result;
                }
            }
            return min;
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
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }

    private static class SearchResult<N> {
        final double f;
        final List<Edge<N>> path;

        public SearchResult(double f, List<Edge<N>> path) {
            this.f = f;
            this.path = path;
        }
    }
}
