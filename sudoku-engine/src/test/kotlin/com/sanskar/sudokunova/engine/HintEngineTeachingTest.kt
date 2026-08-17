package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HintEngineTeachingTest {
    private val solver = SudokuSolver()
    private val teachingEngine = LogicalTeachingEngine()
    private val hintEngine = HintEngine(solver, teachingEngine)

    @Test
    fun teachingStepApiUsesSharedLogicalEngine() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )

        assertEquals(teachingEngine.nextStep(puzzle), hintEngine.nextTeachingStep(puzzle))
    }

    @Test
    fun legacyPlacementHintRemainsCompatibleForNakedSingle() {
        val board = SudokuBoard.parse(
            "534678912672195348198342567859761423426853791713924856961537284287419635345286170",
        )
        val step = assertNotNull(hintEngine.nextTeachingStep(board))
        val hint = assertNotNull(hintEngine.nextHint(board))

        assertEquals(LogicalTechnique.NAKED_SINGLE, step.technique)
        assertEquals(80, step.placement?.cellIndex)
        assertEquals(9, step.placement?.value)
        assertEquals(HintTechnique.NAKED_SINGLE, hint.technique)
        assertEquals(step.placement?.cellIndex, hint.cellIndex)
        assertEquals(step.placement?.value, hint.value)
    }

    @Test
    fun completedBoardHasNoTeachingOrLegacyHint() {
        val solved = SudokuBoard.parse(
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
        )

        assertEquals(null, hintEngine.nextTeachingStep(solved))
        assertEquals(null, hintEngine.nextHint(solved))
    }
}
