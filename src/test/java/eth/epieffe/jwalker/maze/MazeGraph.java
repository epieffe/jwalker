package eth.epieffe.jwalker.maze;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;

import java.util.ArrayList;
import java.util.List;

public class MazeGraph implements Graph<Cell> {

    private final byte[][] grid;
    private final int targetRow;
    private final int targetCol;

    public static MazeGraph newInstance(int[][] grid, int targetRow, int targetCol) {
        if (grid.length == 0) {
            throw new IllegalArgumentException("Empty grid");
        }
        int width = grid.length;
        int height = grid[0].length;
        if (targetRow < 0 || targetCol < 0 || targetRow >= height || targetCol >= width) {
            throw new IllegalArgumentException("Target cell out of grid range");
        }
        if (grid[targetRow][targetCol] <= 0) {
            throw new IllegalArgumentException("Target cell is blocked");
        }

        byte[][] newGrid = new byte[width][height];
        for (int i = 0; i < width; ++i) {
            if (grid[i].length != height) {
                throw new IllegalArgumentException("Invalid grid");
            }
            for (int j = 0; j < height; ++j) {
                int cell = grid[i][j];
                if (cell > Byte.MAX_VALUE) {
                    throw new IllegalArgumentException("Cell value too high: " + cell);
                }
                newGrid[i][j] = (byte)(Math.max(cell, 0));
            }
        }
        return new MazeGraph(newGrid, targetRow, targetCol);
    }

    private MazeGraph(byte[][] grid, int targetRow, int targetCol) {
        this.grid = grid;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    @Override
    public List<Edge<Cell>> outgoingEdges(Cell cell) {
        List<Edge<Cell>> edges = new ArrayList<>(8);
        int width = grid.length;
        int height = grid[0].length;

        int[][] directions = {
                {0, -1}, {0, 1}, {-1, 0}, {1, 0},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        String[] moveNames = {"LEFT", "RIGHT", "UP", "DOWN", "UP-LEFT", "UP-RIGHT", "DOWN-LEFT", "DOWN-RIGHT"};

        for (int i = 0; i < directions.length; i++) {
            int newRow = cell.row + directions[i][0];
            int newCol = cell.col + directions[i][1];

            if (newRow >= 0 && newRow < width && newCol >= 0 && newCol < height) {
                byte cost = grid[newRow][newCol];
                if (cost > 0) {
                    Cell newCell = new Cell(newRow, newCol);
                    edges.add(new Edge<>(moveNames[i], cost, newCell));
                }
            }
        }

        return edges;
    }


    @Override
    public boolean isTarget(Cell cell) {
        return cell.row == this.targetRow &&
                cell.col == this.targetCol;
    }

    public int cell(int row, int col) {
        return grid[row][col];
    }

    public int width() {
        return this.grid[0].length;
    }

    public int height() {
        return this.grid.length;
    }
}
