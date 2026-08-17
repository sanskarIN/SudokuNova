package com.sanskar.sudokunova.engine

data class CalibratedDifficultyAssessment(
    val requestedDifficulty: Difficulty,
    val legacyAssessment: DifficultyAssessment,
    val logicalEvidence: LogicalDifficultyEvidence,
    val logicalSolveResult: LogicalSolveResult,
    val combinedScore: Int,
    val suggestedDifficulty: Difficulty,
) {
    val matchesRequestedBand: Boolean
        get() = suggestedDifficulty == requestedDifficulty

    val unresolvedAfterSupportedLogic: Int
        get() = logicalSolveResult.unresolvedCells
}

object DifficultyCalibrator {
    fun calibrate(
        board: SudokuBoard,
        legacyAssessment: DifficultyAssessment,
        requestedDifficulty: Difficulty,
    ): CalibratedDifficultyAssessment {
        val logical = LogicalDifficultyAnalyzer.analyze(board)
        val logicSolve = LogicalSolver().solve(board)
        val hardestTechniqueWeight = (logicSolve.hardestTechnique?.rank ?: 0) * 35
        val unresolvedWeight = logicSolve.unresolvedCells * 14
        val eliminationWeight = logicSolve.candidateEliminations.coerceAtMost(100) * 2
        val signalWeight = logical.logicalScore / 20

        val combinedScore = (
            legacyAssessment.score +
                hardestTechniqueWeight +
                unresolvedWeight +
                eliminationWeight +
                signalWeight
            ).coerceAtLeast(0)

        val suggested = Difficulty.entries
            .filter { combinedScore >= it.minimumScore }
            .maxByOrNull { it.minimumScore }
            ?: Difficulty.entries.minBy { it.minimumScore }

        return CalibratedDifficultyAssessment(
            requestedDifficulty = requestedDifficulty,
            legacyAssessment = legacyAssessment,
            logicalEvidence = logical,
            logicalSolveResult = logicSolve,
            combinedScore = combinedScore,
            suggestedDifficulty = suggested,
        )
    }
}
