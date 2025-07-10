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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleGraph<N> implements Graph<N> {

    private final Map<N, List<Edge<N>>> edges;

    public static <N> Builder<N> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unused")
    public static <N> Builder<N> builder(Class<N> clazz) {
        return new Builder<>();
    }

    private SimpleGraph(Map<N, List<Edge<N>>> edges) {
        this.edges = edges;
    }

    @Override
    public List<Edge<N>> outgoingEdges(N node) {
        return edges.getOrDefault(node, Collections.emptyList());
    }

    /**
     * Builder class for constructing {@link SimpleGraph} instances.
     * <p>
     * Methods are provided to add edges between nodes. Once all desired edges
     * are added, call {@link #build()} to obtain the resulting {@link SimpleGraph}.
     *
     * @param <N> the type of nodes in the graph
     */
    public static class Builder<N> {

        private final HashMap<N, List<Edge<N>>> edges = new HashMap<>();

        /**
         * Adds an edge between two nodes in the graph. The edge is
         * created with a weight of 1.
         *
         * @param from the source node in the edge
         * @param to the destination node in the edge
         * @return a reference to this object
         */
        public Builder<N> addEdge(N from, N to) {
            return addEdge(from, to, 1, null);
        }

        /**
         * Adds an edge between two nodes in the graph, with a specified weight.
         *
         * @param from the source node in the edge
         * @param to the destination node in the edge
         * @param weight the weight of the edge
         * @return a reference to this object
         */
        public Builder<N> addEdge(N from, N to, double weight) {
            return addEdge(from, to, weight, null);
        }

        /**
         * Adds an edge between two nodes in the graph, with a specified label
         * and a default weight of 1.
         *
         * @param from the source node in the edge
         * @param to the destination node in the edge
         * @param label the label for the edge
         * @return a reference to this object
         */
        public Builder<N> addEdge(N from, N to, String label) {
            return addEdge(from, to, 1, label);
        }

        /**
         * Adds an edge between two nodes in the graph with the specified weight and label.
         *
         * @param from the source node in the edge
         * @param to the destination node in the edge
         * @param weight the weight of the edge
         * @param label the label for the edge
         * @return a reference to this object
         */
        public Builder<N> addEdge(N from, N to, double weight, String label) {
            Edge<N> edge = new Edge<>(label, weight, to);
            edges.computeIfAbsent(from, k -> new ArrayList<>()).add(edge);
            return this;
        }

        /**
         * Returns a new {@link SimpleGraph} instance with the edges defined in this builder.
         *
         * @return a new {@link SimpleGraph} instance
         */
        public SimpleGraph<N> build() {
            Map<N, List<Edge<N>>> unmodifiableEdges = edges.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> Collections.unmodifiableList(new ArrayList<>(e.getValue()))));
            return new SimpleGraph<>(unmodifiableEdges);
        }
    }
}
