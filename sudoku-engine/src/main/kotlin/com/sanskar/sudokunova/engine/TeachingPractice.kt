package com.sanskar.sudokunova.engine

data class TeachingPracticeExercise(
    val id: String,
    val step: TeachingStep,
    val choices: List<LogicalTechnique>,
) {
    init {
        require(id.isNotBlank()) { "Practice exercise id must not be blank." }
        require(choices.size >= 2) { "Practice exercise must offer at least two choices." }
        require(choices.distinct().size == choices.size) { "Practice choices must be unique." }
        require(step.technique in choices) { "Correct technique must be included in practice choices." }
    }

    fun isCorrect(answer: LogicalTechnique): Boolean = answer == step.technique
}

object TeachingPracticeCatalog {
    val exercises: List<TeachingPracticeExercise> = listOf(
        placementExercise(
            id = "naked-single-01",
            technique = LogicalTechnique.NAKED_SINGLE,
            sourceCells = listOf(40),
            targetCell = 40,
            value = 5,
        ),
        placementExercise(
            id = "hidden-single-01",
            technique = LogicalTechnique.HIDDEN_SINGLE,
            sourceCells = rowIndices(4),
            sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 4),
            targetCell = 43,
            value = 7,
        ),
        eliminationExercise(
            id = "naked-pair-01",
            technique = LogicalTechnique.NAKED_PAIR,
            sourceCells = listOf(18, 20),
            sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 2),
            eliminations = listOf(
                CandidateElimination(19, 2),
                CandidateElimination(19, 8),
                CandidateElimination(24, 8),
            ),
        ),
        eliminationExercise(
            id = "pointing-01",
            technique = LogicalTechnique.POINTING_PAIR_OR_TRIPLE,
            sourceCells = listOf(9, 11),
            sourceUnit = SudokuUnitRef(SudokuUnitType.BOX, 0),
            eliminations = listOf(
                CandidateElimination(12, 4),
                CandidateElimination(16, 4),
            ),
        ),
        eliminationExercise(
            id = "box-line-01",
            technique = LogicalTechnique.BOX_LINE_REDUCTION,
            sourceCells = listOf(27, 28),
            sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 3),
            eliminations = listOf(
                CandidateElimination(36, 6),
                CandidateElimination(37, 6),
            ),
        ),
        eliminationExercise(
            id = "hidden-pair-01",
            technique = LogicalTechnique.HIDDEN_PAIR,
            sourceCells = listOf(30, 32),
            sourceUnit = SudokuUnitRef(SudokuUnitType.BOX, 4),
            eliminations = listOf(
                CandidateElimination(30, 1),
                CandidateElimination(32, 9),
            ),
        ),
        eliminationExercise(
            id = "naked-triple-01",
            technique = LogicalTechnique.NAKED_TRIPLE,
            sourceCells = listOf(54, 55, 57),
            sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 6),
            eliminations = listOf(
                CandidateElimination(58, 2),
                CandidateElimination(58, 3),
                CandidateElimination(61, 7),
            ),
        ),
        eliminationExercise(
            id = "hidden-triple-01",
            technique = LogicalTechnique.HIDDEN_TRIPLE,
            sourceCells = listOf(5, 14, 23),
            sourceUnit = SudokuUnitRef(SudokuUnitType.COLUMN, 5),
            eliminations = listOf(
                CandidateElimination(5, 4),
                CandidateElimination(14, 8),
                CandidateElimination(23, 9),
            ),
        ),
        eliminationExercise(
            id = "x-wing-01",
            technique = LogicalTechnique.X_WING,
            sourceCells = listOf(1, 7, 28, 34),
            sourceUnit = null,
            eliminations = listOf(
                CandidateElimination(46, 5),
                CandidateElimination(52, 5),
                CandidateElimination(64, 5),
                CandidateElimination(70, 5),
            ),
        ),
    )

    fun forTechnique(technique: LogicalTechnique): List<TeachingPracticeExercise> =
        exercises.filter { it.step.technique == technique }

    fun exerciseFor(technique: LogicalTechnique, attemptIndex: Int): TeachingPracticeExercise? {
        require(attemptIndex >= 0) { "Practice attempt index must be non-negative." }
        val matching = forTechnique(technique)
        if (matching.isEmpty()) return null
        return matching[attemptIndex % matching.size]
    }

    private fun placementExercise(
        id: String,
        technique: LogicalTechnique,
        sourceCells: List<Int>,
        sourceUnit: SudokuUnitRef? = null,
        targetCell: Int,
        value: Int,
    ): TeachingPracticeExercise = TeachingPracticeExercise(
        id = id,
        step = TeachingStep(
            technique = technique,
            sourceCells = sourceCells,
            sourceUnit = sourceUnit,
            targetCells = listOf(targetCell),
            placement = TeachingPlacement(targetCell, value),
        ),
        choices = choicesFor(technique),
    )

    private fun eliminationExercise(
        id: String,
        technique: LogicalTechnique,
        sourceCells: List<Int>,
        sourceUnit: SudokuUnitRef?,
        eliminations: List<CandidateElimination>,
    ): TeachingPracticeExercise = TeachingPracticeExercise(
        id = id,
        step = TeachingStep(
            technique = technique,
            sourceCells = sourceCells,
            sourceUnit = sourceUnit,
            targetCells = eliminations.map { it.cellIndex }.distinct(),
            candidateEliminations = eliminations,
        ),
        choices = choicesFor(technique),
    )

    private fun choicesFor(correct: LogicalTechnique): List<LogicalTechnique> {
        val ordered = LogicalTechnique.entries
        val index = ordered.indexOf(correct)
        val choices = linkedSetOf(correct)
        var offset = 1
        while (choices.size < 4 && choices.size < ordered.size) {
            choices += ordered[(index + offset) % ordered.size]
            offset++
        }
        return choices.toList().sortedBy { it.rank }
    }
}
