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

/**
 * Methods that implement consistent heuristics for {@link NQueens}.
 */
public class NQueensHeuristics {

    private NQueensHeuristics() {}

    /**
     * Returns the number of threats in the provided {@link NQueens} instance.
     *
     * @param nQueens an instance of {@link NQueens}
     * @return the number of threats in the provided instance
     */
    public static int numThreats(NQueens nQueens) {
        int threats = 0;
        for (int col = 0; col < nQueens.size(); ++col) {
            int row = nQueens.row(col);
            for (int i = col + 1; i < nQueens.size(); ++i) {
                int row2 = nQueens.row(i);
                int dist = i - col;
                if (row2 == row || row2 == row - dist || row2 == row + dist) {
                    ++threats;
                }
            }
        }
        return threats;
    }
}
