package com.sanskar.sudokunova.data

import com.sanskar.sudokunova.engine.LogicalTechnique

data class TechniqueLearningProgress(
    val lessonViews: Int = 0,
    val practiceAttempts: Int = 0,
    val practiceSuccesses: Int = 0,
) {
    init {
        require(lessonViews >= 0)
        require(practiceAttempts >= 0)
        require(practiceSuccesses >= 0)
        require(practiceSuccesses <= practiceAttempts)
    }

    val masteryPercent: Int
        get() {
            if (practiceAttempts == 0) return if (lessonViews > 0) 10 else 0
            val accuracy = (practiceSuccesses * 100) / practiceAttempts
            val practiceDepth = (practiceAttempts * 12).coerceAtMost(60)
            val lessonCredit = if (lessonViews > 0) 10 else 0
            return (accuracy * 30 / 100 + practiceDepth + lessonCredit).coerceIn(0, 100)
        }

    val mastered: Boolean
        get() = practiceAttempts >= 3 && practiceSuccesses >= 3 && masteryPercent >= 75
}

data class LearningProgress(
    val techniques: Map<LogicalTechnique, TechniqueLearningProgress> =
        LogicalTechnique.entries.associateWith { TechniqueLearningProgress() },
) {
    fun forTechnique(technique: LogicalTechnique): TechniqueLearningProgress =
        techniques[technique] ?: TechniqueLearningProgress()

    val totalLessonViews: Int
        get() = techniques.values.sumOf { it.lessonViews }

    val totalPracticeAttempts: Int
        get() = techniques.values.sumOf { it.practiceAttempts }

    val totalPracticeSuccesses: Int
        get() = techniques.values.sumOf { it.practiceSuccesses }

    val masteredTechniqueCount: Int
        get() = techniques.values.count { it.mastered }

    val overallMasteryPercent: Int
        get() = if (techniques.isEmpty()) 0 else techniques.values.sumOf { it.masteryPercent } / techniques.size
}
