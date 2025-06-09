package eth.epieffe.jwalker.maze;

import java.util.Objects;

/**
 * A cell in a {@link MazeGraph}.
 */
public class Cell {

    public final int row;

    public final int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row of this cell.
     *
     * @return the row of this cell
     */
    public int row() {
        return row;
    }

    /**
     * Returns the column of this cell.
     *
     * @return the column of this cell
     */
    public int col() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell other = (Cell) o;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return String.format("(row: %d, col: %d)", row, col);
    }

}
