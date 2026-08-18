package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SudokuHintTest {
    @Test
    fun multiStepHintReportsHardestTechniqueAndKeepsFinalPlacement() {
        val elimination = TeachingStep(
            technique = LogicalTechnique.NAKED_PAIR,
            sourceCells = listOf(0, 1),
            sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 0),
            targetCells = listOf(2),
            candidateEliminations = listOf(CandidateElimination(2, 4)),
        )
        val placement = TeachingStep(
            technique = LogicalTechnique.NAKED_SINGLE,
            sourceCells = listOf(2),
            sourceUnit = null,
            targetCells = listOf(2),
            placement = TeachingPlacement(2, 7),
        )

        val hint = SudokuHint(teachingSteps = listOf(elimination, placement))

        assertEquals(HintTechnique.NAKED_PAIR, hint.technique)
        assertEquals(2, hint.cellIndex)
        assertEquals(7, hint.value)
        assertTrue(hint.usesAdvancedElimination)
    }

    @Test
    fun directPlacementReportsItsOwnTechnique() {
        val hint = SudokuHint(
            teachingSteps = listOf(
                TeachingStep(
                    technique = LogicalTechnique.HIDDEN_SINGLE,
                    sourceCells = rowIndices(3),
                    sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 3),
                    targetCells = listOf(29),
                    placement = TeachingPlacement(29, 8),
                ),
            ),
        )

        assertEquals(HintTechnique.HIDDEN_SINGLE, hint.technique)
        assertEquals(29, hint.cellIndex)
        assertEquals(8, hint.value)
    }

    @Test
    fun revealRemainsExplicitlySeparateFromTeachingEvidence() {
        val hint = SudokuHint(
            teachingSteps = emptyList(),
            revealPlacement = TeachingPlacement(40, 5),
        )

        assertEquals(HintTechnique.REVEAL, hint.technique)
        assertEquals(40, hint.cellIndex)
        assertEquals(5, hint.value)
    }
}
