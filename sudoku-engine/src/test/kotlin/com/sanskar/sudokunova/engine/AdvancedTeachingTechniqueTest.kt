package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdvancedTeachingTechniqueTest {
    private val finder = TeachingStepFinder()
    private val board = SudokuBoard.empty()
    private val all = (1..9).toSet()

    @Test
    fun hiddenPairProducesOnlyExtraCandidateEliminationsFromItsTwoCells() {
        val overrides = mutableMapOf<Int, Set<Int>>()
        overrides[0] = setOf(1, 2, 3)
        overrides[4] = setOf(1, 2, 4)
        (1..8).filter { it != 4 }.forEach { overrides[it] = all - setOf(1, 2) }

        val step = assertNotNull(finder.nextStepForCandidates(board, overrides))

        assertEquals(LogicalTechnique.HIDDEN_PAIR, step.technique)
        assertEquals(listOf(0, 4), step.sourceCells)
        assertEquals(
            setOf(CandidateElimination(0, 3), CandidateElimination(4, 4)),
            step.candidateEliminations.toSet(),
        )
    }

    @Test
    fun nakedTripleEliminatesItsThreeDigitsFromOtherCellsInTheUnit() {
        val overrides = mapOf(
            0 to setOf(1, 2),
            4 to setOf(1, 3),
            8 to setOf(2, 3),
        )

        val step = assertNotNull(finder.nextStepForCandidates(board, overrides))

        assertEquals(LogicalTechnique.NAKED_TRIPLE, step.technique)
        assertEquals(setOf(0, 4, 8), step.sourceCells.toSet())
        assertTrue(step.candidateEliminations.isNotEmpty())
        assertTrue(step.candidateEliminations.all { it.candidate in setOf(1, 2, 3) })
        assertTrue(step.candidateEliminations.none { it.cellIndex in step.sourceCells })
    }

    @Test
    fun hiddenTripleKeepsThreeRestrictedDigitsAndRemovesOtherCandidates() {
        val overrides = mutableMapOf<Int, Set<Int>>()
        overrides[0] = setOf(1, 2, 4)
        overrides[4] = setOf(1, 3, 5)
        overrides[8] = setOf(2, 3, 6)
        (1..7).filter { it != 4 }.forEach { overrides[it] = all - setOf(1, 2, 3) }

        val step = assertNotNull(finder.nextStepForCandidates(board, overrides))

        assertEquals(LogicalTechnique.HIDDEN_TRIPLE, step.technique)
        assertEquals(setOf(0, 4, 8), step.sourceCells.toSet())
        assertEquals(
            setOf(
                CandidateElimination(0, 4),
                CandidateElimination(4, 5),
                CandidateElimination(8, 6),
            ),
            step.candidateEliminations.toSet(),
        )
    }

    @Test
    fun rowBasedXWingEliminatesCandidateOnlyOutsideItsTwoSourceRows() {
        val overrides = mutableMapOf<Int, Set<Int>>()
        val withoutFive = all - 5
        listOf(0, 3).forEach { row ->
            (0 until SudokuBoard.SIZE).forEach { column ->
                val index = row * SudokuBoard.SIZE + column
                overrides[index] = if (column == 1 || column == 7) all else withoutFive
            }
        }

        val step = assertNotNull(finder.nextStepForCandidates(board, overrides))

        assertEquals(LogicalTechnique.X_WING, step.technique)
        assertEquals(setOf(1, 7, 28, 34), step.sourceCells.toSet())
        assertTrue(step.candidateEliminations.isNotEmpty())
        assertTrue(step.candidateEliminations.all { it.candidate == 5 })
        assertTrue(step.candidateEliminations.all { it.cellIndex / SudokuBoard.SIZE !in setOf(0, 3) })
        assertTrue(step.candidateEliminations.all { it.cellIndex % SudokuBoard.SIZE in setOf(1, 7) })
    }

    @Test
    fun candidateProbeRejectsImpossibleOverrides() {
        val solvedRow = SudokuBoard.parse(
            "123456789000000000000000000000000000000000000000000000000000000000000000000000000",
        )
        val error = runCatching {
            finder.nextStepForCandidates(solvedRow, mapOf(9 to setOf(1)))
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
    }
}
