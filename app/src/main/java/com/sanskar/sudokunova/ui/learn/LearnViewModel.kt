package com.sanskar.sudokunova.ui.learn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.AppPreferencesRepository
import com.sanskar.sudokunova.data.LearningProgress
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.engine.TeachingPracticeCatalog
import com.sanskar.sudokunova.engine.TeachingPracticeExercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PracticeAnswerState {
    data object Unanswered : PracticeAnswerState
    data class Answered(
        val selected: LogicalTechnique,
        val correct: Boolean,
    ) : PracticeAnswerState
}

data class PracticeUiState(
    val technique: LogicalTechnique,
    val exercise: TeachingPracticeExercise,
    val answerState: PracticeAnswerState = PracticeAnswerState.Unanswered,
)

class LearnViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppPreferencesRepository(application.applicationContext)

    val progress: StateFlow<LearningProgress> = repository.learningProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LearningProgress(),
    )

    private val _practice = MutableStateFlow<PracticeUiState?>(null)
    val practice: StateFlow<PracticeUiState?> = _practice.asStateFlow()

    private val attemptIndices = mutableMapOf<LogicalTechnique, Int>()

    fun recordLessonViewed(technique: LogicalTechnique) {
        viewModelScope.launch {
            repository.recordTechniqueLessonViewed(technique)
        }
    }

    fun startPractice(technique: LogicalTechnique) {
        val attemptIndex = attemptIndices[technique] ?: 0
        val exercise = TeachingPracticeCatalog.exerciseFor(technique, attemptIndex) ?: return
        _practice.value = PracticeUiState(
            technique = technique,
            exercise = exercise,
        )
    }

    fun answerPractice(answer: LogicalTechnique) {
        val current = _practice.value ?: return
        if (current.answerState != PracticeAnswerState.Unanswered) return

        val correct = current.exercise.isCorrect(answer)
        _practice.value = current.copy(
            answerState = PracticeAnswerState.Answered(
                selected = answer,
                correct = correct,
            ),
        )
        viewModelScope.launch {
            repository.recordPracticeAttempt(current.technique, correct)
        }
    }

    fun nextPractice() {
        val current = _practice.value ?: return
        val nextIndex = (attemptIndices[current.technique] ?: 0) + 1
        attemptIndices[current.technique] = nextIndex
        val exercise = TeachingPracticeCatalog.exerciseFor(current.technique, nextIndex) ?: run {
            _practice.value = null
            return
        }
        _practice.value = PracticeUiState(
            technique = current.technique,
            exercise = exercise,
        )
    }

    fun closePractice() {
        _practice.value = null
    }

    fun resetLearningProgress() {
        viewModelScope.launch {
            repository.resetLearningProgress()
        }
    }
}
