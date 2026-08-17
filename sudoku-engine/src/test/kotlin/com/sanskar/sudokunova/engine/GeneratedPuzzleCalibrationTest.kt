package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GeneratedPuzzleCalibrationTest {
    @Test
    fun generatedPuzzleCarriesLogicalCalibrationEvidence() {
        val generated = SudokuGenerator().generate(Difficulty.MEDIUM, seed = 30_030L)
        val calibrated = assertNotNull(generated.calibratedAssessment)

        assertEquals(Difficulty.MEDIUM, calibrated.requestedDifficulty)
        assertEquals(generated.assessment, calibrated.legacyAssessment)
        assertTrue(calibrated.combinedScore >= generated.assessment.score)
        assertTrue(calibrated.logicalEvidence.startingEmptyCells > 0)
        assertTrue(calibrated.logicalSolveResult.unresolvedCells >= 0)
    }
}
