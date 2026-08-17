package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TeachingHintSequenceTest {
    private val solver = SudokuSolver()
    private val teachingEngine = LogicalTeachingEngine()
    private val hintEngine = HintEngine(solver, teachingEngine)

    @Test
    fun sequenceCanCarryPrerequisiteEliminationsBeforePlacement() {
        val elimination = TeachingStep(
            technique = LogicalTechnique.NAKED_PAIR,
            sourceCells = setOf(0, 1),
            sourceUnit = LogicalUnit(LogicalUnitType.ROW, 0),
            affectedUnit = LogicalUnit(LogicalUnitType.ROW, 0),
            candidateValues = setOf(1, 2),
            eliminations = listOf(CandidateElimination(2, 1)),
        )
        val placement = TeachingStep(
            technique = LogicalTechnique.NAKED_SINGLE,
            sourceCells = setOf(2),
            candidateValues = setOf(3),
            placement = TeachingPlacement(2, 3),
        )

        val sequence = TeachingHintSequence(listOf(elimination, placement))

        assertEquals(listOf(elimination), sequence.prerequisiteEliminations)
        assertEquals(TeachingPlacement(2, 3), sequence.placement)
        assertEquals(
            listOf(LogicalTechnique.NAKED_PAIR, LogicalTechnique.NAKED_SINGLE),
            sequence.techniques,
        )
    }

    @Test
    fun sequenceRejectsMissingOrEarlyPlacements() {
        val elimination = TeachingStep(
            technique = LogicalTechnique.NAKED_PAIR,
            sourceCells = setOf(0, 1),
            candidateValues = setOf(1, 2),
            eliminations = listOf(CandidateElimination(2, 1)),
        )
        val placement = TeachingStep(
            technique = LogicalTechnique.NAKED_SINGLE,
            sourceCells = setOf(2),
            candidateValues = setOf(3),
            placement = TeachingPlacement(2, 3),
        )

        assertFailsWith<IllegalArgumentException> { TeachingHintSequence(emptyList()) }
        assertFailsWith<IllegalArgumentException> { TeachingHintSequence(listOf(elimination)) }
        assertFailsWith<IllegalArgumentException> {
            TeachingHintSequence(listOf(placement, placement))
        }
    }

    @Test
    fun nextPlacementSequenceIsExactlyTracePrefixThroughFirstPlacement() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val trace = teachingEngine.trace(puzzle, maxSteps = 64)
        val firstPlacementIndex = trace.steps.indexOfFirst(TeachingStep::isPlacement)
        val sequence = assertNotNull(teachingEngine.nextPlacementSequence(puzzle))

        assertTrue(firstPlacementIndex >= 0)
        assertEquals(trace.steps.take(firstPlacementIndex + 1), sequence.steps)
        assertEquals(sequence, hintEngine.nextTeachingHint(puzzle))
    }

    @Test
    fun generatedTeachingHintSequencesNeverContradictUniqueSolutions() {
        val generated = listOf(
            SudokuGenerator(solver).generate(Difficulty.MEDIUM, seed = 21_008L),
            SudokuGenerator(solver).generate(Difficulty.HARD, seed = 73_001L),
            SudokuGenerator(solver).generate(Difficulty.EXPERT, seed = 88_008L),
        )

        generated.forEach { puzzle ->
            teachingEngine.nextPlacementSequence(puzzle.puzzle)?.let { sequence ->
                sequence.steps.forEach { step ->
                    step.placement?.let { placement ->
                        assertEquals(puzzle.solution.valueAt(placement.cellIndex), placement.value)
                    }
                    step.eliminations.forEach { elimination ->
                        assertNotEquals(puzzle.solution.valueAt(elimination.cellIndex), elimination.value)
                    }
                }
            }
        }
    }

    @Test
    fun completedBoardHasNoPlacementSequence() {
        val solved = SudokuBoard.parse(
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
        )

        assertEquals(null, teachingEngine.nextPlacementSequence(solved))
        assertEquals(null, hintEngine.nextTeachingHint(solved))
    }
}
