package eth.epieffe.jwalker.npuzzle;

/**
 * Methods that implement consistent heuristics for {@link NPuzzle}.
 */
public class NPuzzleHeuristics {

    private NPuzzleHeuristics() {}

    /**
     * Returns the sum of the Manhattan distances from each cell of the
     * provided {@link NPuzzle} instance to its target position.
     *
     * @param nPuzzle an instance of {@link NPuzzle}
     * @return the sum of the Manhattan distances from each cell to its target
     * position
     * @throws NullPointerException if nPuzzle is {@code null}
     */
    public static int manhattanSum(NPuzzle nPuzzle) {
        int sum = 0;
        for (byte row = 0; row < nPuzzle.size; row++) {
            for (byte col = 0; col < nPuzzle.size; col++) {
                int cell = nPuzzle.cell(row, col);
                if (cell == NPuzzle.EMPTY_CELL) {
                    cell = nPuzzle.size * nPuzzle.size;
                }
                int targetCol = cell;
                int targetRow = 0;
                while (targetCol > nPuzzle.size) {
                    targetCol -= nPuzzle.size;
                    targetRow++;
                }
                targetCol--;
                sum += Math.abs(col - targetCol) + Math.abs(row - targetRow);
            }
        }
        return sum;
    }

    /**
     * Returns the number of cells in the provided {@link NPuzzle} instance
     * that are out of place.
     *
     * @param nPuzzle an instance of {@link NPuzzle}
     * @return the number of out of place cells in nPuzzle
     * @throws NullPointerException if nPuzzle is {@code null}
     */
    public static int outOfPlace(NPuzzle nPuzzle) {
        int count = 0;
        for (byte row = 0; row < nPuzzle.size; row++) {
            for (byte col = 0; col < nPuzzle.size; col++) {
                if (row == nPuzzle.size - 1 && col == nPuzzle.size - 1) {
                    if (nPuzzle.cell(row, col) != NPuzzle.EMPTY_CELL) {
                        count++;
                    }
                } else if (nPuzzle.cell(row, col) != nPuzzle.size * row + col + 1) {
                    count++;
                }
            }
        }
        return count;
    }
}
