package com.sanskar.sudokunova.engine

enum class LogicalTechnique(val rank: Int) {
    NAKED_SINGLE(1),
    HIDDEN_SINGLE(2),
    NAKED_PAIR(3),
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

/**
 * Solves only with explicitly implemented human-style logic. It never reads a completed solution.
 * When the supported techniques cannot make further progress, the partial valid board is returned.
 */
class LogicalSolver {
    fun solve(input: SudokuBoard): LogicalSolveResult {
        require(input.isValid()) { "Logical solving requires a valid Sudoku board." }

        val state = CandidateState(input)
        val usage = mutableMapOf<LogicalTechnique, Int>()
        var eliminations = 0
        var iterations = 0

        while (!state.board.isComplete && iterations++ < MAX_ITERATIONS) {
            val nakedSingle = state.findNakedSingle()
            if (nakedSingle != null) {
                state.place(nakedSingle.first, nakedSingle.second)
                usage.increment(LogicalTechnique.NAKED_SINGLE)
                continue
            }

            val hiddenSingle = state.findHiddenSingle()
            if (hiddenSingle != null) {
                state.place(hiddenSingle.first, hiddenSingle.second)
                usage.increment(LogicalTechnique.HIDDEN_SINGLE)
                continue
            }

            val pairEliminations = state.applyNakedPair()
            if (pairEliminations > 0) {
                eliminations += pairEliminations
                usage.increment(LogicalTechnique.NAKED_PAIR)
                continue
            }

            val pointingEliminations = state.applyPointingPairOrTriple()
            if (pointingEliminations > 0) {
                eliminations += pointingEliminations
                usage.increment(LogicalTechnique.POINTING_PAIR_OR_TRIPLE)
                continue
            }

            val boxLineEliminations = state.applyBoxLineReduction()
            if (boxLineEliminations > 0) {
                eliminations += boxLineEliminations
                usage.increment(LogicalTechnique.BOX_LINE_REDUCTION)
                continue
            }

            break
        }

        return LogicalSolveResult(
            board = state.board,
            techniqueUsage = LogicalTechnique.entries.associateWith { usage[it] ?: 0 },
            candidateEliminations = eliminations,
            unresolvedCells = SudokuBoard.CELL_COUNT - state.board.clueCount,
        )
    }

    private class CandidateState(initial: SudokuBoard) {
        var board: SudokuBoard = initial
            private set

        private val candidates: Array<MutableSet<Int>> = Array(SudokuBoard.CELL_COUNT) { index ->
            if (initial.valueAt(index) == SudokuBoard.EMPTY) {
                initial.candidates(index).toMutableSet()
            } else {
                mutableSetOf()
            }
        }

        fun findNakedSingle(): Pair<Int, Int>? {
            for (index in candidates.indices) {
                if (board.valueAt(index) == SudokuBoard.EMPTY && candidates[index].size == 1) {
                    return index to candidates[index].single()
                }
            }
            return null
        }

        fun findHiddenSingle(): Pair<Int, Int>? {
            for (unit in allUnits()) {
                for (value in 1..9) {
                    val matching = unit.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (matching.size == 1) return matching.single() to value
                }
            }
            return null
        }

        fun place(index: Int, value: Int) {
            require(board.valueAt(index) == SudokuBoard.EMPTY)
            require(value in candidates[index])
            board = board.withValue(index, value)
            candidates[index].clear()
            peers(index).forEach { peer -> candidates[peer].remove(value) }
        }

        fun applyNakedPair(): Int {
            for (unit in allUnits()) {
                val groupedPairs = unit
                    .filter { board.valueAt(it) == SudokuBoard.EMPTY && candidates[it].size == 2 }
                    .groupBy { candidates[it].toSet() }
                for ((pair, pairCells) in groupedPairs) {
                    if (pairCells.size != 2) continue
                    var removed = 0
                    for (index in unit) {
                        if (index in pairCells || board.valueAt(index) != SudokuBoard.EMPTY) continue
                        for (value in pair) {
                            if (candidates[index].remove(value)) removed++
                        }
                    }
                    if (removed > 0) return removed
                }
            }
            return 0
        }

