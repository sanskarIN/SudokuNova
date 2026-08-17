package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogicalSolverTest {
    private val logicalSolver = LogicalSolver()
    private val solver = SudokuSolver()

    @Test
    fun commonPuzzleSolvesWithoutGuessing() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val expected = solver.solve(puzzle).solution
        val result = logicalSolver.solve(puzzle)

        assertTrue(result.solved)
        assertEquals(expected, result.board)
        assertTrue(result.placements > 0)
    }

    @Test
    fun emptyBoardStopsWithoutInventingValues() {
        val result = logicalSolver.solve(SudokuBoard.empty())

        assertEquals(SudokuBoard.empty(), result.board)
        assertEquals(81, result.unresolvedCells)
        assertEquals(0, result.placements)
        assertEquals(0, result.candidateEliminations)
    }

    @Test
    fun generatedHardPuzzleNeverPlacesValueThatDisagreesWithUniqueSolution() {
        val generated = SudokuGenerator(solver).generate(Difficulty.HARD, seed = 73_001L)
        val result = logicalSolver.solve(generated.puzzle)

        assertTrue(result.board.isValid())
        for (index in 0 until SudokuBoard.CELL_COUNT) {
            val value = result.board.valueAt(index)
            if (value != SudokuBoard.EMPTY) {
                assertEquals(
                    generated.solution.valueAt(index),
                    value,
                    "Logical solver placed an incorrect value at cell $index",
                )
            }
        }
    }

    @Test
    fun analysisIsDeterministic() {
        val puzzle = SudokuGenerator(solver).generate(Difficulty.EXPERT, seed = 88_008L).puzzle

        assertEquals(logicalSolver.solve(puzzle), logicalSolver.solve(puzzle))
    }
}
