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
