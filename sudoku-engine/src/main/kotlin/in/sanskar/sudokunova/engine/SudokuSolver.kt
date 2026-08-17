package in.sanskar.sudokunova.engine

data class SolveResult(
    val solution: SudokuBoard?,
    val solutionCount: Int,
    val metrics: SolveMetrics,
) {
    val hasUniqueSolution: Boolean get() = solutionCount == 1
    val isSolvable: Boolean get() = solutionCount > 0
}

class SudokuSolver {
    fun solve(board: SudokuBoard): SolveResult = analyze(board, solutionLimit = 1)

    fun analyze(board: SudokuBoard, solutionLimit: Int = 2): SolveResult {
        require(solutionLimit >= 1) { "solutionLimit must be at least 1." }
        if (!board.isValid()) return SolveResult(null, 0, SolveMetrics())

        val cells = board.toIntArray()
        var solutions = 0
        var firstSolution: IntArray? = null
        var visitedNodes = 0
        var guesses = 0
        var backtracks = 0
        var maximumDepth = 0

        fun candidatesMask(index: Int): Int {
            val row = index / SudokuBoard.SIZE
            val column = index % SudokuBoard.SIZE
            var usedMask = 0

            for (i in 0 until SudokuBoard.SIZE) {
                val rowValue = cells[row * SudokuBoard.SIZE + i]
                val columnValue = cells[i * SudokuBoard.SIZE + column]
                if (rowValue != 0) usedMask = usedMask or (1 shl rowValue)
                if (columnValue != 0) usedMask = usedMask or (1 shl columnValue)
            }

            val boxRow = (row / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE
            val boxColumn = (column / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE
            for (r in boxRow until boxRow + SudokuBoard.BOX_SIZE) {
                for (c in boxColumn until boxColumn + SudokuBoard.BOX_SIZE) {
                    val value = cells[r * SudokuBoard.SIZE + c]
                    if (value != 0) usedMask = usedMask or (1 shl value)
                }
            }

            val allDigitsMask = 0b1111111110
            return allDigitsMask and usedMask.inv()
        }

        fun search(depth: Int) {
            if (solutions >= solutionLimit) return
            visitedNodes++
            maximumDepth = maxOf(maximumDepth, depth)

            var bestIndex = -1
            var bestMask = 0
            var bestCount = Int.MAX_VALUE

            for (index in cells.indices) {
                if (cells[index] != 0) continue
                val mask = candidatesMask(index)
                val count = Integer.bitCount(mask)
                if (count == 0) {
                    backtracks++
                    return
                }
                if (count < bestCount) {
                    bestCount = count
                    bestIndex = index
                    bestMask = mask
                    if (count == 1) break
                }
            }

            if (bestIndex == -1) {
                solutions++
                if (firstSolution == null) firstSolution = cells.copyOf()
                return
            }

            if (bestCount > 1) guesses++
            var mask = bestMask
            while (mask != 0 && solutions < solutionLimit) {
                val bit = mask and -mask
                val value = Integer.numberOfTrailingZeros(bit)
                cells[bestIndex] = value
                search(depth + 1)
                cells[bestIndex] = 0
                mask = mask xor bit
            }
        }

        search(depth = 0)

        return SolveResult(
            solution = firstSolution?.let(SudokuBoard::from),
            solutionCount = solutions,
            metrics = SolveMetrics(
                visitedNodes = visitedNodes,
                guesses = guesses,
                backtracks = backtracks,
                maxDepth = maximumDepth,
            ),
        )
    }

    fun hasUniqueSolution(board: SudokuBoard): Boolean = analyze(board, solutionLimit = 2).solutionCount == 1
}
