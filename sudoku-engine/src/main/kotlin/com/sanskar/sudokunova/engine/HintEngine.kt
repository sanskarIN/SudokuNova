package com.sanskar.sudokunova.engine

enum class HintTechnique {
    NAKED_SINGLE,
    HIDDEN_SINGLE,
    NAKED_PAIR,
    POINTING_PAIR_OR_TRIPLE,
    BOX_LINE_REDUCTION,
    HIDDEN_PAIR,
    NAKED_TRIPLE,
    HIDDEN_TRIPLE,
    X_WING,
    REVEAL,
}

data class SudokuHint(
    val teachingSteps: List<TeachingStep>,
    val revealPlacement: TeachingPlacement? = null,
) {
    init {
        require(teachingSteps.isNotEmpty() || revealPlacement != null) {
            "A hint must contain teaching evidence or an explicit reveal placement."
        }
        require(revealPlacement == null || teachingSteps.isEmpty()) {
            "Reveal fallback must remain separate from supported teaching evidence."
        }
        if (teachingSteps.isNotEmpty()) {
            require(teachingSteps.last().placement != null) {
                "A directly applicable hint must end with a supported placement step."
            }
        }
    }

    val technique: HintTechnique
        get() = revealPlacement?.let { HintTechnique.REVEAL }
            ?: teachingSteps.last().technique.toHintTechnique()

    val placement: TeachingPlacement
        get() = revealPlacement ?: requireNotNull(teachingSteps.last().placement)

    val cellIndex: Int
        get() = placement.cellIndex

    val value: Int
        get() = placement.value

    val usesAdvancedElimination: Boolean
        get() = teachingSteps.dropLast(1).any { it.isElimination }
}

class HintEngine(
    private val solver: SudokuSolver = SudokuSolver(),
    private val teachingStepFinder: TeachingStepFinder = TeachingStepFinder(),
) {
    fun nextHint(board: SudokuBoard): SudokuHint? {
        if (!board.isValid() || board.isComplete) return null

        val trace = teachingStepFinder.trace(board)
        val placementIndex = trace.steps.indexOfFirst { it.placement != null }
        if (placementIndex >= 0) {
            return SudokuHint(
                teachingSteps = trace.steps.take(placementIndex + 1),
            )
        }

        val solution = solver.solve(board).solution ?: return null
        val index = board.emptyIndices().firstOrNull() ?: return null
        return SudokuHint(
            teachingSteps = emptyList(),
            revealPlacement = TeachingPlacement(index, solution.valueAt(index)),
        )
    }
}

private fun LogicalTechnique.toHintTechnique(): HintTechnique = when (this) {
    LogicalTechnique.NAKED_SINGLE -> HintTechnique.NAKED_SINGLE
    LogicalTechnique.HIDDEN_SINGLE -> HintTechnique.HIDDEN_SINGLE
    LogicalTechnique.NAKED_PAIR -> HintTechnique.NAKED_PAIR
    LogicalTechnique.POINTING_PAIR_OR_TRIPLE -> HintTechnique.POINTING_PAIR_OR_TRIPLE
    LogicalTechnique.BOX_LINE_REDUCTION -> HintTechnique.BOX_LINE_REDUCTION
    LogicalTechnique.HIDDEN_PAIR -> HintTechnique.HIDDEN_PAIR
    LogicalTechnique.NAKED_TRIPLE -> HintTechnique.NAKED_TRIPLE
    LogicalTechnique.HIDDEN_TRIPLE -> HintTechnique.HIDDEN_TRIPLE
    LogicalTechnique.X_WING -> HintTechnique.X_WING
}
