package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SudokuSolverTest {
    private val solver = SudokuSolver()

    @Test
    fun solvesKnownPuzzle() {
        val board = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )

        val result = solver.solve(board)

        assertNotNull(result.solution)
        assertTrue(result.solution.isComplete)
        assertEquals(4, result.solution.valueAt(2))
    }

    @Test
    fun detectsInvalidPuzzle() {
        val invalid = SudokuBoard.empty()
            .withValue(0, 0, 4)
            .withValue(0, 1, 4)

        val result = solver.analyze(invalid)

        assertEquals(0, result.solutionCount)
        assertFalse(result.isSolvable)
    }

    @Test
    fun confirmsUniqueKnownPuzzle() {
        val board = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )

        assertTrue(solver.hasUniqueSolution(board))
    }
}
