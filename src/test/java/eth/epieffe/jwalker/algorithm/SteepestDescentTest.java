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
package eth.epieffe.jwalker.algorithm;

import eth.epieffe.jwalker.nqueens.NQueens;
import eth.epieffe.jwalker.nqueens.NQueensGraph;
import eth.epieffe.jwalker.nqueens.NQueensHeuristics;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SteepestDescentTest {

    private final SteepestDescent<NQueens> search = new SteepestDescent<>(
            NQueensGraph.INSTANCE,
            null,
            NQueensHeuristics::numThreats,
            500);

    @ParameterizedTest
    @MethodSource("nQueens8Provider")
    public void test8QueensWithNThreatsHeuristic(NQueens start) {
        NQueens sol = search.run(start);
        int numThreats = NQueensHeuristics.numThreats(sol);
        // Retry up to 10 times
        for (int retry = 0; numThreats > 0 && retry < 10; ++retry) {
            numThreats = NQueensHeuristics.numThreats(sol);
        }
        assertEquals(0, numThreats);
    }

    @ParameterizedTest
    @MethodSource("nQueens50Provider")
    public void test50QueensWithNThreatsHeuristic(NQueens start) {
        NQueens sol = search.run(start);
        int numThreats = NQueensHeuristics.numThreats(sol);
        // Retry up to 20 times
        for (int retry = 0; numThreats > 0 && retry < 20; ++retry) {
            numThreats = NQueensHeuristics.numThreats(sol);
        }
        assertEquals(0, numThreats);
    }

    static Stream<NQueens> nQueens8Provider() {
        return Stream.of(
            NQueens.newInstance(2, 3, 6, 5, 3, 3, 4, 2),
            NQueens.newInstance(3, 3, 5, 1, 4, 7, 4, 0),
            NQueens.newInstance(6, 6, 1, 0, 3, 5, 5, 6),
            NQueens.newInstance(5, 5, 1, 0, 3, 0, 2, 0),
            NQueens.newInstance(5, 3, 2, 5, 5, 6, 3, 4),
            NQueens.newInstance(1, 6, 0, 0, 4, 5, 3, 0),
            NQueens.newInstance(3, 2, 3, 2, 0, 1, 5, 1),
            NQueens.newInstance(2, 0, 4, 6, 6, 1, 7, 7),
            NQueens.newInstance(1, 5, 3, 4, 2, 0, 6, 3),
            NQueens.newInstance(6, 0, 2, 5, 5, 4, 3, 0)
        );
    }

    static Stream<NQueens> nQueens50Provider() {
        return Stream.of(
            NQueens.newInstance(3, 44, 25, 34, 30, 42, 41, 21, 1, 4, 9, 7, 36, 43, 32, 8, 6, 22, 27, 23, 42, 7, 35, 38, 36, 6, 43, 42, 9, 17, 44, 0, 37, 28, 33, 28, 31, 32, 37, 45, 36, 23, 27, 28, 44, 15, 9, 21, 42, 35),
            NQueens.newInstance(44, 17, 18, 34, 1, 44, 18, 33, 6, 41, 27, 7, 48, 5, 45, 36, 30, 39, 42, 1, 25, 22, 17, 37, 22, 24, 33, 34, 45, 18, 28, 2, 15, 0, 45, 38, 12, 0, 47, 35, 34, 35, 6, 21, 37, 12, 44, 27, 9, 5),
            NQueens.newInstance(14, 6, 8, 16, 44, 16, 27, 17, 15, 19, 9, 49, 42, 33, 38, 10, 32, 19, 14, 33, 46, 41, 4, 30, 41, 6, 26, 47, 46, 8, 37, 2, 47, 19, 8, 49, 47, 22, 4, 6, 34, 35, 25, 1, 21, 43, 43, 11, 27, 30),
            NQueens.newInstance(21, 31, 41, 22, 42, 18, 28, 40, 0, 17, 37, 44, 17, 28, 16, 23, 19, 11, 40, 42, 4, 1, 32, 21, 16, 2, 5, 45, 47, 11, 34, 46, 17, 4, 30, 38, 42, 35, 12, 28, 30, 32, 28, 38, 29, 22, 42, 33, 25, 18),
            NQueens.newInstance(1, 49, 10, 22, 43, 31, 21, 32, 13, 29, 33, 9, 34, 23, 49, 14, 12, 37, 10, 9, 7, 44, 34, 34, 15, 29, 7, 14, 34, 29, 20, 38, 15, 21, 35, 14, 49, 10, 46, 4, 37, 15, 32, 31, 14, 16, 23, 18, 19, 39)
        );
    }
}
