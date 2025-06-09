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
package eth.epieffe.jwalker.nqueens;

import eth.epieffe.jwalker.Edge;
import eth.epieffe.jwalker.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link Graph} of {@link NQueens} instances.
 */
public class NQueensGraph implements Graph<NQueens> {

    /**
     * The singleton instance
     */
    public static final NQueensGraph INSTANCE = new NQueensGraph();

    // No need to create more than one instance
    private NQueensGraph() {}

    /**
     * Returns the edges that bring from the provided {@link NQueens} instance
     * to its neighbours.<p>
     *
     * Each returned edge represents a move of one queen through its column in
     * the current instance.
     *
     * @param node an instance of {@link NQueens}
     * @return the edges that bring from node to its neighbours
     * @throws NullPointerException if node is {@code null}
     */
    @Override
    public List<Edge<NQueens>> outgoingEdges(NQueens node) {
        int length = node.size();
        int nMoves = (length - 1) * length;
        List<Edge<NQueens>> edgeList = new ArrayList<>(nMoves);
        for (int i = 0; i < length; i++) {
            for (int v = 0; v < length; v++) {
                if (v != node.row(i)) {
                    int[] newPosArray = new int[length];
                    for (int j = 0; j < length; j++) {
                        if (j == i) {
                            newPosArray[j] = v;
                        } else {
                            newPosArray[j] = node.row(j);
                        }
                    }
                    NQueens newConfig = new NQueens(newPosArray);
                    Edge<NQueens> edge = new Edge<>(null, 1, newConfig);
                    edgeList.add(edge);
                }
            }
        }
        return edgeList;
    }

    /**
     * Returns {@code true} if none of the queens in the provided {@link NQueens}
     * instance is threatened by another queen.
     *
     * @param node an instance of {@link NQueens}
     * @return {@code true} if none of the queens in the provided instance is threatened
     */
    @Override
    public boolean isTarget(NQueens node) {
        for (int col = 0; col < node.size(); col++) {
            int colVal = node.row(col);
            for (int i = col + 1; i < node.size(); i++) {
                int val = node.row(i);
                int dist = i - col;
                if (val == colVal || val == colVal - dist || val == colVal + dist) {
                    return false;
                }
            }
        }
        return true;
    }
}
