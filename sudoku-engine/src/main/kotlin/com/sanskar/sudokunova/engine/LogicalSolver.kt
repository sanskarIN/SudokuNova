package com.sanskar.sudokunova.engine

enum class LogicalTechnique(val rank: Int) {
    NAKED_SINGLE(1),
    HIDDEN_SINGLE(2),
    NAKED_PAIR(3),
    NAKED_TRIPLE(5),
    POINTING_PAIR_OR_TRIPLE(4),
    BOX_LINE_REDUCTION(5),
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
    private val teachingEngine: LogicalTeachingEngine = LogicalTeachingEngine(),
) {
    fun solve(input: SudokuBoard): LogicalSolveResult {
        val trace = teachingEngine.trace(input)
        val usage = LogicalTechnique.entries.associateWith { technique ->
            trace.steps.count { it.technique == technique }
        }

        return LogicalSolveResult(
            board = trace.finalBoard,
            techniqueUsage = usage,
            candidateEliminations = trace.candidateEliminationCount,
            unresolvedCells = trace.unresolvedCells,
        )
    }
}
