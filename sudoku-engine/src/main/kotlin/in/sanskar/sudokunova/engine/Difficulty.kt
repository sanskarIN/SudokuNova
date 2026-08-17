package in.sanskar.sudokunova.engine

enum class Difficulty(
    val displayName: String,
    val targetClues: IntRange,
    val minimumScore: Int,
) {
    BEGINNER("Beginner", 46..52, 0),
    EASY("Easy", 40..45, 90),
    MEDIUM("Medium", 35..39, 145),
    HARD("Hard", 31..34, 210),
    EXPERT("Expert", 28..30, 285),
    MASTER("Master", 25..27, 380),
    EXTREME("Extreme", 22..24, 500),
}

data class SolveMetrics(
    val visitedNodes: Int = 0,
    val guesses: Int = 0,
    val backtracks: Int = 0,
    val maxDepth: Int = 0,
)

data class DifficultyAssessment(
    val score: Int,
    val estimatedDifficulty: Difficulty,
    val metrics: SolveMetrics,
)

object DifficultyRater {
    fun assess(board: SudokuBoard, metrics: SolveMetrics): DifficultyAssessment {
        val emptyCells = 81 - board.clueCount
        val score =
            emptyCells * 4 +
                metrics.guesses * 20 +
                metrics.backtracks * 3 +
                metrics.maxDepth * 2

        val estimated = Difficulty.entries
            .lastOrNull { score >= it.minimumScore }
            ?: Difficulty.BEGINNER

        return DifficultyAssessment(
            score = score,
            estimatedDifficulty = estimated,
            metrics = metrics,
        )
    }
}
