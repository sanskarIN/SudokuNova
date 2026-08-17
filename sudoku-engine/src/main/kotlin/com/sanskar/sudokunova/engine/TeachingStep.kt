package com.sanskar.sudokunova.engine

enum class LogicalUnitType {
    ROW,
    COLUMN,
    BOX,
}

data class LogicalUnit(
    val type: LogicalUnitType,
    val index: Int,
) {
    init {
        require(index in 0 until SudokuBoard.SIZE)
    }
}

data class TeachingPlacement(
    val cellIndex: Int,
    val value: Int,
) {
    init {
        require(cellIndex in 0 until SudokuBoard.CELL_COUNT)
        require(value in 1..9)
    }
}

data class CandidateElimination(
    val cellIndex: Int,
    val value: Int,
) {
    init {
        require(cellIndex in 0 until SudokuBoard.CELL_COUNT)
        require(value in 1..9)
    }
}

data class TeachingStep(
    val technique: LogicalTechnique,
    val sourceCells: Set<Int>,
    val sourceUnit: LogicalUnit? = null,
    val affectedUnit: LogicalUnit? = null,
    val candidateValues: Set<Int> = emptySet(),
    val placement: TeachingPlacement? = null,
    val eliminations: List<CandidateElimination> = emptyList(),
) {
    init {
        require(sourceCells.all { it in 0 until SudokuBoard.CELL_COUNT })
        require(candidateValues.all { it in 1..9 })
        require(placement != null || eliminations.isNotEmpty())
        require(placement == null || eliminations.isEmpty())
        require(eliminations.distinct().size == eliminations.size)
    }

    val affectedCells: Set<Int>
        get() = buildSet {
            placement?.let { add(it.cellIndex) }
            eliminations.forEach { add(it.cellIndex) }
        }

    val isPlacement: Boolean
        get() = placement != null
}

data class LogicalTeachingTrace(
    val initialBoard: SudokuBoard,
    val finalBoard: SudokuBoard,
    val steps: List<TeachingStep>,
) {
    val solved: Boolean
        get() = finalBoard.isComplete

    val placementCount: Int
        get() = steps.count(TeachingStep::isPlacement)

    val candidateEliminationCount: Int
        get() = steps.sumOf { it.eliminations.size }

    val unresolvedCells: Int
        get() = SudokuBoard.CELL_COUNT - finalBoard.clueCount
}
