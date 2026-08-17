package com.sanskar.sudokunova.engine

sealed interface PracticeAction {
    val cellIndex: Int
    val value: Int

    data class Place(
        override val cellIndex: Int,
        override val value: Int,
    ) : PracticeAction {
        init {
            require(cellIndex in 0 until SudokuBoard.CELL_COUNT)
            require(value in 1..9)
        }
    }

    data class Eliminate(
        override val cellIndex: Int,
        override val value: Int,
    ) : PracticeAction {
        init {
            require(cellIndex in 0 until SudokuBoard.CELL_COUNT)
            require(value in 1..9)
        }
    }
}

enum class PracticeSubmissionResult {
    INCORRECT,
    CORRECT_PROGRESS,
    STEP_COMPLETED,
    SESSION_COMPLETED,
    SESSION_ALREADY_COMPLETED,
}

data class TeachingPracticeSubmission(
    val result: PracticeSubmissionResult,
    val state: TeachingPracticeState,
)

@ConsistentCopyVisibility
data class TeachingPracticeState private constructor(
    val steps: List<TeachingStep>,
    val stepIndex: Int,
    val remainingActions: Set<PracticeAction>,
) {
    init {
        require(steps.isNotEmpty())
        require(stepIndex in 0..steps.size)
        if (stepIndex < steps.size) {
            require(remainingActions.isNotEmpty())
            require(remainingActions.all { it in steps[stepIndex].practiceActions() })
        } else {
            require(remainingActions.isEmpty())
        }
    }

    val isComplete: Boolean
        get() = stepIndex == steps.size

    val currentStep: TeachingStep?
        get() = steps.getOrNull(stepIndex)

    fun submit(action: PracticeAction): TeachingPracticeSubmission {
        if (isComplete) {
            return TeachingPracticeSubmission(PracticeSubmissionResult.SESSION_ALREADY_COMPLETED, this)
        }
        if (action !in remainingActions) {
            return TeachingPracticeSubmission(PracticeSubmissionResult.INCORRECT, this)
        }

        val remaining = remainingActions - action
        if (remaining.isNotEmpty()) {
            return TeachingPracticeSubmission(
                PracticeSubmissionResult.CORRECT_PROGRESS,
                copy(remainingActions = remaining),
            )
        }

        val nextIndex = stepIndex + 1
        if (nextIndex == steps.size) {
            return TeachingPracticeSubmission(
                PracticeSubmissionResult.SESSION_COMPLETED,
                TeachingPracticeState(steps, nextIndex, emptySet()),
            )
        }

        return TeachingPracticeSubmission(
            PracticeSubmissionResult.STEP_COMPLETED,
            TeachingPracticeState(
                steps = steps,
                stepIndex = nextIndex,
                remainingActions = steps[nextIndex].practiceActions(),
            ),
        )
    }

    companion object {
        fun start(steps: List<TeachingStep>): TeachingPracticeState {
            require(steps.isNotEmpty()) { "Practice requires at least one vetted teaching step." }
            return TeachingPracticeState(
                steps = steps.toList(),
                stepIndex = 0,
                remainingActions = steps.first().practiceActions(),
            )
        }

        fun start(sequence: TeachingHintSequence): TeachingPracticeState = start(sequence.steps)
    }
}

fun TeachingStep.practiceActions(): Set<PracticeAction> = when {
    placement != null -> setOf(PracticeAction.Place(placement.cellIndex, placement.value))
    eliminations.isNotEmpty() -> eliminations
        .mapTo(linkedSetOf()) { PracticeAction.Eliminate(it.cellIndex, it.value) }
    else -> error("TeachingStep invariants require a placement or eliminations.")
}
