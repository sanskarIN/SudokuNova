package com.sanskar.sudokunova.engine

enum class SudokuUnitType {
    ROW,
    COLUMN,
    BOX,
}

data class SudokuUnitRef(
    val type: SudokuUnitType,
    val index: Int,
) {
    init {
        require(index in 0 until SudokuBoard.SIZE) { "Sudoku unit index must be in 0..8." }
    }
}

data class CandidateElimination(
    val cellIndex: Int,
    val candidate: Int,
) {
    init {
        require(cellIndex in 0 until SudokuBoard.CELL_COUNT) { "Cell index must be in 0..80." }
        require(candidate in 1..9) { "Candidate must be in 1..9." }
    }
}

data class TeachingPlacement(
    val cellIndex: Int,
    val value: Int,
) {
    init {
        require(cellIndex in 0 until SudokuBoard.CELL_COUNT) { "Cell index must be in 0..80." }
        require(value in 1..9) { "Placement value must be in 1..9." }
    }
}

data class TeachingStep(
    val technique: LogicalTechnique,
    val sourceCells: List<Int>,
    val sourceUnit: SudokuUnitRef?,
    val targetCells: List<Int>,
    val candidateEliminations: List<CandidateElimination> = emptyList(),
    val placement: TeachingPlacement? = null,
) {
    init {
        require(sourceCells.all { it in 0 until SudokuBoard.CELL_COUNT }) { "Source cells must be on the Sudoku board." }
        require(targetCells.all { it in 0 until SudokuBoard.CELL_COUNT }) { "Target cells must be on the Sudoku board." }
        require(sourceCells.distinct().size == sourceCells.size) { "Source cells must not contain duplicates." }
        require(targetCells.distinct().size == targetCells.size) { "Target cells must not contain duplicates." }
        require(candidateEliminations.distinct().size == candidateEliminations.size) {
            "Candidate eliminations must not contain duplicates."
        }
        require(placement != null || candidateEliminations.isNotEmpty()) {
            "A teaching step must either place a value or eliminate at least one candidate."
        }
    }

    val isPlacement: Boolean
        get() = placement != null

    val isElimination: Boolean
        get() = candidateEliminations.isNotEmpty()
}
