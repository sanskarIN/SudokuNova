package com.sanskar.sudokunova.engine

data class TeachingTrace(
    val initialBoard: SudokuBoard,
    val finalBoard: SudokuBoard,
    val steps: List<TeachingStep>,
) {
    val solved: Boolean
        get() = finalBoard.isComplete

    val unresolvedCells: Int
        get() = SudokuBoard.CELL_COUNT - finalBoard.clueCount
}

class TeachingStepFinder {
    fun nextStep(board: SudokuBoard): TeachingStep? {
        if (!board.isValid() || board.isComplete) return null
        return CandidateState(board).nextStep()
    }

    fun trace(board: SudokuBoard, maxSteps: Int = MAX_STEPS): TeachingTrace {
        require(board.isValid()) { "Teaching trace requires a valid Sudoku board." }
        require(maxSteps > 0) { "maxSteps must be positive." }

        val state = CandidateState(board)
        val steps = mutableListOf<TeachingStep>()

        while (!state.board.isComplete && steps.size < maxSteps) {
            val step = state.nextStep() ?: break
            state.apply(step)
            steps += step
        }

        return TeachingTrace(
            initialBoard = board,
            finalBoard = state.board,
            steps = steps,
        )
    }

    internal class CandidateState(initial: SudokuBoard) {
        var board: SudokuBoard = initial
            private set

        private val candidates: Array<MutableSet<Int>> = Array(SudokuBoard.CELL_COUNT) { index ->
            if (initial.valueAt(index) == SudokuBoard.EMPTY) {
                initial.candidates(index).toMutableSet()
            } else {
                mutableSetOf()
            }
        }

        fun nextStep(): TeachingStep? =
            findNakedSingle()
                ?: findHiddenSingle()
                ?: findNakedPair()
                ?: findPointingPairOrTriple()
                ?: findBoxLineReduction()
                ?: findHiddenSubset(size = 2, technique = LogicalTechnique.HIDDEN_PAIR)
                ?: findNakedTriple()
                ?: findHiddenSubset(size = 3, technique = LogicalTechnique.HIDDEN_TRIPLE)
                ?: findXWing()

        fun apply(step: TeachingStep) {
            val placement = step.placement
            if (placement != null) {
                place(placement.cellIndex, placement.value)
                return
            }

            for (elimination in step.candidateEliminations) {
                if (board.valueAt(elimination.cellIndex) == SudokuBoard.EMPTY) {
                    candidates[elimination.cellIndex].remove(elimination.candidate)
                }
            }
        }

        private fun findNakedSingle(): TeachingStep? {
            for (index in candidates.indices) {
                if (board.valueAt(index) != SudokuBoard.EMPTY || candidates[index].size != 1) continue
                val value = candidates[index].single()
                return TeachingStep(
                    technique = LogicalTechnique.NAKED_SINGLE,
                    sourceCells = listOf(index),
                    sourceUnit = null,
                    targetCells = listOf(index),
                    placement = TeachingPlacement(index, value),
                )
            }
            return null
        }

        private fun findHiddenSingle(): TeachingStep? {
            for ((unitRef, unit) in allUnitsWithRefs()) {
                for (value in 1..9) {
                    val matching = unit.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (matching.size != 1) continue
                    val target = matching.single()
                    return TeachingStep(
                        technique = LogicalTechnique.HIDDEN_SINGLE,
                        sourceCells = unit.filter { board.valueAt(it) == SudokuBoard.EMPTY },
                        sourceUnit = unitRef,
                        targetCells = listOf(target),
                        placement = TeachingPlacement(target, value),
                    )
                }
            }
            return null
        }

