package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;
import eth.epieffe.jwalker.Visit;
import eth.epieffe.jwalker.maze.Cell;
import eth.epieffe.jwalker.maze.MazeGraph;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static eth.epieffe.jwalker.algorithm.PathAssertions.assertValidPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BFSTest {

    public static class TestCase {
        public final Graph<Cell> graph;
        public final Cell startCell;
        public final double totalCost;

        public TestCase(Graph<Cell> graph, Cell startCell, double totalCost) {
            this.graph = graph;
            this.startCell = startCell;
            this.totalCost = totalCost;
        }
    }

    @ParameterizedTest
    @MethodSource("gridPathfindingProvider")
    public void testGridPathfinding(TestCase test) {
        Visit<Cell> visit = new BFS<>(test.graph);
        List<Edge<Cell>> path = visit.run(test.startCell);
        assertValidPath(test.graph, test.startCell, path);
        assertEquals(test.totalCost, path.size());
    }

    static Stream<TestCase> gridPathfindingProvider() {
        int[][] grid1 = {
                {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
                {1, 0, 1, 0, 0, 1, 1, 0, 1, 1},
                {1, 0, 1, 1, 1, 1, 0, 0, 1, 1},
                {1, 0, 1, 0, 0, 1, 1, 1, 1, 1},
                {1, 0, 1, 0, 1, 1, 1, 0, 0, 1},
                {1, 1, 1, 1, 0, 1, 0, 1, 1, 1},
                {0, 0, 0, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1},
                {1, 1, 1, 0, 0, 0, 0, 0, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        MazeGraph graph1 = MazeGraph.newInstance(grid1, 9, 6);
        Cell start1 = new Cell(4, 2);
        TestCase test1 = new TestCase(graph1, start1, 8);

        return Stream.of(test1);
    }
}
