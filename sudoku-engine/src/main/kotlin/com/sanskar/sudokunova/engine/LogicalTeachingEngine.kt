package com.sanskar.sudokunova.engine

class LogicalTeachingEngine {
    fun nextStep(input: SudokuBoard): TeachingStep? = trace(input, maxSteps = 1).steps.firstOrNull()

    fun trace(
        input: SudokuBoard,
        maxSteps: Int = MAX_STEPS,
    ): LogicalTeachingTrace {
        require(input.isValid()) { "Logical teaching requires a valid Sudoku board." }
        require(maxSteps > 0) { "maxSteps must be positive." }

        val state = CandidateState(input)
        val steps = mutableListOf<TeachingStep>()

        while (!state.board.isComplete && steps.size < maxSteps) {
            val step = state.findNextStep() ?: break
            state.apply(step)
            steps += step
        }

        return LogicalTeachingTrace(
            initialBoard = input,
            finalBoard = state.board,
            steps = steps,
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

        fun findNextStep(): TeachingStep? =
            findNakedSingle()
                ?: findHiddenSingle()
                ?: findNakedPair()
                ?: findHiddenPair()
                ?: findNakedTriple()
                ?: findHiddenTriple()
                ?: findPointingPairOrTriple()
                ?: findBoxLineReduction()

        fun apply(step: TeachingStep) {
            step.placement?.let { placement ->
                require(board.valueAt(placement.cellIndex) == SudokuBoard.EMPTY)
                require(placement.value in candidates[placement.cellIndex])
                board = board.withValue(placement.cellIndex, placement.value)
                candidates[placement.cellIndex].clear()
                peers(placement.cellIndex).forEach { peer -> candidates[peer].remove(placement.value) }
                require(board.isValid())
                return
            }

            require(step.eliminations.isNotEmpty())
            step.eliminations.forEach { elimination ->
                require(board.valueAt(elimination.cellIndex) == SudokuBoard.EMPTY)
                require(elimination.value in candidates[elimination.cellIndex])
                candidates[elimination.cellIndex].remove(elimination.value)
                require(candidates[elimination.cellIndex].isNotEmpty()) {
                    "Logical elimination removed the final candidate from cell ${elimination.cellIndex}."
                }
            }
        }

        private fun findNakedSingle(): TeachingStep? {
            for (index in candidates.indices) {
                if (board.valueAt(index) == SudokuBoard.EMPTY && candidates[index].size == 1) {
                    val value = candidates[index].single()
                    return TeachingStep(
                        technique = LogicalTechnique.NAKED_SINGLE,
                        sourceCells = setOf(index),
                        candidateValues = setOf(value),
                        placement = TeachingPlacement(index, value),
                    )
                }
            }
            return null
        }

        private fun findHiddenSingle(): TeachingStep? {
            for (descriptor in allUnitDescriptors()) {
                for (value in 1..9) {
                    val matching = descriptor.indices.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (matching.size == 1) {
                        val index = matching.single()
                        return TeachingStep(
                            technique = LogicalTechnique.HIDDEN_SINGLE,
                            sourceCells = setOf(index),
                            sourceUnit = descriptor.unit,
                            candidateValues = setOf(value),
                            placement = TeachingPlacement(index, value),
                        )
                    }
                }
            }
            return null
        }

        private fun findNakedPair(): TeachingStep? =
            findNakedSubsetStep(subsetSize = 2, technique = LogicalTechnique.NAKED_PAIR)

        private fun findHiddenPair(): TeachingStep? =
            findHiddenSubsetStep(subsetSize = 2, technique = LogicalTechnique.HIDDEN_PAIR)

        private fun findNakedTriple(): TeachingStep? =
            findNakedSubsetStep(subsetSize = 3, technique = LogicalTechnique.NAKED_TRIPLE)

        private fun findHiddenTriple(): TeachingStep? =
            findHiddenSubsetStep(subsetSize = 3, technique = LogicalTechnique.HIDDEN_TRIPLE)

        private fun findNakedSubsetStep(
            subsetSize: Int,
            technique: LogicalTechnique,
        ): TeachingStep? {
            for (descriptor in allUnitDescriptors()) {
                val snapshot = descriptor.indices.associateWith { candidates[it].toSet() }
                val match = findNakedSubset(
                    unitIndices = descriptor.indices,
                    candidates = snapshot,
                    subsetSize = subsetSize,
                ) ?: continue

                return TeachingStep(
                    technique = technique,
                    sourceCells = match.sourceCells.toSet(),
                    sourceUnit = descriptor.unit,
                    affectedUnit = descriptor.unit,
                    candidateValues = match.values,
                    eliminations = match.eliminations,
                )
            }
            return null
        }

        private fun findHiddenSubsetStep(
            subsetSize: Int,
            technique: LogicalTechnique,
        ): TeachingStep? {
            for (descriptor in allUnitDescriptors()) {
                val snapshot = descriptor.indices.associateWith { candidates[it].toSet() }
                val match = findHiddenSubset(
                    unitIndices = descriptor.indices,
                    candidates = snapshot,
                    subsetSize = subsetSize,
                ) ?: continue

                return TeachingStep(
                    technique = technique,
                    sourceCells = match.sourceCells.toSet(),
                    sourceUnit = descriptor.unit,
                    affectedUnit = descriptor.unit,
                    candidateValues = match.values,
                    eliminations = match.eliminations,
                )
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

                    val rows = sourceCells.map(::rowOf).toSet()
                    if (rows.size == 1) {
                        val row = rows.single()
                        val eliminations = rowIndices(row)
                            .filter { it !in boxSet && board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                            .map { CandidateElimination(it, value) }
                        if (eliminations.isNotEmpty()) {
                            return TeachingStep(
                                technique = LogicalTechnique.POINTING_PAIR_OR_TRIPLE,
                                sourceCells = sourceCells.toSet(),
                                sourceUnit = LogicalUnit(LogicalUnitType.BOX, box),
                                affectedUnit = LogicalUnit(LogicalUnitType.ROW, row),
                                candidateValues = setOf(value),
                                eliminations = eliminations,
                            )
                        }
                    }

                    val columns = sourceCells.map(::columnOf).toSet()
                    if (columns.size == 1) {
                        val column = columns.single()
                        val eliminations = columnIndices(column)
                            .filter { it !in boxSet && board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                            .map { CandidateElimination(it, value) }
                        if (eliminations.isNotEmpty()) {
                            return TeachingStep(
                                technique = LogicalTechnique.POINTING_PAIR_OR_TRIPLE,
                                sourceCells = sourceCells.toSet(),
                                sourceUnit = LogicalUnit(LogicalUnitType.BOX, box),
                                affectedUnit = LogicalUnit(LogicalUnitType.COLUMN, column),
                                candidateValues = setOf(value),
                                eliminations = eliminations,
                            )
                        }
                    }
                }
            }
            return null
        }

        private fun findBoxLineReduction(): TeachingStep? {
            for (row in 0 until SudokuBoard.SIZE) {
                val line = rowIndices(row)
                for (value in 1..9) {
                    val sourceCells = line.filter { index ->
                        board.valueAt(index) == SudokuBoard.EMPTY && value in candidates[index]
                    }
                    if (sourceCells.size < 2) continue
                    val boxes = sourceCells.map(::boxOf).toSet()
                    if (boxes.size != 1) continue

                    val box = boxes.single()
                    val sourceSet = sourceCells.toSet()
                    val eliminations = boxIndices(box)
                        .filter { it !in sourceSet && board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                        .map { CandidateElimination(it, value) }
                    if (eliminations.isNotEmpty()) {
                        return TeachingStep(
                            technique = LogicalTechnique.BOX_LINE_REDUCTION,
                            sourceCells = sourceSet,
                            sourceUnit = LogicalUnit(LogicalUnitType.ROW, row),
                            affectedUnit = LogicalUnit(LogicalUnitType.BOX, box),
                            candidateValues = setOf(value),
                            eliminations = eliminations,
                        )
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
                    val boxes = sourceCells.map(::boxOf).toSet()
                    if (boxes.size != 1) continue

                    val box = boxes.single()
                    val sourceSet = sourceCells.toSet()
                    val eliminations = boxIndices(box)
                        .filter { it !in sourceSet && board.valueAt(it) == SudokuBoard.EMPTY && value in candidates[it] }
                        .map { CandidateElimination(it, value) }
                    if (eliminations.isNotEmpty()) {
                        return TeachingStep(
                            technique = LogicalTechnique.BOX_LINE_REDUCTION,
                            sourceCells = sourceSet,
                            sourceUnit = LogicalUnit(LogicalUnitType.COLUMN, column),
                            affectedUnit = LogicalUnit(LogicalUnitType.BOX, box),
                            candidateValues = setOf(value),
                            eliminations = eliminations,
                        )
                    }
                }
            }
            return null
        }

        private fun peers(index: Int): Set<Int> = buildSet {
            addAll(rowIndices(rowOf(index)))
            addAll(columnIndices(columnOf(index)))
            addAll(boxIndices(boxOf(index)))
            remove(index)
        }
    }

    private companion object {
        const val MAX_STEPS = 10_000
    }
}

private data class LogicalUnitDescriptor(
    val unit: LogicalUnit,
    val indices: List<Int>,
)

private fun allUnitDescriptors(): List<LogicalUnitDescriptor> = buildList {
    repeat(SudokuBoard.SIZE) { row ->
        add(LogicalUnitDescriptor(LogicalUnit(LogicalUnitType.ROW, row), rowIndices(row)))
    }
    repeat(SudokuBoard.SIZE) { column ->
        add(LogicalUnitDescriptor(LogicalUnit(LogicalUnitType.COLUMN, column), columnIndices(column)))
    }
    repeat(SudokuBoard.SIZE) { box ->
        add(LogicalUnitDescriptor(LogicalUnit(LogicalUnitType.BOX, box), boxIndices(box)))
    }
}

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

private fun rowOf(index: Int): Int = index / SudokuBoard.SIZE

private fun columnOf(index: Int): Int = index % SudokuBoard.SIZE

private fun boxOf(index: Int): Int =
    (rowOf(index) / SudokuBoard.BOX_SIZE) * SudokuBoard.BOX_SIZE +
        (columnOf(index) / SudokuBoard.BOX_SIZE)