        private fun findNakedPair(): TeachingStep? {
            for ((unitRef, unit) in allUnitsWithRefs()) {
                val groupedPairs = unit
                    .filter { board.valueAt(it) == SudokuBoard.EMPTY && candidates[it].size == 2 }
                    .groupBy { candidates[it].toSortedSet() }
                    .toSortedMap(compareBy { it.joinToString(separator = "") })

                for ((pair, pairCells) in groupedPairs) {
                    if (pairCells.size != 2) continue
                    val eliminations = buildList {
                        for (index in unit) {
                            if (index in pairCells || board.valueAt(index) != SudokuBoard.EMPTY) continue
                            for (value in pair) {
                                if (value in candidates[index]) add(CandidateElimination(index, value))
                            }
                        }
                    }
                    if (eliminations.isNotEmpty()) {
                        return TeachingStep(
                            technique = LogicalTechnique.NAKED_PAIR,
                            sourceCells = pairCells.sorted(),
                            sourceUnit = unitRef,
                            targetCells = eliminations.map { it.cellIndex }.distinct().sorted(),
                            candidateEliminations = eliminations.sortedWith(compareBy({ it.cellIndex }, { it.candidate })),
                        )
                    }
                }
            }
            return null
        }

        private fun findPointingPairOrTriple(): TeachingStep? {
            for (box in 0 until SudokuBoard.SIZE) {
                val boxCells = boxIndices(box)
                val boxSet = boxCells.toSet()
                for (value in 1..9) {
                    val sourceCells = boxCells.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (sourceCells.size !in 2..3) continue

                    val rows = sourceCells.map { it / SudokuBoard.SIZE }.toSet()
                    if (rows.size == 1) {
                        val eliminations = rowIndices(rows.single())
                            .filter { it !in boxSet && board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                            .map { CandidateElimination(it, value) }
                        if (eliminations.isNotEmpty()) {
                            return TeachingStep(
                                technique = LogicalTechnique.POINTING_PAIR_OR_TRIPLE,
                                sourceCells = sourceCells.sorted(),
                                sourceUnit = SudokuUnitRef(SudokuUnitType.BOX, box),
                                targetCells = eliminations.map { it.cellIndex },
                                candidateEliminations = eliminations,
                            )
                        }
                    }

                    val columns = sourceCells.map { it % SudokuBoard.SIZE }.toSet()
                    if (columns.size == 1) {
                        val eliminations = columnIndices(columns.single())
                            .filter { it !in boxSet && board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                            .map { CandidateElimination(it, value) }
                        if (eliminations.isNotEmpty()) {
                            return TeachingStep(
                                technique = LogicalTechnique.POINTING_PAIR_OR_TRIPLE,
                                sourceCells = sourceCells.sorted(),
                                sourceUnit = SudokuUnitRef(SudokuUnitType.BOX, box),
                                targetCells = eliminations.map { it.cellIndex },
                                candidateEliminations = eliminations,
                            )
                        }
                    }
                }
            }
            return null
        }

        private fun findBoxLineReduction(): TeachingStep? {
            for (row in 0 until SudokuBoard.SIZE) {
                findBoxLineReductionInUnit(
                    unitRef = SudokuUnitRef(SudokuUnitType.ROW, row),
                    unit = rowIndices(row),
                )?.let { return it }
            }
            for (column in 0 until SudokuBoard.SIZE) {
                findBoxLineReductionInUnit(
                    unitRef = SudokuUnitRef(SudokuUnitType.COLUMN, column),
                    unit = columnIndices(column),
                )?.let { return it }
            }
            return null
        }

        private fun findBoxLineReductionInUnit(
            unitRef: SudokuUnitRef,
            unit: List<Int>,
        ): TeachingStep? {
            for (value in 1..9) {
                val sourceCells = unit.filter { index ->
                    board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                }
                if (sourceCells.size < 2) continue
                val boxes = sourceCells.map(::boxIndex).toSet()
                if (boxes.size != 1) continue

                val sourceSet = sourceCells.toSet()
                val eliminations = boxIndices(boxes.single())
                    .filter { index ->
                        index !in sourceSet && board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    .map { CandidateElimination(it, value) }
                if (eliminations.isEmpty()) continue

                return TeachingStep(
                    technique = LogicalTechnique.BOX_LINE_REDUCTION,
                    sourceCells = sourceCells.sorted(),
                    sourceUnit = unitRef,
                    targetCells = eliminations.map { it.cellIndex },
                    candidateEliminations = eliminations,
                )
            }
            return null
        }

        private fun findHiddenSubset(size: Int, technique: LogicalTechnique): TeachingStep? {
            for ((unitRef, unit) in allUnitsWithRefs()) {
                val candidateCellsByValue = (1..9).associateWith { value ->
                    unit.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }.toSet()
                }

                for (values in combinations((1..9).toList(), size)) {
                    val sourceSets = values.map { candidateCellsByValue.getValue(it) }
                    if (sourceSets.any { it.size !in 2..size }) continue
                    val sourceCells = sourceSets.flatten().toSortedSet()
                    if (sourceCells.size != size) continue

                    val allowed = values.toSet()
                    val eliminations = buildList {
                        for (cell in sourceCells) {
                            for (candidate in candidates[cell].sorted()) {
                                if (candidate !in allowed) add(CandidateElimination(cell, candidate))
                            }
                        }
                    }
                    if (eliminations.isEmpty()) continue

                    return TeachingStep(
                        technique = technique,
                        sourceCells = sourceCells.toList(),
                        sourceUnit = unitRef,
                        targetCells = eliminations.map { it.cellIndex }.distinct().sorted(),
                        candidateEliminations = eliminations,
                    )
                }
            }
            return null
        }

        private fun findNakedTriple(): TeachingStep? {
            for ((unitRef, unit) in allUnitsWithRefs()) {
                val eligibleCells = unit.filter { index ->
                    board.valueAt(index) == SudokuBoard.EMPTY && candidates[index].size in 2..3
                }
                for (cells in combinations(eligibleCells, 3)) {
                    val union = cells.flatMap { candidates[it] }.toSortedSet()
                    if (union.size != 3) continue

                    val sourceSet = cells.toSet()
                    val eliminations = buildList {
                        for (index in unit) {
                            if (index in sourceSet || board.valueAt(index) != SudokuBoard.EMPTY) continue
                            for (value in union) {
                                if (value in candidates[index]) add(CandidateElimination(index, value))
                            }
                        }
                    }
                    if (eliminations.isEmpty()) continue

                    return TeachingStep(
                        technique = LogicalTechnique.NAKED_TRIPLE,
                        sourceCells = cells.sorted(),
                        sourceUnit = unitRef,
                        targetCells = eliminations.map { it.cellIndex }.distinct().sorted(),
                        candidateEliminations = eliminations.sortedWith(compareBy({ it.cellIndex }, { it.candidate })),
                    )
                }
            }
            return null
        }

        private fun findXWing(): TeachingStep? {
            for (value in 1..9) {
                findXWingByRows(value)?.let { return it }
                findXWingByColumns(value)?.let { return it }
            }
            return null
        }

        private fun findXWingByRows(value: Int): TeachingStep? {
            val rowPatterns = (0 until SudokuBoard.SIZE).mapNotNull { row ->
                val columns = rowIndices(row)
                    .filter { board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                    .map { it % SudokuBoard.SIZE }
                columns.takeIf { it.size == 2 }?.let { row to it }
            }

            for (pair in combinations(rowPatterns, 2)) {
                val (firstRow, firstColumns) = pair[0]
                val (secondRow, secondColumns) = pair[1]
                if (firstColumns != secondColumns) continue

                val sourceCells = listOf(
                    firstRow * SudokuBoard.SIZE + firstColumns[0],
                    firstRow * SudokuBoard.SIZE + firstColumns[1],
                    secondRow * SudokuBoard.SIZE + firstColumns[0],
                    secondRow * SudokuBoard.SIZE + firstColumns[1],
                ).sorted()
                val sourceRows = setOf(firstRow, secondRow)
                val eliminations = buildList {
                    for (column in firstColumns) {
                        for (index in columnIndices(column)) {
                            if (index / SudokuBoard.SIZE in sourceRows) continue
                            if (board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]) {
                                add(CandidateElimination(index, value))
                            }
                        }
                    }
                }.sortedWith(compareBy({ it.cellIndex }, { it.candidate }))
                if (eliminations.isEmpty()) continue

                return TeachingStep(
                    technique = LogicalTechnique.X_WING,
                    sourceCells = sourceCells,
                    sourceUnit = null,
                    targetCells = eliminations.map { it.cellIndex }.distinct().sorted(),
                    candidateEliminations = eliminations,
                )
            }
            return null
        }

        private fun findXWingByColumns(value: Int): TeachingStep? {
            val columnPatterns = (0 until SudokuBoard.SIZE).mapNotNull { column ->
                val rows = columnIndices(column)
                    .filter { board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                    .map { it / SudokuBoard.SIZE }
                rows.takeIf { it.size == 2 }?.let { column to it }
            }

            for (pair in combinations(columnPatterns, 2)) {
                val (firstColumn, firstRows) = pair[0]
                val (secondColumn, secondRows) = pair[1]
                if (firstRows != secondRows) continue

                val sourceCells = listOf(
                    firstRows[0] * SudokuBoard.SIZE + firstColumn,
                    firstRows[1] * SudokuBoard.SIZE + firstColumn,
                    firstRows[0] * SudokuBoard.SIZE + secondColumn,
                    firstRows[1] * SudokuBoard.SIZE + secondColumn,
                ).sorted()
                val sourceColumns = setOf(firstColumn, secondColumn)
                val eliminations = buildList {
                    for (row in firstRows) {
                        for (index in rowIndices(row)) {
                            if (index % SudokuBoard.SIZE in sourceColumns) continue
                            if (board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]) {
                                add(CandidateElimination(index, value))
                            }
                        }
                    }
                }.sortedWith(compareBy({ it.cellIndex }, { it.candidate }))
                if (eliminations.isEmpty()) continue

                return TeachingStep(
                    technique = LogicalTechnique.X_WING,
                    sourceCells = sourceCells,
                    sourceUnit = null,
                    targetCells = eliminations.map { it.cellIndex }.distinct().sorted(),
                    candidateEliminations = eliminations,
                )
            }
            return null
        }

        private fun place(index: Int, value: Int) {
            require(board.valueAt(index) == SudokuBoard.EMPTY)
            require(value in candidates[index])
            board = board.withValue(index, value)
            candidates[index].clear()
            peers(index).forEach { peer -> candidates[peer].remove(value) }
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
        const val MAX_STEPS = 10_000
    }
}

internal fun allUnitsWithRefs(): List<Pair<SudokuUnitRef, List<Int>>> = buildList {
    repeat(SudokuBoard.SIZE) { row -> add(SudokuUnitRef(SudokuUnitType.ROW, row) to rowIndices(row)) }
    repeat(SudokuBoard.SIZE) { column -> add(SudokuUnitRef(SudokuUnitType.COLUMN, column) to columnIndices(column)) }
    repeat(SudokuBoard.SIZE) { box -> add(SudokuUnitRef(SudokuUnitType.BOX, box) to boxIndices(box)) }
}

internal fun rowIndices(row: Int): List<Int> =
    (0 until SudokuBoard.SIZE).map { column -> row * SudokuBoard.SIZE + column }

internal fun columnIndices(column: Int): List<Int> =
    (0 until SudokuBoard.SIZE).map { row -> row * SudokuBoard.SIZE + column }

internal fun boxIndices(box: Int): List<Int> {
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

internal fun boxIndex(index: Int): Int {
    val row = index / SudokuBoard.SIZE
    val column = index % SudokuBoard.SIZE
    return (row / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE + (column / SudokuBoard.BOX_SIZE)
}

internal fun <T> combinations(items: List<T>, size: Int): List<List<T>> {
    if (size < 0 || size > items.size) return emptyList()
    if (size == 0) return listOf(emptyList())

    val result = mutableListOf<List<T>>()
    fun collect(start: Int, selected: MutableList<T>) {
        if (selected.size == size) {
            result += selected.toList()
            return
        }
        val remainingNeeded = size - selected.size
        val lastStart = items.size - remainingNeeded
        for (index in start..lastStart) {
            selected += items[index]
            collect(index + 1, selected)
            selected.removeAt(selected.lastIndex)
        }
    }
    collect(0, mutableListOf())
    return result
}
