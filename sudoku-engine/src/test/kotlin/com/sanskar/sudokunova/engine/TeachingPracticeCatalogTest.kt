package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TeachingPracticeCatalogTest {
    @Test
    fun catalogCoversEverySupportedTechnique() {
        val covered = TeachingPracticeCatalog.exercises.map { it.step.technique }.toSet()

        assertEquals(LogicalTechnique.entries.toSet(), covered)
        LogicalTechnique.entries.forEach { technique ->
            assertTrue(TeachingPracticeCatalog.forTechnique(technique).isNotEmpty())
        }
    }

    @Test
    fun exercisesAreDeterministicAndContainTheirCorrectAnswer() {
        LogicalTechnique.entries.forEach { technique ->
            val first = assertNotNull(TeachingPracticeCatalog.exerciseFor(technique, 0))
            val repeated = assertNotNull(TeachingPracticeCatalog.exerciseFor(technique, 9_999))

            assertEquals(first, repeated)
            assertTrue(first.isCorrect(technique))
            assertTrue(technique in first.choices)
            assertEquals(first.choices.distinct(), first.choices)
            first.choices.firstOrNull { it != technique }?.let { wrong ->
                assertFalse(first.isCorrect(wrong))
            }
        }
    }

    @Test
    fun eliminationExercisesExposeTargetsAndCandidates() {
        TeachingPracticeCatalog.exercises
            .filter { it.step.isElimination }
            .forEach { exercise ->
                assertTrue(exercise.step.candidateEliminations.isNotEmpty())
                assertTrue(exercise.step.targetCells.isNotEmpty())
                assertTrue(
                    exercise.step.candidateEliminations.all { it.cellIndex in exercise.step.targetCells },
                )
            }
    }
}
