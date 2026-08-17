package com.sanskar.sudokunova.data.learning

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sanskar.sudokunova.engine.LogicalTechnique
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningProgressRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repository: LearningProgressRepository

    @Before
    fun setUp() = runBlocking {
        repository = LearningProgressRepository(context)
        repository.reset()
    }

    @After
    fun tearDown() = runBlocking {
        repository.reset()
    }

    @Test
    fun emptyProgressContainsEverySupportedTechnique() = runBlocking {
        val snapshot = repository.progress.first()

        assertEquals(LogicalTechnique.entries.size, snapshot.techniques.size)
        LogicalTechnique.entries.forEach { technique ->
            val progress = snapshot[technique]
            assertEquals(0, progress.hintViews)
            assertEquals(0, progress.practiceAttempts)
            assertEquals(0, progress.correctPracticeActions)
            assertEquals(0, progress.completedSteps)
            assertEquals(0, progress.completedSessions)
            assertNull(progress.accuracyPercent)
            assertFalse(progress.hasPractice)
        }
    }

    @Test
    fun recordsHintsPracticeAccuracyStepsAndSessionsPerTechnique() = runBlocking {
        val technique = LogicalTechnique.NAKED_PAIR

        repository.recordHintViewed(technique)
        repository.recordHintViewed(technique)
        repository.recordPracticeAnswer(technique, correct = true, practicedAtEpochMillis = 1_000L)
        repository.recordPracticeAnswer(technique, correct = false, practicedAtEpochMillis = 2_000L)
        repository.recordStepCompleted(technique, practicedAtEpochMillis = 3_000L)
        repository.recordSessionCompleted(
            setOf(technique, LogicalTechnique.NAKED_SINGLE),
            practicedAtEpochMillis = 4_000L,
        )

        val snapshot = repository.progress.first()
        val pair = snapshot[technique]
        assertEquals(2, pair.hintViews)
        assertEquals(2, pair.practiceAttempts)
        assertEquals(1, pair.correctPracticeActions)
        assertEquals(1, pair.completedSteps)
        assertEquals(1, pair.completedSessions)
        assertEquals(50, pair.accuracyPercent)
        assertEquals(4_000L, pair.lastPracticedAtEpochMillis)
        assertTrue(pair.hasPractice)

        val single = snapshot[LogicalTechnique.NAKED_SINGLE]
        assertEquals(1, single.completedSessions)
        assertEquals(4_000L, single.lastPracticedAtEpochMillis)
        assertEquals(2, snapshot.totalHintViews)
        assertEquals(2, snapshot.totalPracticeAttempts)
        assertEquals(1, snapshot.totalCompletedSteps)
    }

    @Test
    fun resetReturnsRepositoryToEmptySnapshot() = runBlocking {
        repository.recordHintViewed(LogicalTechnique.HIDDEN_SINGLE)
        repository.recordPracticeAnswer(
            LogicalTechnique.HIDDEN_SINGLE,
            correct = true,
            practicedAtEpochMillis = 10L,
        )

        repository.reset()
        val progress = repository.progress.first()[LogicalTechnique.HIDDEN_SINGLE]

        assertEquals(0, progress.hintViews)
        assertEquals(0, progress.practiceAttempts)
        assertEquals(0, progress.correctPracticeActions)
        assertNull(progress.lastPracticedAtEpochMillis)
    }
}
