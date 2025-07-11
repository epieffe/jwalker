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
            edgeList.add(new Edge<>(swapEmptyCell(node, newEmptyIndex), 1, "UP"));
        }
        // move down
        if (node.emptyRow() < node.size - 1) {
            int newEmptyIndex = (node.emptyRow() + 1) * node.size + node.emptyCol();
            edgeList.add(new Edge<>(swapEmptyCell(node, newEmptyIndex), 1, "DOWN"));
        }
        // move left
        if (node.emptyCol() > 0) {
            int newEmptyIndex = node.emptyRow() * node.size + (node.emptyCol() - 1);
            edgeList.add(new Edge<>(swapEmptyCell(node, newEmptyIndex), 1, "LEFT"));
        }
        // move right
        if (node.emptyCol() < node.size - 1) {
            int newEmptyIndex = node.emptyRow() * node.size + (node.emptyCol() + 1);
            edgeList.add(new Edge<>(swapEmptyCell(node, newEmptyIndex), 1, "RIGHT"));
        }

        return edgeList;
    }

    private static NPuzzle swapEmptyCell(NPuzzle nPuzzle, int newEmptyIndex) {
        byte[] newTable = nPuzzle.table.clone();
        newTable[nPuzzle.emptyIndex] = nPuzzle.table[newEmptyIndex];
        newTable[newEmptyIndex] = NPuzzle.EMPTY_CELL;
        return new NPuzzle(nPuzzle.size, (byte) newEmptyIndex, newTable);
    }
}
