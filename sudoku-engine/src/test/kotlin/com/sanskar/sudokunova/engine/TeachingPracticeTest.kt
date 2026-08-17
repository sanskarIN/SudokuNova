package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TeachingPracticeTest {
    private val eliminationStep = TeachingStep(
        technique = LogicalTechnique.NAKED_PAIR,
        sourceCells = setOf(0, 1),
        sourceUnit = LogicalUnit(LogicalUnitType.ROW, 0),
        affectedUnit = LogicalUnit(LogicalUnitType.ROW, 0),
        candidateValues = setOf(1, 2),
        eliminations = listOf(
            CandidateElimination(2, 1),
            CandidateElimination(3, 2),
        ),
    )

    private val placementStep = TeachingStep(
        technique = LogicalTechnique.NAKED_SINGLE,
        sourceCells = setOf(2),
        candidateValues = setOf(3),
        placement = TeachingPlacement(2, 3),
    )

    @Test
    fun incorrectActionDoesNotMutatePracticeState() {
        val state = TeachingPracticeState.start(listOf(eliminationStep, placementStep))
        val submission = state.submit(PracticeAction.Eliminate(4, 1))

        assertEquals(PracticeSubmissionResult.INCORRECT, submission.result)
        assertEquals(state, submission.state)
    }

    @Test
    fun multiEliminationStepRequiresEveryExpectedAction() {
        val state = TeachingPracticeState.start(listOf(eliminationStep, placementStep))
        val first = state.submit(PracticeAction.Eliminate(2, 1))

        assertEquals(PracticeSubmissionResult.CORRECT_PROGRESS, first.result)
        assertEquals(setOf(PracticeAction.Eliminate(3, 2)), first.state.remainingActions)
        assertEquals(0, first.state.stepIndex)

        val second = first.state.submit(PracticeAction.Eliminate(3, 2))
        assertEquals(PracticeSubmissionResult.STEP_COMPLETED, second.result)
        assertEquals(1, second.state.stepIndex)
        assertEquals(setOf(PracticeAction.Place(2, 3)), second.state.remainingActions)
    }

    @Test
    fun finalPlacementCompletesSessionAndFurtherSubmissionsAreStable() {
        var state = TeachingPracticeState.start(listOf(eliminationStep, placementStep))
        state = state.submit(PracticeAction.Eliminate(2, 1)).state
        state = state.submit(PracticeAction.Eliminate(3, 2)).state
        val completion = state.submit(PracticeAction.Place(2, 3))

        assertEquals(PracticeSubmissionResult.SESSION_COMPLETED, completion.result)
        assertTrue(completion.state.isComplete)
        assertEquals(null, completion.state.currentStep)
        assertTrue(completion.state.remainingActions.isEmpty())

        val repeated = completion.state.submit(PracticeAction.Place(2, 3))
        assertEquals(PracticeSubmissionResult.SESSION_ALREADY_COMPLETED, repeated.result)
        assertEquals(completion.state, repeated.state)
    }

    @Test
    fun placementOnlySequenceCompletesInOneCorrectAction() {
        val sequence = TeachingHintSequence(listOf(placementStep))
        val state = TeachingPracticeState.start(sequence)

        assertFalse(state.isComplete)
        val result = state.submit(PracticeAction.Place(2, 3))
        assertEquals(PracticeSubmissionResult.SESSION_COMPLETED, result.result)
        assertTrue(result.state.isComplete)
    }
}
