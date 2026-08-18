package com.sanskar.sudokunova.engine

enum class LogicalTechnique(val rank: Int) {
    NAKED_SINGLE(1),
    HIDDEN_SINGLE(2),
    NAKED_PAIR(3),
    POINTING_PAIR_OR_TRIPLE(4),
    BOX_LINE_REDUCTION(5),
    HIDDEN_PAIR(6),
    NAKED_TRIPLE(7),
    HIDDEN_TRIPLE(8),
    X_WING(9),
}

data class LogicalSolveResult(
    val board: SudokuBoard,
    val techniqueUsage: Map<LogicalTechnique, Int>,
    val candidateEliminations: Int,
    val unresolvedCells: Int,
) {
    val solved: Boolean
        get() = board.isComplete

    val hardestTechnique: LogicalTechnique?
        get() = techniqueUsage
            .filterValues { it > 0 }
            .keys
            .maxByOrNull { it.rank }

    val placements: Int
        get() = techniqueUsage[LogicalTechnique.NAKED_SINGLE].orZero() +
            techniqueUsage[LogicalTechnique.HIDDEN_SINGLE].orZero()

    private fun Int?.orZero(): Int = this ?: 0
}

class LogicalSolver(
    private val teachingStepFinder: TeachingStepFinder = TeachingStepFinder(),
) {
    fun solve(input: SudokuBoard): LogicalSolveResult {
        require(input.isValid()) { "Logical solving requires a valid Sudoku board." }

        val trace = teachingStepFinder.trace(input)
        val usage = LogicalTechnique.entries.associateWith { technique ->
            trace.steps.count { it.technique == technique }
        }

        return LogicalSolveResult(
            board = trace.finalBoard,
            techniqueUsage = usage,
            candidateEliminations = trace.steps.sumOf { it.candidateEliminations.size },
            unresolvedCells = trace.unresolvedCells,
        )
    }
}
