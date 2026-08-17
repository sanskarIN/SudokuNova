package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SudokuGeneratorTest {
    private val solver = SudokuSolver()

    @Test
    fun seededGenerationIsDeterministicAndUnique() {
        val generator = SudokuGenerator(solver)
        val first = generator.generate(Difficulty.EASY, seed = 20260817L)
        val second = generator.generate(Difficulty.EASY, seed = 20260817L)

        assertEquals(first.puzzle, second.puzzle)
        assertEquals(first.solution, second.solution)
        assertTrue(first.puzzle.isValid())
        assertTrue(solver.hasUniqueSolution(first.puzzle))
        assertTrue(first.solution.isComplete)
    }
}
