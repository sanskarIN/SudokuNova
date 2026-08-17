package com.sanskar.sudokunova.engine

enum class LogicalDifficultyBand {
    SOLVED,
    SINGLES_ONLY,
    INTERMEDIATE_TECHNIQUES_LIKELY,
    ADVANCED_TECHNIQUES_LIKELY,
}

data class LogicalDifficultyEvidence(
    val startingEmptyCells: Int,
    val nakedSingles: Int,
    val hiddenSingles: Int,
    val nakedPairSignals: Int,
    val pointingSignals: Int,
    val boxLineReductionSignals: Int,
    val unresolvedCells: Int,
) {
    val singlesPlaced: Int
        get() = nakedSingles + hiddenSingles

    val solvedWithSingles: Boolean
        get() = unresolvedCells == 0

    val band: LogicalDifficultyBand
        get() = when {
            startingEmptyCells == 0 -> LogicalDifficultyBand.SOLVED
            solvedWithSingles -> LogicalDifficultyBand.SINGLES_ONLY
            nakedPairSignals + pointingSignals + boxLineReductionSignals > 0 ->
                LogicalDifficultyBand.INTERMEDIATE_TECHNIQUES_LIKELY
            else -> LogicalDifficultyBand.ADVANCED_TECHNIQUES_LIKELY
        }

    /**
     * A deterministic evidence score for calibration and regression comparisons.
     * It is intentionally not presented as a complete human difficulty rating.
     */
    val logicalScore: Int
        get() = (
            startingEmptyCells * 2 +
                hiddenSingles * 3 +
                nakedPairSignals * 10 +
                pointingSignals * 12 +
                boxLineReductionSignals * 14 +
                unresolvedCells * 20
            ).coerceAtLeast(0)
}

/**
 * Provides human-logic evidence without consulting the solved board.
 *
 * The analyzer first applies only techniques it can prove directly from the current board:
 * Naked Single and Hidden Single. If those stall, it scans the remaining candidate grid for
 * intermediate patterns. Pattern counts are calibration signals; they do not claim that a
 * particular signal is the only or required next move.
 */
object LogicalDifficultyAnalyzer {
    fun analyze(input: SudokuBoard): LogicalDifficultyEvidence {
        require(input.isValid()) { "Logical difficulty analysis requires a valid Sudoku board." }

        val startingEmptyCells = SudokuBoard.CELL_COUNT - input.clueCount
        var board = input
        var nakedSingles = 0
        var hiddenSingles = 0

        while (!board.isComplete) {
            val naked = findNakedSingle(board)
            if (naked != null) {
                board = board.withValue(naked.index, naked.value)
                nakedSingles++
                continue
            }

            val hidden = findHiddenSingle(board)
            if (hidden != null) {
                board = board.withValue(hidden.index, hidden.value)
                hiddenSingles++
                continue
            }

            break
        }

        val remainingCandidates = candidateMap(board)
        return LogicalDifficultyEvidence(
            startingEmptyCells = startingEmptyCells,
            nakedSingles = nakedSingles,
            hiddenSingles = hiddenSingles,
            nakedPairSignals = countNakedPairSignals(remainingCandidates),
            pointingSignals = countPointingSignals(remainingCandidates),
            boxLineReductionSignals = countBoxLineReductionSignals(remainingCandidates),
            unresolvedCells = SudokuBoard.CELL_COUNT - board.clueCount,
        )
    }

    private data class Placement(val index: Int, val value: Int)

    private fun findNakedSingle(board: SudokuBoard): Placement? {
        for (index in 0 until SudokuBoard.CELL_COUNT) {
            if (board.valueAt(index) != SudokuBoard.EMPTY) continue
            val candidates = board.candidates(index)
            if (candidates.size == 1) return Placement(index, candidates.single())
        }
        return null
    }

    private fun findHiddenSingle(board: SudokuBoard): Placement? {
        for (unit in allUnits()) {
            for (value in 1..9) {
                val possible = unit.filter { index ->
                    board.valueAt(index) == SudokuBoard.EMPTY && value in board.candidates(index)
                }
                if (possible.size == 1) return Placement(possible.single(), value)
            }
        }
        return null
    }

    private fun candidateMap(board: SudokuBoard): Map<Int, Set<Int>> = buildMap {
        for (index in 0 until SudokuBoard.CELL_COUNT) {
            if (board.valueAt(index) == SudokuBoard.EMPTY) {
                put(index, board.candidates(index))
            }
        }
    }

    private fun countNakedPairSignals(candidates: Map<Int, Set<Int>>): Int {
        var signals = 0
        for (unit in allUnits()) {
            val pairs = unit
                .mapNotNull { index -> candidates[index]?.takeIf { it.size == 2 }?.let { index to it } }
                .groupBy { it.second }
            signals += pairs.values.count { entries -> entries.size == 2 }
        }
        return signals
    }

    private fun countPointingSignals(candidates: Map<Int, Set<Int>>): Int {
        var signals = 0
        for (box in boxes()) {
            for (value in 1..9) {
                val cells = box.filter { index -> value in candidates[index].orEmpty() }
                if (cells.size < 2) continue
                val rows = cells.map { it / SudokuBoard.SIZE }.toSet()
                val columns = cells.map { it % SudokuBoard.SIZE }.toSet()
                if (rows.size == 1 && hasCandidateOutsideBoxInRow(candidates, value, rows.single(), box)) {
                    signals++
                }
                if (columns.size == 1 && hasCandidateOutsideBoxInColumn(candidates, value, columns.single(), box)) {
                    signals++
                }
            }
        }
        return signals
    }

    private fun countBoxLineReductionSignals(candidates: Map<Int, Set<Int>>): Int {
        var signals = 0
        for (row in 0 until SudokuBoard.SIZE) {
            val rowIndices = rowIndices(row)
            for (value in 1..9) {
                val cells = rowIndices.filter { value in candidates[it].orEmpty() }
                if (cells.size < 2) continue
                val boxes = cells.map(::boxIndex).toSet()
                if (boxes.size == 1 && hasCandidateElsewhereInBox(candidates, value, boxes.single(), cells.toSet())) {
                    signals++
                }
            }
        }
        for (column in 0 until SudokuBoard.SIZE) {
            val columnIndices = columnIndices(column)
            for (value in 1..9) {
                val cells = columnIndices.filter { value in candidates[it].orEmpty() }
                if (cells.size < 2) continue
                val boxes = cells.map(::boxIndex).toSet()
                if (boxes.size == 1 && hasCandidateElsewhereInBox(candidates, value, boxes.single(), cells.toSet())) {
                    signals++
                }
            }
        }
        return signals
    }

    private fun hasCandidateOutsideBoxInRow(
        candidates: Map<Int, Set<Int>>,
        value: Int,
        row: Int,
        box: List<Int>,
    ): Boolean {
        val boxSet = box.toSet()
        return rowIndices(row).any { index -> index !in boxSet && value in candidates[index].orEmpty() }
    }

    private fun hasCandidateOutsideBoxInColumn(
        candidates: Map<Int, Set<Int>>,
        value: Int,
        column: Int,
        box: List<Int>,
    ): Boolean {
        val boxSet = box.toSet()
        return columnIndices(column).any { index -> index !in boxSet && value in candidates[index].orEmpty() }
    }

    private fun hasCandidateElsewhereInBox(
        candidates: Map<Int, Set<Int>>,
        value: Int,
        boxIndex: Int,
        excluded: Set<Int>,
    ): Boolean = boxIndices(boxIndex).any { index ->
        index !in excluded && value in candidates[index].orEmpty()
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
}
