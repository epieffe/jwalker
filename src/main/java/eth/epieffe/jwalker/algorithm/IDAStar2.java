package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

public class IDAStar2<N> implements Visit<N> {

    private static final double FOUND = Double.MIN_VALUE;

    private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    public IDAStar2(Graph<N> graph, Heuristic<N> heuristic) {
        this.graph = graph;
        this.heuristic = heuristic;
    }

    @Override
    public List<Edge<N>> run(N node, Consumer<N> onVisit) {
        double bound = heuristic.eval(node);
        IDANode<N> idaNode = new IDANode<>(null, null, node, bound);
        IDANode<N> result;
        while (true) {
            System.out.println("##################################");
            System.out.println("NEW BOUND IDA2: " + bound);
            System.out.println("##################################");
            RecursiveTask<IDANode<N>> task = new RecursiveSearchTask(idaNode, 0, bound);
            result = forkJoinPool.invoke(task);
            if (result == null) return null;
            if (result.f == FOUND) return Util.buildPath(result);
            bound = result.f;
        }
    }

    private class RecursiveSearchTask extends RecursiveTask<IDANode<N>> {

        private final IDANode<N> node;
        private final double g;
        private final double bound;

        public RecursiveSearchTask(IDANode<N> node, double g, double bound) {
            this.node = node;
            this.g = g;
            this.bound = bound;
        }

        @Override
        protected IDANode<N> compute() {
            IDANode<N> min = null;
            List<ForkJoinTask<IDANode<N>>> tasks = new ArrayList<>();
            for (Edge<N> edge : graph.outgoingEdges(node.value)) {
                if (!hasInPath(node, edge.destination)) {
                    double nextG = g + edge.weight;
                    double nextF = nextG + heuristic.eval(edge.destination);
                    IDANode<N> next = new IDANode<>(node, edge, edge.destination, nextF);
                    if (nextF > bound) {
                        if (min == null || nextF < min.f) {
                            min = next;
                        }
                    } else {
                        if (graph.isTarget(next.value)) {
                            next.f = FOUND;
                            return next;
                        }
                        RecursiveTask<IDANode<N>> task = new RecursiveSearchTask(next, nextG, bound);
                        tasks.add(task.fork());
                    }
                }
            }
            for (ForkJoinTask<IDANode<N>> task : tasks) {
                IDANode<N> result = task.join();
                if (result != null && (min == null || result.f < min.f)) {
                    min = result;
                }
            }
            return min;
        }

        private boolean hasInPath(IDANode<N> node, N value) {
            while (node != null) {
                if (value.equals(node.value)) {
                    return true;
                }
                node = (IDANode<N>) node.parent;
            }
            return false;
        }
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }

    private static class IDANode<T> extends Node<T> {
        final T value;
        double f;

        public IDANode(IDANode<T> parent, Edge<T> edge, T value, double f) {
            super(parent, edge);
            this.value = value;
            this.f = f;
        }
    }
}
