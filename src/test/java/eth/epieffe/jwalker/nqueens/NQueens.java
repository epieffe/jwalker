package eth.epieffe.jwalker.nqueens;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An instance of the <i>N-Queens</i> problem.<p>
 *
 * The objective of the N-Queens problem is to place <i>N</i> chess queens on
 * an <i>N×N</i> chessboard such that no two queens threaten each other.
 */
public class NQueens {

    private final int[] rows;

    /**
     * Creates a new {@code NQueens} instance from an array that contains
     * a queen row index for each column in the chess board.
     *
     * @param rows an array containing a queen row index for each column in the board
     * @return a new {@code NQueens} instance
     * @throws NullPointerException if rows is null
     * @throws IllegalArgumentException if rows contains any number lower than zero or
     * greater than or equal the board size
     */
    public static NQueens newInstance(int... rows) {
        for (int col = 0; col < rows.length; ++col) {
            if (rows[col] < 0 || rows[col] >= rows.length) {
                throw new IllegalArgumentException("Invalid row " + rows[col] + " at column " + col);
            }
        }
        return new NQueens(rows);
    }

    /**
     * Creates a new random {@code NQueens} instance.
     *
     * @param size the size of the board of the new instance
     * @return a new random {@code NQueens} instance
     */
    public static NQueens newRandomInstance(int size) {
        return newRandomInstance(size, ThreadLocalRandom.current());
    }

    /**
     * Creates a new random {@code NQueens} instance.
     *
     * @param size the size of the board of the new instance
     * @param random a source of randomness
     * @return a new random {@code NQueens} instance
     * @throws NullPointerException if random is null
     */
    public static NQueens newRandomInstance(int size, Random random) {
        int[] pa = new int[size];
        for (int i = 0; i < size; i++) {
            pa[i] = random.nextInt(size);
        }
        return new NQueens(pa);
    }

    // Constructor visibility is package private, it is
    // accessed by NQueensGraph
    NQueens(int[] rows) {
        this.rows = rows;
    }

    /**
     * Returns the queen's row position in the provided column
     *
     * @param col a column index
     * @return the row index of the queen in the provided column
     * @throws ArrayIndexOutOfBoundsException if col is not a valid column in the board
     */
    public int row(int col) {
        return rows[col];
    }

    /**
     * Returns the size of the board of this instance.
     *
     * @return the size of the board of this instance
     */
    public int size() {
        return rows.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(rows);
    }
}
