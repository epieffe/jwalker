package eth.epieffe.jwalker.npuzzle;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link Graph} of {@link NPuzzle} instances.
 */
public class NPuzzleGraph implements Graph<NPuzzle> {

    /**
     * The singleton instance
     */
    public static final NPuzzleGraph INSTANCE = new NPuzzleGraph();

    // No need to create more than one instance
    private NPuzzleGraph() {}

    /**
     * Returns the edges that bring from the provided {@link NPuzzle}
     * instance to its neighbours.
     *
     * @param node an instance of {@link NPuzzle}
     * @return the edges that bring from node to its neighbours
     * @throws NullPointerException if node is {@code null}
     */
    @Override
    public List<Edge<NPuzzle>> outgoingEdges(NPuzzle node) {
        List<Edge<NPuzzle>> edgeList = new ArrayList<>(4);
        // move up
        if (node.emptyRow() > 0) {
            int newEmptyIndex = (node.emptyRow() - 1) * node.size + node.emptyCol();
            edgeList.add(new Edge<>("UP", 1, swapEmptyCell(node, newEmptyIndex)));
        }
        // move down
        if (node.emptyRow() < node.size - 1) {
            int newEmptyIndex = (node.emptyRow() + 1) * node.size + node.emptyCol();
            edgeList.add(new Edge<>("DOWN", 1, swapEmptyCell(node, newEmptyIndex)));
        }
        // move left
        if (node.emptyCol() > 0) {
            int newEmptyIndex = node.emptyRow() * node.size + (node.emptyCol() - 1);
            edgeList.add(new Edge<>("LEFT", 1, swapEmptyCell(node, newEmptyIndex)));
        }
        // move right
        if (node.emptyCol() < node.size - 1) {
            int newEmptyIndex = node.emptyRow() * node.size + (node.emptyCol() + 1);
            edgeList.add(new Edge<>("RIGHT", 1, swapEmptyCell(node, newEmptyIndex)));
        }

        return edgeList;
    }

    /**
     * Returns {@code true} if the provided {@link NPuzzle}
     * instance is a target.
     *
     * @param node an instance of {@link NPuzzle}
     * @return {@code true} if node is a target
     * @throws NullPointerException if node is {@code null}
     */
    @Override
    public boolean isTarget(NPuzzle node) {
        // The last cell must be empty
        if (node.table[node.table.length - 1] > 0) {
            return false;
        }
        // The other cells must be ordered from 1 to n-1
        for (int i = 1; i < node.table.length; ++i) {
            if (node.table[i - 1] != i) {
                return false;
            }
        }
        return true;
    }

    private static NPuzzle swapEmptyCell(NPuzzle nPuzzle, int newEmptyIndex) {
        byte[] newTable = nPuzzle.table.clone();
        newTable[nPuzzle.emptyIndex] = nPuzzle.table[newEmptyIndex];
        newTable[newEmptyIndex] = NPuzzle.EMPTY_CELL;
        return new NPuzzle(nPuzzle.size, (byte) newEmptyIndex, newTable);
    }
}
