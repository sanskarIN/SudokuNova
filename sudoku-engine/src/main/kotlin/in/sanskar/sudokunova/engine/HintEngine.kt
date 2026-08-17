package in.sanskar.sudokunova.engine

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
) {
    fun nextHint(board: SudokuBoard): SudokuHint? {
        if (!board.isValid() || board.isComplete) return null

        findNakedSingle(board)?.let { return it }
        findHiddenSingle(board)?.let { return it }

        val solution = solver.solve(board).solution ?: return null
        val index = board.emptyIndices().firstOrNull() ?: return null
        return SudokuHint(
            cellIndex = index,
            value = solution.valueAt(index),
            technique = HintTechnique.REVEAL,
            explanation = "No supported direct technique was found. Reveal this cell only if you want a stronger hint.",
        )
    }

    private fun findNakedSingle(board: SudokuBoard): SudokuHint? {
        for (index in board.emptyIndices()) {
            val candidates = board.candidates(index)
            if (candidates.size == 1) {
                val value = candidates.first()
                return SudokuHint(
                    cellIndex = index,
                    value = value,
                    technique = HintTechnique.NAKED_SINGLE,
                    explanation = "This cell has only one valid candidate: $value.",
                )
            }
        }
        return null
    }

    private fun findHiddenSingle(board: SudokuBoard): SudokuHint? {
        for (row in 0 until SudokuBoard.SIZE) {
            findUniqueCandidate(
                board = board,
                indices = (0 until SudokuBoard.SIZE).map { column -> row * SudokuBoard.SIZE + column },
                unitName = "row ${row + 1}",
            )?.let { return it }
        }

        for (column in 0 until SudokuBoard.SIZE) {
            findUniqueCandidate(
                board = board,
                indices = (0 until SudokuBoard.SIZE).map { row -> row * SudokuBoard.SIZE + column },
                unitName = "column ${column + 1}",
            )?.let { return it }
        }

        for (boxRow in 0 until SudokuBoard.SIZE step SudokuBoard.BOX_SIZE) {
            for (boxColumn in 0 until SudokuBoard.SIZE step SudokuBoard.BOX_SIZE) {
                val indices = buildList {
                    for (row in boxRow until boxRow + SudokuBoard.BOX_SIZE) {
                        for (column in boxColumn until boxColumn + SudokuBoard.BOX_SIZE) {
                            add(row * SudokuBoard.SIZE + column)
                        }
                    }
                }
                findUniqueCandidate(
                    board = board,
                    indices = indices,
                    unitName = "3×3 box",
                )?.let { return it }
            }
        }
        return null
    }

    private fun findUniqueCandidate(
        board: SudokuBoard,
        indices: List<Int>,
        unitName: String,
    ): SudokuHint? {
        val candidateCells = mutableMapOf<Int, MutableList<Int>>()
        for (index in indices) {
            if (board.valueAt(index) != SudokuBoard.EMPTY) continue
            for (candidate in board.candidates(index)) {
                candidateCells.getOrPut(candidate) { mutableListOf() }.add(index)
            }
        }

        for ((candidate, cells) in candidateCells) {
            if (cells.size == 1) {
                return SudokuHint(
                    cellIndex = cells.single(),
                    value = candidate,
                    technique = HintTechnique.HIDDEN_SINGLE,
                    explanation = "$candidate can appear in only one cell of this $unitName.",
                )
            }
        }
        return null
    }
}
