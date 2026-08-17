package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogicalSolverCorpusTest {
    private val solver = SudokuSolver()
    private val generator = SudokuGenerator(solver)
    private val logicalSolver = LogicalSolver()

    @Test
    fun supportedLogicNeverContradictsSolutionAcrossDifficultyCorpus() {
        Difficulty.entries.forEach { difficulty ->
            val seed = 330_000L + difficulty.ordinal * 997L
            val generated = generator.generate(difficulty, seed)
            val logical = logicalSolver.solve(generated.puzzle)

            assertTrue(logical.board.isValid(), "${difficulty.name} logical board must remain valid")
            for (index in 0 until SudokuBoard.CELL_COUNT) {
                val value = logical.board.valueAt(index)
                if (value != SudokuBoard.EMPTY) {
                    assertEquals(
                        generated.solution.valueAt(index),
                        value,
                        "${difficulty.name} logical result disagreed with the unique solution at $index",
                    )
                }
            }
        }
    }
}
