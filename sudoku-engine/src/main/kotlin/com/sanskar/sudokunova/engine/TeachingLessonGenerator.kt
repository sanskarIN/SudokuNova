package com.sanskar.sudokunova.engine

data class GeneratedTeachingLesson(
    val puzzle: GeneratedPuzzle,
    val sequence: TeachingHintSequence,
) {
    init {
        require(sequence.steps.isNotEmpty())
        require(sequence.placement.value == puzzle.solution.valueAt(sequence.placement.cellIndex))
    }
}

class TeachingLessonGenerator(
    private val solver: SudokuSolver = SudokuSolver(),
) {
    private val generator = SudokuGenerator(solver)
    private val hintEngine = HintEngine(solver)

    fun findPlacementLesson(
        seedStart: Long,
        difficulties: List<Difficulty> = listOf(Difficulty.MEDIUM, Difficulty.HARD),
        allowedTechniques: Set<LogicalTechnique> = DEFAULT_PLACEMENT_TECHNIQUES,
        maxAttempts: Int = 12,
    ): GeneratedTeachingLesson? {
        require(difficulties.isNotEmpty())
        require(allowedTechniques.isNotEmpty())
        require(maxAttempts > 0)

        repeat(maxAttempts) { offset ->
            val difficulty = difficulties[offset % difficulties.size]
            val generated = generator.generate(difficulty, seedStart + offset)
            val sequence = hintEngine.nextTeachingHint(generated.puzzle) ?: return@repeat
            if (sequence.steps.last().technique !in allowedTechniques) return@repeat
            return GeneratedTeachingLesson(generated, sequence)
        }
        return null
    }

    companion object {
        val DEFAULT_PLACEMENT_TECHNIQUES: Set<LogicalTechnique> = setOf(
            LogicalTechnique.NAKED_SINGLE,
            LogicalTechnique.HIDDEN_SINGLE,
        )
    }
}
