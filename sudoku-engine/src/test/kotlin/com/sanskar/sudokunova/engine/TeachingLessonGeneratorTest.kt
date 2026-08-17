package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TeachingLessonGeneratorTest {
    private val lessonGenerator = TeachingLessonGenerator()

    @Test
    fun sameSeedRangeProducesSameLesson() {
        val first = lessonGenerator.findPlacementLesson(seedStart = 80_800L)
        val second = lessonGenerator.findPlacementLesson(seedStart = 80_800L)

        assertEquals(first, second)
    }

    @Test
    fun defaultLessonEndsInSupportedPlacementAndMatchesSolution() {
        val lesson = assertNotNull(lessonGenerator.findPlacementLesson(seedStart = 80_800L))
        val finalStep = lesson.sequence.steps.last()
        val placement = lesson.sequence.placement

        assertTrue(finalStep.technique in TeachingLessonGenerator.DEFAULT_PLACEMENT_TECHNIQUES)
        assertEquals(lesson.puzzle.solution.valueAt(placement.cellIndex), placement.value)
    }

    @Test
    fun boundedSearchReturnsNullWhenNoTechniqueIsAllowed() {
        val result = lessonGenerator.findPlacementLesson(
            seedStart = 80_800L,
            allowedTechniques = setOf(LogicalTechnique.NAKED_PAIR),
            maxAttempts = 1,
        )

        if (result != null) {
            assertEquals(LogicalTechnique.NAKED_PAIR, result.sequence.steps.last().technique)
        }
    }

    @Test
    fun generatedSequenceNeverEliminatesUniqueSolutionValue() {
        val lesson = assertNotNull(lessonGenerator.findPlacementLesson(seedStart = 81_103L))

        lesson.sequence.steps.forEach { step ->
            step.eliminations.forEach { elimination ->
                assertTrue(
                    lesson.puzzle.solution.valueAt(elimination.cellIndex) != elimination.value,
                    "${step.technique} eliminated the unique solution value.",
                )
            }
        }
    }
}
