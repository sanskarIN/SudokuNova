package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReleaseEngineBudgetTest {
    private val solver = SudokuSolver()

    @Test
    fun knownSolverCorpusStaysWithinDeterministicSearchBudget() {
        val corpus = listOf(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
            "000260701680070090190004500820100040004602900050003028009300074040050036703018000",
            "300000000005009000200504000020000700160000058704310600000890100000067080000005437",
        )

        corpus.forEachIndexed { index, encoded ->
            val result = solver.analyze(SudokuBoard.parse(encoded), solutionLimit = 2)

            assertEquals(1, result.solutionCount, "Corpus puzzle $index must stay uniquely solvable")
            assertTrue(
                result.metrics.visitedNodes <= MAX_SOLVER_NODES,
                "Corpus puzzle $index exceeded the deterministic node budget: ${result.metrics}",
            )
            assertTrue(
                result.metrics.guesses <= MAX_SOLVER_GUESSES,
                "Corpus puzzle $index exceeded the deterministic guess budget: ${result.metrics}",
            )
            assertTrue(
                result.metrics.backtracks <= MAX_SOLVER_BACKTRACKS,
                "Corpus puzzle $index exceeded the deterministic backtrack budget: ${result.metrics}",
            )
            assertTrue(result.metrics.maxDepth <= SudokuBoard.CELL_COUNT)
        }
    }

    @Test
    fun teachingTraceStaysWithinDeterministicStepBudget() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val trace = TeachingStepFinder().trace(puzzle)

        assertTrue(trace.solved)
        assertTrue(
            trace.steps.size <= MAX_TEACHING_STEPS,
            "Teaching trace exceeded release step budget: ${trace.steps.size}",
        )
        assertTrue(trace.steps.all { it.placement != null || it.candidateEliminations.isNotEmpty() })
    }

    @Test
    fun seededExtremeGenerationKeepsFinalSolveWithinSearchBudget() {
        val generated = SudokuGenerator(solver).generate(
            difficulty = Difficulty.EXTREME,
            seed = 909_202_608L,
        )
        val result = solver.analyze(generated.puzzle, solutionLimit = 2)

        assertEquals(1, result.solutionCount)
        assertTrue(generated.puzzle.clueCount in Difficulty.EXTREME.targetClues)
        assertTrue(
            result.metrics.visitedNodes <= MAX_GENERATED_SOLVER_NODES,
            "Generated Extreme puzzle exceeded release node budget: ${result.metrics}",
        )
        assertTrue(result.metrics.maxDepth <= SudokuBoard.CELL_COUNT)
    }

    private companion object {
        const val MAX_SOLVER_NODES = 100_000
        const val MAX_SOLVER_GUESSES = 25_000
        const val MAX_SOLVER_BACKTRACKS = 100_000
        const val MAX_TEACHING_STEPS = 2_000
        const val MAX_GENERATED_SOLVER_NODES = 1_000_000
    }
}
