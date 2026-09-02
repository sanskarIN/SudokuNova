package com.sanskar.sudokunova.engine

data class ImportedPuzzle(
    val puzzle: SudokuBoard,
    val solution: SudokuBoard,
    val difficulty: Difficulty,
    val assessment: DifficultyAssessment,
)

class PuzzleExchangeService(
    private val solver: SudokuSolver = SudokuSolver(),
) {
    fun exportCode(puzzle: SudokuBoard, difficulty: Difficulty): String =
        PuzzleCodeCodec.encode(puzzle, difficulty)

    fun importCode(raw: String): ImportedPuzzle? {
        val decoded = PuzzleCodeCodec.decode(raw) ?: return null
        val analysis = solver.analyze(decoded.puzzle, solutionLimit = 2)
        if (!analysis.hasUniqueSolution) return null
        val solution = analysis.solution ?: return null
        val assessment = DifficultyRater.assess(decoded.puzzle, analysis.metrics)

        return ImportedPuzzle(
            puzzle = decoded.puzzle,
            solution = solution,
            difficulty = decoded.difficulty,
            assessment = assessment,
        )
    }
}
