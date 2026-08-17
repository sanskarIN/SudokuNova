package com.sanskar.sudokunova.ui.learn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.AppPreferencesRepository
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.data.learning.LearningProgressRepository
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.HintEngine
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.engine.PracticeAction
import com.sanskar.sudokunova.engine.PracticeSubmissionResult
import com.sanskar.sudokunova.engine.SudokuGenerator
import com.sanskar.sudokunova.engine.SudokuSolver
import com.sanskar.sudokunova.engine.TeachingHintSequence
import com.sanskar.sudokunova.engine.TeachingPracticeState
import com.sanskar.sudokunova.game.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface GuidedPracticeUiState {
    data object Loading : GuidedPracticeUiState
    data class Ready(
        val game: GameState,
        val sequence: TeachingHintSequence,
        val practice: TeachingPracticeState,
        val completed: Boolean = false,
        val lastAnswerCorrect: Boolean? = null,
    ) : GuidedPracticeUiState
    data class Error(val message: String) : GuidedPracticeUiState
}

class GuidedPracticeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val preferences = AppPreferencesRepository(application.applicationContext)
    private val progressRepository = LearningProgressRepository(application.applicationContext)
    private val solver = SudokuSolver()
    private val generator = SudokuGenerator(solver)
    private val hintEngine = HintEngine(solver)

    private val _uiState = MutableStateFlow<GuidedPracticeUiState>(GuidedPracticeUiState.Loading)
    val uiState: StateFlow<GuidedPracticeUiState> = _uiState.asStateFlow()

    val settings: StateFlow<UserSettings> = preferences.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettings(),
    )

    private var lessonIndex = 0

    init {
        loadLesson()
    }

    fun selectCell(index: Int) {
        val ready = _uiState.value as? GuidedPracticeUiState.Ready ?: return
        if (index !in 0 until 81 || ready.completed) return
        _uiState.value = ready.copy(game = ready.game.copy(selectedIndex = index), lastAnswerCorrect = null)
    }

    fun submitValue(value: Int) {
        if (value !in 1..9) return
        val ready = _uiState.value as? GuidedPracticeUiState.Ready ?: return
        if (ready.completed) return

        val finalStep = ready.sequence.steps.last()
        val technique = finalStep.technique
        val action = PracticeAction.Place(ready.game.selectedIndex, value)
        val submission = ready.practice.submit(action)
        val correct = submission.result != PracticeSubmissionResult.INCORRECT

        viewModelScope.launch {
            progressRepository.recordPracticeAnswer(technique, correct)
            if (correct) {
                progressRepository.recordStepCompleted(technique)
                progressRepository.recordSessionCompleted(ready.sequence.techniques.toSet())
            }
        }

        if (!correct) {
            _uiState.value = ready.copy(lastAnswerCorrect = false)
            return
        }

        val placement = ready.sequence.placement
        _uiState.value = ready.copy(
            game = ready.game.copy(
                board = ready.game.board.withValue(placement.cellIndex, placement.value),
                selectedIndex = placement.cellIndex,
                selectedNumber = placement.value,
            ),
            practice = submission.state,
            completed = true,
            lastAnswerCorrect = true,
        )
    }

    fun nextLesson() {
        lessonIndex++
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            _uiState.value = GuidedPracticeUiState.Loading
            val lesson = withContext(Dispatchers.Default) { createLesson(lessonIndex) }
            _uiState.value = lesson ?: GuidedPracticeUiState.Error("No suitable deterministic practice puzzle found.")
        }
    }

    private fun createLesson(index: Int): GuidedPracticeUiState.Ready? {
        repeat(MAX_SEED_ATTEMPTS) { offset ->
            val seed = BASE_SEED + index * SEED_STRIDE + offset
            val difficulty = if ((index + offset) % 2 == 0) Difficulty.MEDIUM else Difficulty.HARD
            val generated = generator.generate(difficulty, seed.toLong())
            val sequence = hintEngine.nextTeachingHint(generated.puzzle) ?: return@repeat
            val placementStep = sequence.steps.last()
            if (placementStep.technique !in PLACEMENT_TECHNIQUES) return@repeat
            val initialSelection = generated.puzzle.emptyIndices().firstOrNull()
                ?: sequence.placement.cellIndex

            return GuidedPracticeUiState.Ready(
                game = GameState.fromGenerated(generated).copy(selectedIndex = initialSelection),
                sequence = sequence,
                practice = TeachingPracticeState.start(listOf(placementStep)),
            )
        }
        return null
    }

    private companion object {
        const val BASE_SEED = 80_800
        const val SEED_STRIDE = 101
        const val MAX_SEED_ATTEMPTS = 12
        val PLACEMENT_TECHNIQUES = setOf(
            LogicalTechnique.NAKED_SINGLE,
            LogicalTechnique.HIDDEN_SINGLE,
        )
    }
}
