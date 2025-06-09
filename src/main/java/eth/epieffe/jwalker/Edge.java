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

import java.util.Objects;

/**
 * An edge that connects a source node to a destination node in a {@link Graph}.<p>
 *
 * @param <T> the type of the destination node in this edge
 */
public final class Edge<T> {

    /**
     * The label of this edge.
     */
    public final String label;

    /**
     * The cost of traversing this edge to reach the destination node
     * from the source node.
     */
    public final double weight;

    /**
     * The destination node of this edge.
     */
    public final T destination;

    /**
     * Creates a new {@code Edge}.
     *
     * @param label the label of the new edge
     * @param weight the weight of the new edge
     * @param destination the destination node of the new edge
     */
    public Edge(String label, double weight, T destination) {
        this.label = label;
        this.weight = weight;
        this.destination = destination;
    }

    /**
     * Returns the label of this edge.
     *
     * @return the label of this edge
     */
    public String label() {
        return label;
    }

    /**
     * Returns the cost of traversing this edge to reach
     * the destination node from the source node.
     *
     * @return the weight of this edge
     */
    public double weight() {
        return weight;
    }

    /**
     * Returns the destination node of this edge.
     *
     * @return the destination node of this edge
     */
    public T destination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge<?> other = (Edge<?>) o;
        return weight == other.weight &&
                Objects.equals(label, other.label) &&
                Objects.equals(destination, other.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, weight, destination);
    }
}
