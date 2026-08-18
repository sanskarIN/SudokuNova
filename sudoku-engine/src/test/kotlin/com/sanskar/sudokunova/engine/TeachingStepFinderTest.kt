package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TeachingStepFinderTest {
    private val solver = SudokuSolver()
    private val finder = TeachingStepFinder()

    @Test
    fun traceIsDeterministicAndSolutionSafe() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val solution = assertNotNull(solver.solve(puzzle).solution)

        val first = finder.trace(puzzle)
        val second = finder.trace(puzzle)

        assertEquals(first, second)
        assertTrue(first.steps.isNotEmpty())
        assertTrue(first.solved)

        first.steps.forEach { step ->
            step.placement?.let { placement ->
                assertEquals(
                    solution.valueAt(placement.cellIndex),
                    placement.value,
                    "Teaching step placed a value that disagrees with the solved board.",
                )
            }
            step.candidateEliminations.forEach { elimination ->
                assertTrue(
                    solution.valueAt(elimination.cellIndex) != elimination.candidate,
                    "Teaching step eliminated the solved value from cell ${elimination.cellIndex}.",
                )
            }
        }
    }

    @Test
    fun generatedCorpusNeverEliminatesOrPlacesTheSolvedValueIncorrectly() {
        val seeds = listOf(11_003L, 23_019L, 41_009L, 73_001L)

        Difficulty.entries.zip(seeds).forEach { (difficulty, seed) ->
            val generated = SudokuGenerator(solver).generate(difficulty, seed)
            val trace = finder.trace(generated.puzzle)

            trace.steps.forEach { step ->
                step.placement?.let { placement ->
                    assertEquals(
                        generated.solution.valueAt(placement.cellIndex),
                        placement.value,
                        "$difficulty produced an unsafe placement at ${placement.cellIndex}",
                    )
                }
                step.candidateEliminations.forEach { elimination ->
                    assertTrue(
                        generated.solution.valueAt(elimination.cellIndex) != elimination.candidate,
                        "$difficulty eliminated its solved value at ${elimination.cellIndex}",
                    )
                }
            }
        }
    }

    @Test
    fun emptyAndInvalidProgressStatesFailClosed() {
        assertEquals(null, finder.nextStep(SudokuBoard.empty()))
        assertTrue(finder.trace(SudokuBoard.empty()).steps.isEmpty())

        val invalid = SudokuBoard.parse(
            "110000000000000000000000000000000000000000000000000000000000000000000000000000000",
        )
        assertEquals(null, finder.nextStep(invalid))
    }
}
