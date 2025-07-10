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
package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Heuristic;
import eth.epieffe.jwalker.Visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static eth.epieffe.jwalker.algorithm.IDAStar.IDANode;

public class ParallelIDAStar<N> implements Visit<N> {

    private static final ExecutorService executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Integer.MAX_VALUE,
            60, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    private final Graph<N> graph;

    private final Heuristic<N> heuristic;

    private final Predicate<N> targetPredicate;

    private final int nThreads;

    public ParallelIDAStar(Graph<N> graph, Heuristic<N> heuristic, int nThreads) {
        this(graph, heuristic, null, nThreads);
    }

    public ParallelIDAStar(Graph<N> graph, Predicate<N> targetPredicate, int nThreads) {
        this(graph, n -> 0, Objects.requireNonNull(targetPredicate), nThreads);
    }

    public ParallelIDAStar(Graph<N> graph, Heuristic<N> heuristic, Predicate<N> targetPredicate, int nThreads) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException("Argument nThreads must be greater than zero");
        }
        this.graph = Objects.requireNonNull(graph);
        this.heuristic = Objects.requireNonNull(heuristic);
        this.targetPredicate = targetPredicate;
        this.nThreads = nThreads;
    }

    @Override
    public List<Edge<N>> run(N node, Consumer<N> onVisit) {
        PIDATask task = new PIDATask(onVisit);
        return task.run(node);
    }

    @Override
    public Graph<N> getGraph() {
        return graph;
    }

    private enum Colour { BLACK, WHITE }

    private static class Processor<N> {
        final Object lock;
        IDANode<N>[] stack;
        Colour colour;
        double nextBound;
        int depth;
        volatile int excDepth;
        volatile int head;

        @SuppressWarnings("unchecked")
        Processor() {
            this.lock = new Object();
            this.stack = new IDANode[60];
            this.colour = Colour.WHITE;
            this.nextBound = Double.MAX_VALUE;
        }

        void stackPush(IDANode<N> elem) {
            if (depth == stack.length) {
                int newLength = depth + (depth >> 1);
                stack = Arrays.copyOf(stack, newLength);
            }
            stack[depth++] = elem;
        }

        IDANode<N> stackPop() {
            return stack[--depth];
        }
    }

    private class PIDATask {
        private final int getWorkRetry;

        Consumer<N> onVisit;

        Processor<N>[] processors;
        double bound;

        volatile IDANode<N> solution;
        volatile boolean quit;

        Colour tokenColour;
        volatile int tokenHolder;

        @SuppressWarnings("unchecked")
        PIDATask(Consumer<N> onVisit) {
            this.getWorkRetry = Math.min(3, nThreads - 1);
            this.onVisit = onVisit;
            this.processors = new Processor[nThreads];
            this.tokenColour = Colour.BLACK;
            for (int i = 0; i < nThreads; ++i) {
                processors[i] = new Processor<>();
            }
        }

        List<Edge<N>> run(N node) {
            List<Future<?>> futures = new ArrayList<>(nThreads);
            bound = heuristic.eval(node);
            while (bound < Double.MAX_VALUE) {
                futures.clear();
                processors[0].stackPush(new IDANode<>(null, null, node, 0));
                for (int i = 0; i < nThreads - 1; ++i) {
                    final int procId = i;
                    futures.add(executor.submit(() -> processorLoop(procId)));
                }
                processorLoop(nThreads - 1);
                try {
                    for (Future<?> future : futures) {
                        future.get();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (solution != null) {
                    return Util.buildPath(solution);
                }
                // Compute next cost bound and prepare for next iteration
                bound = Double.MAX_VALUE;
                for (Processor<N> processor : processors) {
                    if (processor.nextBound < bound) {
                        bound = processor.nextBound;
                    }
                    processor.nextBound = Double.MAX_VALUE;
                    processor.colour = Colour.WHITE;
                }
                tokenColour = Colour.BLACK;
                quit = false;
            }
            return null;
        }

        private void processorLoop(int procId) {
            Processor<N> proc = processors[procId];
            while (!quit && solution == null) {
                if (proc.depth > proc.head) {
                    boundedDFS(proc);
                } else {
                    if (!getWork(procId)) {
                        checkTermination(procId);
                    }
                }
            }
            proc.head = proc.excDepth = proc.depth = 0;
        }

        private void boundedDFS(Processor<N> proc) {
            while (proc.depth > proc.head && solution == null) {
                IDANode<N> current = proc.stackPop();

                if (proc.depth < proc.excDepth) {
                    synchronized (proc.lock) {
                        proc.excDepth = (proc.depth + proc.head) / 2;
                    }
                }

                double h = heuristic.eval(current.value);
                double f = current.g + h;
                if (f <= bound) {
                    if (onVisit != null) {
                        onVisit.accept(current.value);
                    }
                    if (targetPredicate == null ? h == 0 : targetPredicate.test(current.value)) {
                        solution = current;
                        return;
                    }
                    for (Edge<N> edge : graph.outgoingEdges(current.value)) {
                        if (!current.hasInPath(edge.destination)) {
                            proc.stackPush(new IDANode<>(current, edge, edge.destination, current.g + edge.weight));
                        }
                    }
                    int half = (proc.depth + proc.head) / 2;
                    if (half > proc.excDepth) {
                        proc.excDepth = half;
                    }
                } else {
                    proc.nextBound = Math.min(f, proc.nextBound);
                }
            }
        }

        private boolean getWork(int procId) {
            Processor<N> proc = processors[procId];
            for (int j = 1; j <= getWorkRetry; ++j) {
                int targetId = (procId + j) % processors.length;
                Processor<N> target = processors[targetId];
                if (target.excDepth > target.head) {
                    synchronized (target.lock) {
                        int excDepth = target.excDepth;
                        for (int i = target.head; i < excDepth; ++i) {
                            proc.stackPush(target.stack[i]);
                        }
                        if (procId > targetId) {
                            target.colour = Colour.BLACK;
                        }
                        target.head = excDepth;
                    }
                    return true;
                }
            }
            return false;
        }

        private void checkTermination(int procId) {
            if (procId != tokenHolder) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.err.println("Thread was interrupted");
                }
                return;
            }
            Processor<N> proc = processors[procId];
            if (procId == 0) {
                if (tokenColour == Colour.WHITE && proc.colour == Colour.WHITE) {
                    // Successful termination detection
                    quit = true;
                } else {
                    // Initiate probe
                    tokenColour = Colour.WHITE;
                    proc.colour = Colour.WHITE;
                    tokenHolder = processors.length - 1;
                }
            } else {
                // Send token to next processor
                if (proc.colour == Colour.BLACK) {
                    tokenColour = Colour.BLACK;
                    proc.colour = Colour.WHITE;
                }
                tokenHolder = (procId - 1 + processors.length) % processors.length;
            }
        }
    }
}