        fun applyPointingPairOrTriple(): Int {
            for (box in boxes()) {
                val boxSet = box.toSet()
                for (value in 1..9) {
                    val sourceCells = box.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (sourceCells.size !in 2..3) continue

                    val rows = sourceCells.map { it / SudokuBoard.SIZE }.toSet()
                    if (rows.size == 1) {
                        var removed = 0
                        for (index in rowIndices(rows.single())) {
                            if (index !in boxSet && candidates[index].remove(value)) removed++
                        }
                        if (removed > 0) return removed
                    }

                    val columns = sourceCells.map { it % SudokuBoard.SIZE }.toSet()
                    if (columns.size == 1) {
                        var removed = 0
                        for (index in columnIndices(columns.single())) {
                            if (index !in boxSet && candidates[index].remove(value)) removed++
                        }
                        if (removed > 0) return removed
                    }
                }
            }
            return 0
        }

        fun applyBoxLineReduction(): Int {
            for (row in 0 until SudokuBoard.SIZE) {
                val line = rowIndices(row)
                for (value in 1..9) {
                    val sourceCells = line.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (sourceCells.size < 2) continue
                    val boxIds = sourceCells.map(::boxIndex).toSet()
                    if (boxIds.size == 1) {
                        val sourceSet = sourceCells.toSet()
                        var removed = 0
                        for (index in boxIndices(boxIds.single())) {
                            if (index !in sourceSet && candidates[index].remove(value)) removed++
                        }
                        if (removed > 0) return removed
                    }
                }
            }

            for (column in 0 until SudokuBoard.SIZE) {
                val line = columnIndices(column)
                for (value in 1..9) {
                    val sourceCells = line.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (sourceCells.size < 2) continue
                    val boxIds = sourceCells.map(::boxIndex).toSet()
                    if (boxIds.size == 1) {
                        val sourceSet = sourceCells.toSet()
                        var removed = 0
                        for (index in boxIndices(boxIds.single())) {
                            if (index !in sourceSet && candidates[index].remove(value)) removed++
                        }
                        if (removed > 0) return removed
                    }
                }
            }
            return 0
        }

        private fun peers(index: Int): Set<Int> {
            val row = index / SudokuBoard.SIZE
            val column = index % SudokuBoard.SIZE
            val box = boxIndex(index)
            return buildSet {
                addAll(rowIndices(row))
                addAll(columnIndices(column))
                addAll(boxIndices(box))
                remove(index)
            }
        }
    }

    private companion object {
        const val MAX_ITERATIONS = 10_000
    }
}

private fun MutableMap<LogicalTechnique, Int>.increment(technique: LogicalTechnique) {
    this[technique] = (this[technique] ?: 0) + 1
}

private fun allUnits(): List<List<Int>> = buildList {
    repeat(SudokuBoard.SIZE) { add(rowIndices(it)) }
    repeat(SudokuBoard.SIZE) { add(columnIndices(it)) }
    addAll(boxes())
}

private fun boxes(): List<List<Int>> = (0 until SudokuBoard.SIZE).map(::boxIndices)

private fun rowIndices(row: Int): List<Int> =
    (0 until SudokuBoard.SIZE).map { column -> row * SudokuBoard.SIZE + column }

private fun columnIndices(column: Int): List<Int> =
    (0 until SudokuBoard.SIZE).map { row -> row * SudokuBoard.SIZE + column }

private fun boxIndices(box: Int): List<Int> {
    val startRow = (box / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE
    val startColumn = (box % SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE
    return buildList {
        for (row in startRow until startRow + SudokuBoard.BOX_SIZE) {
            for (column in startColumn until startColumn + SudokuBoard.BOX_SIZE) {
                add(row * SudokuBoard.SIZE + column)
            }
        }
    }
}

private fun boxIndex(index: Int): Int {
    val row = index / SudokuBoard.SIZE
    val column = index % SudokuBoard.SIZE
    return (row / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE + (column / SudokuBoard.BOX_SIZE)
}
