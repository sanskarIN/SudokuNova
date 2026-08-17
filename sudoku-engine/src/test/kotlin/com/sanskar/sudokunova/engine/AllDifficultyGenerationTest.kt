package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllDifficultyGenerationTest {
    private val solver = SudokuSolver()
    private val generator = SudokuGenerator(solver)

    @Test
    fun everyDifficultyGeneratesValidUniqueDeterministicPuzzle() {
        Difficulty.entries.forEach { difficulty ->
            val seed = 20_260_817L + difficulty.ordinal * 10_000L
            val first = generator.generate(difficulty, seed)
            val second = generator.generate(difficulty, seed)

            assertEquals(difficulty, first.difficulty)
            assertEquals(first.puzzle, second.puzzle)
            assertEquals(first.solution, second.solution)
            assertTrue(first.puzzle.isValid(), "${difficulty.name} puzzle must be valid")
            assertTrue(first.solution.isComplete, "${difficulty.name} solution must be complete")
            assertTrue(solver.hasUniqueSolution(first.puzzle), "${difficulty.name} puzzle must be unique")
        }
    }
}
