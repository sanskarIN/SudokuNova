package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LogicalTeachingEngineTest {
    private val teachingEngine = LogicalTeachingEngine()
    private val logicalSolver = LogicalSolver()
    private val solver = SudokuSolver()

    @Test
    fun teachingTraceMatchesExistingLogicalSolverBehavior() {
        val boards = listOf(
            SudokuBoard.parse(
                "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
            ),
            SudokuGenerator(solver).generate(Difficulty.HARD, seed = 73_001L).puzzle,
            SudokuGenerator(solver).generate(Difficulty.EXPERT, seed = 88_008L).puzzle,
        )

        boards.forEach { board ->
            val oldResult = logicalSolver.solve(board)
            val trace = teachingEngine.trace(board)

            assertEquals(oldResult.board, trace.finalBoard)
            assertEquals(oldResult.candidateEliminations, trace.candidateEliminationCount)
            assertEquals(oldResult.placements, trace.placementCount)
            assertEquals(oldResult.unresolvedCells, trace.unresolvedCells)
        }
    }

    @Test
    fun everyTeachingDeductionIsSafeAgainstUniqueSolution() {
        val generatedBoards = listOf(
            SudokuGenerator(solver).generate(Difficulty.MEDIUM, seed = 21_008L),
            SudokuGenerator(solver).generate(Difficulty.HARD, seed = 73_001L),
            SudokuGenerator(solver).generate(Difficulty.EXPERT, seed = 88_008L),
        )

        generatedBoards.forEach { generated ->
            val trace = teachingEngine.trace(generated.puzzle)

            trace.steps.forEach { step ->
                step.placement?.let { placement ->
                    assertEquals(
                        generated.solution.valueAt(placement.cellIndex),
                        placement.value,
                        "${step.technique} placed a value that disagrees with the unique solution.",
                    )
                }
                step.eliminations.forEach { elimination ->
                    assertNotEquals(
                        generated.solution.valueAt(elimination.cellIndex),
                        elimination.value,
                        "${step.technique} eliminated the unique solution value.",
                    )
                }
            }
        }
    }

    @Test
    fun evidenceIsDeterministicAndStructurallyBounded() {
        val puzzle = SudokuGenerator(solver).generate(Difficulty.HARD, seed = 81_017L).puzzle
        val first = teachingEngine.trace(puzzle)
        val second = teachingEngine.trace(puzzle)

        assertEquals(first, second)
        first.steps.forEach { step ->
            assertTrue(step.sourceCells.all { it in 0 until SudokuBoard.CELL_COUNT })
            assertTrue(step.affectedCells.all { it in 0 until SudokuBoard.CELL_COUNT })
            assertTrue(step.candidateValues.all { it in 1..9 })
            assertTrue(step.placement != null || step.eliminations.isNotEmpty())
            assertTrue(step.placement == null || step.eliminations.isEmpty())
        }
    }

    @Test
    fun nextStepIsExactlyTheFirstTraceStep() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val trace = teachingEngine.trace(puzzle)
        val first = assertNotNull(trace.steps.firstOrNull())

        assertEquals(first, teachingEngine.nextStep(puzzle))
        assertEquals(first, teachingEngine.trace(puzzle, maxSteps = 1).steps.single())
    }

    @Test
    fun invalidAndCompleteBoardsDoNotProduceInventedTeachingSteps() {
        val solved = SudokuBoard.parse(
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
        )

        assertTrue(teachingEngine.trace(solved).steps.isEmpty())
        assertEquals(null, teachingEngine.nextStep(solved))
    }
}
