package in.sanskar.sudokunova.engine

import kotlin.random.Random

data class GeneratedPuzzle(
    val puzzle: SudokuBoard,
    val solution: SudokuBoard,
    val difficulty: Difficulty,
    val assessment: DifficultyAssessment,
    val seed: Long,
)

class SudokuGenerator(
    private val solver: SudokuSolver = SudokuSolver(),
) {
    fun generate(
        difficulty: Difficulty,
        seed: Long = Random.nextLong(),
    ): GeneratedPuzzle {
        val random = Random(seed)
        var best: GeneratedPuzzle? = null

        repeat(MAX_GENERATION_ATTEMPTS) { attempt ->
            val attemptRandom = Random(random.nextLong() xor attempt.toLong())
            val solution = generateSolvedBoard(attemptRandom)
            val puzzle = carvePuzzle(solution, difficulty, attemptRandom)
            val analysis = solver.analyze(puzzle, solutionLimit = 2)

            if (analysis.solutionCount == 1) {
                val assessment = DifficultyRater.assess(puzzle, analysis.metrics)
                val generated = GeneratedPuzzle(
                    puzzle = puzzle,
                    solution = solution,
                    difficulty = difficulty,
                    assessment = assessment,
                    seed = seed,
                )

                if (best == null || distanceToRequested(generated, difficulty) < distanceToRequested(best!!, difficulty)) {
                    best = generated
                }

                if (puzzle.clueCount in difficulty.targetClues &&
                    assessment.score >= difficulty.minimumScore
                ) {
                    return generated
                }
            }
        }

        return requireNotNull(best) { "Unable to generate a uniquely solvable Sudoku puzzle." }
    }

    private fun generateSolvedBoard(random: Random): SudokuBoard {
        val cells = IntArray(SudokuBoard.CELL_COUNT)

        fun candidateValues(index: Int): List<Int> {
            val row = index / SudokuBoard.SIZE
            val column = index % SudokuBoard.SIZE
            val used = BooleanArray(10)

            for (i in 0 until SudokuBoard.SIZE) {
                used[cells[row * SudokuBoard.SIZE + i]] = true
                used[cells[i * SudokuBoard.SIZE + column]] = true
            }

            val boxRow = (row / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE
            val boxColumn = (column / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE
            for (r in boxRow until boxRow + SudokuBoard.BOX_SIZE) {
                for (c in boxColumn until boxColumn + SudokuBoard.BOX_SIZE) {
                    used[cells[r * SudokuBoard.SIZE + c]] = true
                }
            }

            return (1..9).filterNot { used[it] }.shuffled(random)
        }

        fun fill(index: Int): Boolean {
            if (index == SudokuBoard.CELL_COUNT) return true
            if (cells[index] != 0) return fill(index + 1)

            for (value in candidateValues(index)) {
                cells[index] = value
                if (fill(index + 1)) return true
                cells[index] = 0
            }
            return false
        }

        check(fill(0)) { "Failed to create a solved Sudoku grid." }
        return SudokuBoard.from(cells)
    }

    private fun carvePuzzle(
        solution: SudokuBoard,
        difficulty: Difficulty,
        random: Random,
    ): SudokuBoard {
        var puzzle = solution
        val target = difficulty.targetClues.random(random)
        val removalOrder = (0 until SudokuBoard.CELL_COUNT).shuffled(random)

        for (index in removalOrder) {
            if (puzzle.clueCount <= target) break
            val value = puzzle.valueAt(index)
            if (value == SudokuBoard.EMPTY) continue

            val candidate = puzzle.withValue(index, SudokuBoard.EMPTY)
            if (solver.hasUniqueSolution(candidate)) {
                puzzle = candidate
            }
        }
        return puzzle
    }

    private fun distanceToRequested(
        generated: GeneratedPuzzle,
        requested: Difficulty,
    ): Int {
        val clueDistance = when {
            generated.puzzle.clueCount < requested.targetClues.first -> requested.targetClues.first - generated.puzzle.clueCount
            generated.puzzle.clueCount > requested.targetClues.last -> generated.puzzle.clueCount - requested.targetClues.last
            else -> 0
        }
        val scoreDistance = (requested.minimumScore - generated.assessment.score).coerceAtLeast(0) / 10
        return clueDistance * 5 + scoreDistance
    }

    private companion object {
        const val MAX_GENERATION_ATTEMPTS = 6
    }
}
