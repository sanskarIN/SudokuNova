package com.sanskar.sudokunova.engine

enum class HintTechnique(val displayName: String) {
    NAKED_SINGLE("Naked Single"),
    HIDDEN_SINGLE("Hidden Single"),
    REVEAL("Reveal"),
}

data class SudokuHint(
    val cellIndex: Int,
    val value: Int,
    val technique: HintTechnique,
    val explanation: String,
)

class HintEngine(
    private val solver: SudokuSolver = SudokuSolver(),
    private val teachingEngine: LogicalTeachingEngine = LogicalTeachingEngine(),
) {
    fun nextTeachingStep(board: SudokuBoard): TeachingStep? {
        if (!board.isValid() || board.isComplete) return null
        return teachingEngine.nextStep(board)
    }

    fun nextTeachingHint(board: SudokuBoard): TeachingHintSequence? {
        if (!board.isValid() || board.isComplete) return null
        return teachingEngine.nextPlacementSequence(board)
    }

    fun nextHint(board: SudokuBoard): SudokuHint? {
        if (!board.isValid() || board.isComplete) return null

        nextTeachingStep(board)?.toLegacyPlacementHint()?.let { return it }

        val solution = solver.solve(board).solution ?: return null
        val index = board.emptyIndices().firstOrNull() ?: return null
        return SudokuHint(
            cellIndex = index,
            value = solution.valueAt(index),
            technique = HintTechnique.REVEAL,
            explanation = "No supported direct technique was found. Reveal this cell only if you want a stronger hint.",
        )
    }

    private fun TeachingStep.toLegacyPlacementHint(): SudokuHint? {
        val placement = placement ?: return null
        return when (technique) {
            LogicalTechnique.NAKED_SINGLE -> SudokuHint(
                cellIndex = placement.cellIndex,
                value = placement.value,
                technique = HintTechnique.NAKED_SINGLE,
                explanation = "This cell has only one valid candidate: ${placement.value}.",
            )
            LogicalTechnique.HIDDEN_SINGLE -> SudokuHint(
                cellIndex = placement.cellIndex,
                value = placement.value,
                technique = HintTechnique.HIDDEN_SINGLE,
                explanation = "${placement.value} can appear in only one cell of this ${sourceUnit.legacyName()}.",
            )
            else -> null
        }
    }
}

private fun LogicalUnit?.legacyName(): String = when (this?.type) {
    LogicalUnitType.ROW -> "row ${index + 1}"
    LogicalUnitType.COLUMN -> "column ${index + 1}"
    LogicalUnitType.BOX -> "3×3 box"
    null -> "unit"
}
