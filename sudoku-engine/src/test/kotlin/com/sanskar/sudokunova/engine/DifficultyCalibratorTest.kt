package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyCalibratorTest {
    private val generator = SudokuGenerator()

    @Test
    fun calibrationIsDeterministicAndNeverDropsLegacyScore() {
        Difficulty.entries.forEach { difficulty ->
            val generated = generator.generate(
                difficulty = difficulty,
                seed = 9_000L + difficulty.ordinal,
            )

            val first = DifficultyCalibrator.calibrate(
                board = generated.puzzle,
                legacyAssessment = generated.assessment,
                requestedDifficulty = difficulty,
            )
            val second = DifficultyCalibrator.calibrate(
                board = generated.puzzle,
                legacyAssessment = generated.assessment,
                requestedDifficulty = difficulty,
            )

            assertEquals(first, second)
            assertTrue(first.combinedScore >= generated.assessment.score)
            assertTrue(first.suggestedDifficulty in Difficulty.entries)
            assertEquals(difficulty, first.requestedDifficulty)
        }
    }
}
