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
