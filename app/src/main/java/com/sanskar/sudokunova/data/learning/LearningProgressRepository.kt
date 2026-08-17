package com.sanskar.sudokunova.data.learning

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sanskar.sudokunova.engine.LogicalTechnique
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.learningProgressDataStore by preferencesDataStore(name = "sudokunova_learning_progress")

data class TechniqueLearningProgress(
    val technique: LogicalTechnique,
    val hintViews: Int = 0,
    val practiceAttempts: Int = 0,
    val correctPracticeActions: Int = 0,
    val completedSteps: Int = 0,
    val completedSessions: Int = 0,
    val lastPracticedAtEpochMillis: Long? = null,
) {
    init {
        require(hintViews >= 0)
        require(practiceAttempts >= 0)
        require(correctPracticeActions in 0..practiceAttempts)
        require(completedSteps >= 0)
        require(completedSessions >= 0)
        require(lastPracticedAtEpochMillis == null || lastPracticedAtEpochMillis >= 0L)
    }

    val accuracyPercent: Int?
        get() = if (practiceAttempts == 0) null else (correctPracticeActions * 100L / practiceAttempts).toInt()

    val hasPractice: Boolean
        get() = practiceAttempts > 0 || completedSteps > 0 || completedSessions > 0
}

data class LearningProgressSnapshot(
    val techniques: Map<LogicalTechnique, TechniqueLearningProgress>,
) {
    init {
        require(LogicalTechnique.entries.all { it in techniques })
    }

    operator fun get(technique: LogicalTechnique): TechniqueLearningProgress =
        requireNotNull(techniques[technique])

    val totalHintViews: Int
        get() = techniques.values.sumOf { it.hintViews }

    val totalPracticeAttempts: Int
        get() = techniques.values.sumOf { it.practiceAttempts }

    val totalCompletedSteps: Int
        get() = techniques.values.sumOf { it.completedSteps }

    companion object {
        fun empty(): LearningProgressSnapshot = LearningProgressSnapshot(
            LogicalTechnique.entries.associateWith { TechniqueLearningProgress(it) },
        )
    }
}

class LearningProgressRepository(
    private val context: Context,
) {
    val progress: Flow<LearningProgressSnapshot> = data.map { preferences ->
        LearningProgressSnapshot(
            LogicalTechnique.entries.associateWith { technique ->
                TechniqueLearningProgress(
                    technique = technique,
                    hintViews = preferences[key(technique, Metric.HINT_VIEWS)] ?: 0,
                    practiceAttempts = preferences[key(technique, Metric.PRACTICE_ATTEMPTS)] ?: 0,
                    correctPracticeActions = preferences[key(technique, Metric.CORRECT_ACTIONS)] ?: 0,
                    completedSteps = preferences[key(technique, Metric.COMPLETED_STEPS)] ?: 0,
                    completedSessions = preferences[key(technique, Metric.COMPLETED_SESSIONS)] ?: 0,
                    lastPracticedAtEpochMillis = preferences[lastPracticedKey(technique)],
                )
            },
        )
    }

    suspend fun recordHintViewed(technique: LogicalTechnique) {
        context.learningProgressDataStore.edit { preferences ->
            preferences.increment(key(technique, Metric.HINT_VIEWS))
        }
    }

    suspend fun recordPracticeAnswer(
        technique: LogicalTechnique,
        correct: Boolean,
        practicedAtEpochMillis: Long = System.currentTimeMillis(),
    ) {
        require(practicedAtEpochMillis >= 0L)
        context.learningProgressDataStore.edit { preferences ->
            preferences.increment(key(technique, Metric.PRACTICE_ATTEMPTS))
            if (correct) preferences.increment(key(technique, Metric.CORRECT_ACTIONS))
            preferences[lastPracticedKey(technique)] = practicedAtEpochMillis
        }
    }

    suspend fun recordStepCompleted(
        technique: LogicalTechnique,
        practicedAtEpochMillis: Long = System.currentTimeMillis(),
    ) {
        require(practicedAtEpochMillis >= 0L)
        context.learningProgressDataStore.edit { preferences ->
            preferences.increment(key(technique, Metric.COMPLETED_STEPS))
            preferences[lastPracticedKey(technique)] = practicedAtEpochMillis
        }
    }

    suspend fun recordSessionCompleted(
        techniques: Set<LogicalTechnique>,
        practicedAtEpochMillis: Long = System.currentTimeMillis(),
    ) {
        require(practicedAtEpochMillis >= 0L)
        if (techniques.isEmpty()) return
        context.learningProgressDataStore.edit { preferences ->
            techniques.forEach { technique ->
                preferences.increment(key(technique, Metric.COMPLETED_SESSIONS))
                preferences[lastPracticedKey(technique)] = practicedAtEpochMillis
            }
        }
    }

    suspend fun reset() {
        context.learningProgressDataStore.edit { it.clear() }
    }

    private val data: Flow<Preferences>
        get() = context.learningProgressDataStore.data.catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }

    private enum class Metric(val suffix: String) {
        HINT_VIEWS("hint_views"),
        PRACTICE_ATTEMPTS("practice_attempts"),
        CORRECT_ACTIONS("correct_actions"),
        COMPLETED_STEPS("completed_steps"),
        COMPLETED_SESSIONS("completed_sessions"),
    }

    private companion object {
        fun key(technique: LogicalTechnique, metric: Metric) =
            intPreferencesKey("${technique.name.lowercase()}_${metric.suffix}")

        fun lastPracticedKey(technique: LogicalTechnique) =
            longPreferencesKey("${technique.name.lowercase()}_last_practiced")

        fun Preferences.MutablePreferences.increment(key: Preferences.Key<Int>) {
            this[key] = ((this[key] ?: 0).toLong() + 1L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        }
    }
}
