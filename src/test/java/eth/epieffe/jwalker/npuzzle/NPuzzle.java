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
package eth.epieffe.jwalker.npuzzle;

import java.util.Arrays;

/**
 * An instance of the <i>N-Puzzle</i> game.<p>
 *
 * <i>N-Puzzle</i> is a sliding puzzle consisting of a grid of numbered tiles with one
 * empty space. The goal is to order the tiles by sliding them into the empty space.
 */
public class NPuzzle {

    public static final byte EMPTY_CELL = 0;

    final byte size;
    final byte emptyIndex;
    final byte[] table;

    /**
     * Creates a new {@code NPuzzle} instance from an array of numbers.<p>
     *
     * Any number less than or equal to zero  represents the empty cell.
     *
     * @param numbers an array of numbers
     * @return a new {@code NPuzzle} instance
     * @throws NullPointerException if numbers is null
     * @throws IllegalArgumentException if numbers do not represent a valid N-Puzzle instance
     */
    public static NPuzzle newInstance(int... numbers) {
        if (numbers.length > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("Size is too large");
        }
        int length = (int) Math.sqrt(numbers.length);
        if (length == 0 || length * length != numbers.length) {
            throw new IllegalArgumentException("Invalid size");
        }
        // Find empty cell index
        int emptyIndex = -1;
        for (int i = 0; i < numbers.length; ++i) {
            if (numbers[i] <= 0) {
                emptyIndex = i;
                break;
            }
        }
        if (emptyIndex == -1) {
            throw new IllegalArgumentException("Missing empty cell");
        }
        // Copy nums in a new byte array
        byte[] table = new byte[numbers.length];
        for (int i = 0; i < numbers.length; ++i) {
            table[i] = (byte) numbers[i];
        }
        table[emptyIndex] = EMPTY_CELL;
        return new NPuzzle((byte) length, (byte) emptyIndex, table);
    }

    // Constructor visibility is package private, it is
    // accessed by NPuzzleGraph
    NPuzzle(byte size, byte emptyIndex, byte[] table) {
        this.size = size;
        this.emptyIndex = emptyIndex;
        this.table = table;
    }

    /**
     * Returns true if this N-Puzzle instance is a solution.
     *
     * @return true if this N-Puzzle instance is a solution
     */
    public boolean isSolved() {
        // The last cell must be empty
        if (table[table.length - 1] > 0) {
            return false;
        }
        // The other cells must be ordered from 1 to n-1
        for (int i = 1; i < table.length; ++i) {
            if (table[i - 1] != i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the size of this instance.
     *
     * @return the size of this instance
     */
    public byte size() {
        return size;
    }

    /**
     * Returns the value of a cell in this instance.<p>
     *
     * The empty cell is represented as zero.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the cell at the provided coordinates
     * @throws ArrayIndexOutOfBoundsException if the provided coordinates are out of range
     */
    public byte cell(int row, int col) {
        return table[row * size + col];
    }

    /**
     * Returns the column of the empty cell.
     *
     * @return the column of the empty cell
     */
    public int emptyCol() {
        return emptyIndex % size;
    }

    /**
     * Returns the row of the empty cell.
     *
     * @return the row of the empty cell
     */
    public int emptyRow() {
        return emptyIndex / size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NPuzzle)) return false;
        NPuzzle other = (NPuzzle) o;
        return Arrays.equals(this.table, other.table);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(table);
    }

    @Override
    public String toString() {
        return Arrays.toString(table);
    }
}
