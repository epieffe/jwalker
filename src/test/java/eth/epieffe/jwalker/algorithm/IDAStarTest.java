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

public class IDAStarTest {

    @Test
    public void test8Puzzle() {
        Graph<NPuzzle> graph = NPuzzleGraph.INSTANCE;
        Visit<NPuzzle> visit = new IDAStar3<>(graph, NPuzzleHeuristics::manhattanSum);
        //NPuzzle start = NPuzzle.newInstance(8, 7, 4, 1, 6, 3, 2, 5, 0);
        NPuzzle start = NPuzzle.newInstance(8, 12, 10,  7, 3, 14,  6, 13, 4,  9,  5,  2, 1, 15, 11,  0);
        long time = System.currentTimeMillis();
        List<Edge<NPuzzle>> path = visit.run(start);
        time = System.currentTimeMillis() - time;
        System.out.println("TIME: " + time);
        System.out.println("LENGTH: " + path.size());
        assertValidPath(graph, start, path);
    }
}
