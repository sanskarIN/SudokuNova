package com.sanskar.sudokunova.engine

data class TeachingHintSequence(
    val steps: List<TeachingStep>,
) {
    init {
        require(steps.isNotEmpty()) { "A teaching hint sequence must contain at least one step." }
        require(steps.last().placement != null) { "A teaching hint sequence must end in a placement." }
        require(steps.dropLast(1).none(TeachingStep::isPlacement)) {
            "A teaching hint sequence must stop at its first placement."
        }
    }

    val placement: TeachingPlacement
        get() = requireNotNull(steps.last().placement)

    val prerequisiteEliminations: List<TeachingStep>
        get() = steps.dropLast(1)

    val techniques: List<LogicalTechnique>
        get() = steps.map(TeachingStep::technique)
}

fun LogicalTeachingEngine.nextPlacementSequence(
    input: SudokuBoard,
    maxSteps: Int = 64,
): TeachingHintSequence? {
    require(maxSteps > 0) { "maxSteps must be positive." }
    val trace = trace(input, maxSteps = maxSteps)
    val placementIndex = trace.steps.indexOfFirst(TeachingStep::isPlacement)
    if (placementIndex < 0) return null
    return TeachingHintSequence(trace.steps.take(placementIndex + 1))
}
