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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathAssertions {

    public static <T> void assertValidPath(Graph<T> graph, T start, List<Edge<T>> path) {
        assertNotNull(path);

        T current = start;
        for (Edge<T> edge : path) {
            boolean valid = graph.outgoingEdges(current).contains(edge);
            assertTrue(valid, "Invalid move");
            current = edge.destination;
        }
        assertTrue(graph.isTarget(current), "Invalid solution");
    }
}
