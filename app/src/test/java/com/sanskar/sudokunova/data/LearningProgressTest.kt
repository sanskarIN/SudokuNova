package com.sanskar.sudokunova.data

import com.sanskar.sudokunova.engine.LogicalTechnique
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningProgressTest {
    @Test
    fun newProgressStartsAtZeroForEveryTechnique() {
        val progress = LearningProgress()

        assertEquals(LogicalTechnique.entries.size, progress.techniques.size)
        assertEquals(0, progress.totalLessonViews)
        assertEquals(0, progress.totalPracticeAttempts)
        assertEquals(0, progress.totalPracticeSuccesses)
        assertEquals(0, progress.masteredTechniqueCount)
        assertEquals(0, progress.overallMasteryPercent)
    }

    @Test
    fun lessonViewAddsSmallProgressWithoutClaimingMastery() {
        val techniqueProgress = TechniqueLearningProgress(lessonViews = 1)

        assertEquals(10, techniqueProgress.masteryPercent)
        assertFalse(techniqueProgress.mastered)
    }

    @Test
    fun repeatedSuccessfulPracticeCanReachMastery() {
        val techniqueProgress = TechniqueLearningProgress(
            lessonViews = 1,
            practiceAttempts = 5,
            practiceSuccesses = 5,
        )

        assertEquals(100, techniqueProgress.masteryPercent)
        assertTrue(techniqueProgress.mastered)
    }

    @Test
    fun aggregateProgressUsesTechniqueSpecificValues() {
        val values = LogicalTechnique.entries.associateWith { TechniqueLearningProgress() }.toMutableMap()
        values[LogicalTechnique.NAKED_SINGLE] = TechniqueLearningProgress(
            lessonViews = 2,
            practiceAttempts = 3,
            practiceSuccesses = 3,
        )
        values[LogicalTechnique.HIDDEN_SINGLE] = TechniqueLearningProgress(
            lessonViews = 1,
            practiceAttempts = 2,
            practiceSuccesses = 1,
        )

        val progress = LearningProgress(values)

        assertEquals(3, progress.totalLessonViews)
        assertEquals(5, progress.totalPracticeAttempts)
        assertEquals(4, progress.totalPracticeSuccesses)
        assertEquals(1, progress.masteredTechniqueCount)
        assertTrue(progress.overallMasteryPercent in 1..100)
    }

    @Test
    fun invalidCountersAreRejected() {
        val error = runCatching {
            TechniqueLearningProgress(
                lessonViews = 0,
                practiceAttempts = 1,
                practiceSuccesses = 2,
            )
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
    }
}
