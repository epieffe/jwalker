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
import eth.epieffe.jwalker.Visit;
import eth.epieffe.jwalker.npuzzle.NPuzzle;
import eth.epieffe.jwalker.npuzzle.NPuzzleGraph;
import eth.epieffe.jwalker.npuzzle.NPuzzleHeuristics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eth.epieffe.jwalker.algorithm.PathAssertions.assertValidPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinCostTest {

    @Test
    public void test8PuzzleWithManhattanHeuristic() {
        Graph<NPuzzle> graph = NPuzzleGraph.INSTANCE;
        Visit<NPuzzle> visit = new MinCost<>(graph, NPuzzleHeuristics::manhattanSum);
        NPuzzle start = NPuzzle.newInstance(8, 7, 4, 1, 6, 3, 2, 5, 0);
        List<Edge<NPuzzle>> path = visit.run(start);
        assertTrue(path.get(path.size() - 1).destination.isSolved());
        assertValidPath(graph, start, path);
        assertEquals(22, path.size());
    }

    @Test
    public void test8PuzzleWithOutOfPlaceHeuristic() {
        Graph<NPuzzle> graph = NPuzzleGraph.INSTANCE;
        Visit<NPuzzle> visit = new MinCost<>(graph, NPuzzleHeuristics::outOfPlace);
        NPuzzle start = NPuzzle.newInstance(5, 3, 7, 4, 0, 6, 1, 2, 8);
        List<Edge<NPuzzle>> path = visit.run(start);
        assertTrue(path.get(path.size() - 1).destination.isSolved());
        assertValidPath(graph, start, path);
        assertEquals(22, path.size());
    }

    @Test
    public void test8PuzzleWithDijkstra() {
        Graph<NPuzzle> graph = NPuzzleGraph.INSTANCE;
        Visit<NPuzzle> visit = new MinCost<>(graph, NPuzzle::isSolved);
        NPuzzle start = NPuzzle.newInstance(7, 1, 2, 4, 8, 3, 5, 0, 6);
        List<Edge<NPuzzle>> path = visit.run(start);
        assertTrue(path.get(path.size() - 1).destination.isSolved());
        assertValidPath(graph, start, path);
        assertEquals(13, path.size());
    }

    @Test
    public void test15PuzzleWithHMulAndManhattanHeuristic() {
        Graph<NPuzzle> graph = NPuzzleGraph.INSTANCE;
        Visit<NPuzzle> visit = new MinCost<>(graph, NPuzzleHeuristics::manhattanSum, null, 2);
        NPuzzle start = NPuzzle.newInstance(8, 12, 10,  7, 3, 14,  6, 13, 4,  9,  5,  2, 1, 15, 11,  0);
        List<Edge<NPuzzle>> path = visit.run(start);
        assertTrue(path.get(path.size() - 1).destination.isSolved());
        assertValidPath(graph, start, path);
        assertEquals(74, path.size());
    }
}
