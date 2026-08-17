package com.sanskar.sudokunova.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.AppPreferencesRepository
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.data.challenge.ChallengeDescriptor
import com.sanskar.sudokunova.data.challenge.ChallengeKeys
import com.sanskar.sudokunova.data.challenge.ChallengeRepository
import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.HintEngine
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.engine.SudokuGenerator
import com.sanskar.sudokunova.engine.SudokuHint
import com.sanskar.sudokunova.engine.SudokuSolver
import com.sanskar.sudokunova.game.GameState
import com.sanskar.sudokunova.game.GameStateCodec
import com.sanskar.sudokunova.game.GameStatus
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

sealed interface GameScreenState {
    data object Loading : GameScreenState
    data class Ready(val game: GameState) : GameScreenState
    data class Error(val message: String) : GameScreenState
}

class GameViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val repository = AppPreferencesRepository(application.applicationContext)
    private val challengeRepository = ChallengeRepository(application.applicationContext)
    private val solver = SudokuSolver()
    private val generator = SudokuGenerator(solver)
    private val hintEngine = HintEngine(solver)

    private val requestedDifficulty = runCatching {
        Difficulty.valueOf(savedStateHandle.get<String>("difficulty") ?: Difficulty.EASY.name)
    }.getOrDefault(Difficulty.EASY)
    private val legacyDailyChallenge = savedStateHandle.get<Boolean>("daily") ?: false
    private val requestedChallengeType = savedStateHandle.get<String>("challengeType")
        ?.takeIf(String::isNotBlank)
        ?.let { runCatching { ChallengeType.valueOf(it) }.getOrNull() }
        ?: if (legacyDailyChallenge) ChallengeType.DAILY else null
    private val requestedChallengeKey = savedStateHandle.get<Long>("challengeKey")
        ?.takeIf { it != Long.MIN_VALUE }
    private val resumeRequested = savedStateHandle.get<Boolean>("resume") ?: false
    private val customPuzzle = savedStateHandle.get<String>("custom")
        ?.takeIf { it.length == SudokuBoard.CELL_COUNT }

    private val _uiState = MutableStateFlow<GameScreenState>(GameScreenState.Loading)
    val uiState: StateFlow<GameScreenState> = _uiState.asStateFlow()

    private val _pendingHint = MutableStateFlow<SudokuHint?>(null)
    val pendingHint: StateFlow<SudokuHint?> = _pendingHint.asStateFlow()

    val settings = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettings(),
    )

    private val undoStack = ArrayDeque<GameState>()
    private val redoStack = ArrayDeque<GameState>()
    private var timerJob: Job? = null
    private var completionRecorded = false

    init {
        loadOrCreateGame()
        startTimer()
    }

    fun selectCell(index: Int) {
        if (index !in 0 until SudokuBoard.CELL_COUNT) return
        mutateGame(save = false) { state -> state.copy(selectedIndex = index) }
    }

    fun selectNumber(value: Int?) {
        if (value != null && value !in 1..9) return
        mutateGame(save = false) { state -> state.copy(selectedNumber = value) }
    }

    fun toggleNotesMode() {
        mutateGame { state -> state.copy(notesMode = !state.notesMode) }
    }

    fun enterNumber(value: Int) {
        if (value !in 1..9) return
        val state = currentGame() ?: return
        val index = state.selectedIndex
        if (state.isPaused || state.status != GameStatus.PLAYING || state.isOriginal(index)) return

        if (state.notesMode) {
            pushUndo(state)
            val notes = state.notes.toMutableList()
            val updated = notes[index].toMutableSet().apply {
                if (!add(value)) remove(value)
            }
            notes[index] = updated
            setGame(state.copy(notes = notes, selectedNumber = value))
            return
        }

        placeValue(state.copy(selectedNumber = value), index, value, countMistake = true)
    }

    fun erase() {
        val state = currentGame() ?: return
        val index = state.selectedIndex
        if (state.isPaused || state.status != GameStatus.PLAYING || state.isOriginal(index)) return
        if (state.board.valueAt(index) == SudokuBoard.EMPTY && state.notes[index].isEmpty()) return

        pushUndo(state)
        val notes = state.notes.toMutableList().also { it[index] = emptySet() }
        setGame(
            state.copy(
                board = state.board.withValue(index, SudokuBoard.EMPTY),
                notes = notes,
            ),
        )
    }

    fun undo() {
        val current = currentGame() ?: return
        val previous = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(current)
        setGame(previous, save = true, clearRedo = false)
    }

    fun redo() {
        val current = currentGame() ?: return
        val next = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(current)
        setGame(next, save = true, clearRedo = false)
    }

    fun requestHint() {
        val state = currentGame() ?: return
        if (state.isPaused || state.status != GameStatus.PLAYING) return
        val hint = hintEngine.nextHint(state.board)
        _pendingHint.value = hint
        if (hint != null) {
            mutateGame(save = false) { it.copy(selectedIndex = hint.cellIndex) }
        }
    }

    fun dismissHint() {
        _pendingHint.value = null
    }

    fun applyHint() {
        val hint = _pendingHint.value ?: return
        val state = currentGame() ?: return
        if (state.isOriginal(hint.cellIndex) || state.status != GameStatus.PLAYING) return
        pushUndo(state)
        _pendingHint.value = null
        placeValue(
            state = state.copy(hintsUsed = state.hintsUsed + 1),
            index = hint.cellIndex,
            value = hint.value,
            countMistake = false,
            alreadyAddedToUndo = true,
        )
    }

    fun togglePause() {
        mutateGame { state ->
            if (state.status == GameStatus.PLAYING) state.copy(isPaused = !state.isPaused) else state
        }
    }

    fun restart() {
        val state = currentGame() ?: return
        pushUndo(state)
        completionRecorded = false
        setGame(
            state.copy(
                board = state.puzzle,
                notes = List(SudokuBoard.CELL_COUNT) { emptySet() },
                selectedIndex = 0,
                selectedNumber = null,
                elapsedSeconds = 0,
                mistakes = 0,
                hintsUsed = 0,
                isPaused = false,
                status = GameStatus.PLAYING,
            ),
        )
    }

    fun abandon() {
        val state = currentGame() ?: return
        viewModelScope.launch {
            repository.recordGameAbandoned()
            repository.clearActiveGame()
        }
        _uiState.value = GameScreenState.Error("Game ended. Start a new puzzle from Home.")
        undoStack.clear()
        redoStack.clear()
        _pendingHint.value = null
        if (state.status == GameStatus.COMPLETED) completionRecorded = true
    }

    private fun loadOrCreateGame() {
        viewModelScope.launch {
            _uiState.value = GameScreenState.Loading
            val restored = if (resumeRequested) {
                repository.activeGame.first()?.let(GameStateCodec::decode)
            } else {
                null
            }

            if (restored != null && restored.status == GameStatus.PLAYING) {
                val resumed = restored.copy(isPaused = false)
                _uiState.value = GameScreenState.Ready(resumed)
                persist(resumed)
                return@launch
            }

            runCatching {
                if (customPuzzle != null) {
                    createCustomGame(customPuzzle)
                } else if (requestedChallengeType != null) {
                    createChallengeGame(requestedChallengeType)
                } else {
                    val seed = Random.nextLong()
                    val generated = withContext(Dispatchers.Default) {
                        generator.generate(requestedDifficulty, seed)
                    }
                    GameState.fromGenerated(generated)
                }
            }.onSuccess { game ->
                repository.recordGameStarted()
                _uiState.value = GameScreenState.Ready(game)
                persist(game)
            }.onFailure { error ->
                _uiState.value = GameScreenState.Error(error.message ?: "Puzzle generation failed.")
            }
        }
    }

    private suspend fun createChallengeGame(type: ChallengeType): GameState {
        val key = requestedChallengeKey ?: when (type) {
            ChallengeType.DAILY -> ChallengeKeys.daily(LocalDate.now())
            ChallengeType.WEEKLY -> ChallengeKeys.weekly(LocalDate.now())
        }
        val descriptor = ChallengeDescriptor(
            type = type,
            key = key,
            difficulty = ChallengeRepository.difficultyFor(type),
        )
        val generated = withContext(Dispatchers.Default) {
            generator.generate(descriptor.difficulty, ChallengeKeys.seed(descriptor))
        }
        return GameState.fromGenerated(
            generated = generated,
            dailyChallenge = type == ChallengeType.DAILY,
            challengeType = type.name,
            challengeKey = key,
        )
    }

    private suspend fun createCustomGame(encodedPuzzle: String): GameState = withContext(Dispatchers.Default) {
        val puzzle = SudokuBoard.parse(encodedPuzzle)
        require(puzzle.isValid()) { "The custom puzzle contains contradictory clues." }
        val analysis = solver.analyze(puzzle, solutionLimit = 2)
        require(analysis.solutionCount == 1 && analysis.solution != null) {
            "The custom puzzle must have exactly one solution before it can be played."
        }
        GameState(
            puzzle = puzzle,
            solution = requireNotNull(analysis.solution),
            board = puzzle,
            difficulty = requestedDifficulty,
            seed = 0L,
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                val state = currentGame() ?: continue
                if (state.status == GameStatus.PLAYING && !state.isPaused) {
                    val updated = state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                    _uiState.value = GameScreenState.Ready(updated)
                    if (updated.elapsedSeconds % 5L == 0L) persist(updated)
                }
            }
        }
    }

    private fun placeValue(
        state: GameState,
        index: Int,
        value: Int,
        countMistake: Boolean,
        alreadyAddedToUndo: Boolean = false,
    ) {
        if (!alreadyAddedToUndo) pushUndo(state)

        val settingsValue = settings.value
        val isWrong = value != state.solution.valueAt(index)
        val mistakes = if (countMistake && settingsValue.autoCheckMistakes && isWrong) {
            state.mistakes + 1
        } else {
            state.mistakes
        }

        var notes = state.notes.toMutableList().also { it[index] = emptySet() }
        val newBoard = state.board.withValue(index, value)
        if (!isWrong && settingsValue.autoRemoveNotes) {
            notes = removePeerNote(notes, index, value)
        }

        val reachedMistakeLimit = settingsValue.mistakeLimit > 0 && mistakes >= settingsValue.mistakeLimit
        val completed = newBoard == state.solution
        val newStatus = when {
            completed -> GameStatus.COMPLETED
            reachedMistakeLimit -> GameStatus.FAILED
            else -> GameStatus.PLAYING
        }

        val updated = state.copy(
            board = newBoard,
            notes = notes,
            selectedIndex = index,
            mistakes = mistakes,
            status = newStatus,
            isPaused = newStatus != GameStatus.PLAYING,
        )
        setGame(updated)

        if (completed) recordCompletion(updated)
    }

    private fun removePeerNote(
        notes: MutableList<Set<Int>>,
        index: Int,
        value: Int,
    ): MutableList<Set<Int>> {
        val row = index / 9
        val column = index % 9
        val boxRow = (row / 3) * 3
        val boxColumn = (column / 3) * 3

        for (candidateIndex in 0 until SudokuBoard.CELL_COUNT) {
            val candidateRow = candidateIndex / 9
            val candidateColumn = candidateIndex % 9
            val sameRow = candidateRow == row
            val sameColumn = candidateColumn == column
            val sameBox = candidateRow in boxRow until boxRow + 3 &&
                candidateColumn in boxColumn until boxColumn + 3
            if (sameRow || sameColumn || sameBox) {
                notes[candidateIndex] = notes[candidateIndex] - value
            }
        }
        return notes
    }

    private fun recordCompletion(state: GameState) {
        if (completionRecorded) return
        completionRecorded = true
        viewModelScope.launch {
            repository.recordGameCompleted(
                elapsedSeconds = state.elapsedSeconds,
                mistakes = state.mistakes,
                hintsUsed = state.hintsUsed,
                completedEpochDay = LocalDate.now().toEpochDay(),
            )
            if (state.challengeType != null && state.challengeKey != null) {
                challengeRepository.recordCompletion(state)
            }
            repository.clearActiveGame()
        }
    }

    private fun currentGame(): GameState? = (_uiState.value as? GameScreenState.Ready)?.game

    private fun mutateGame(
        save: Boolean = true,
        transform: (GameState) -> GameState,
    ) {
        val current = currentGame() ?: return
        setGame(transform(current), save = save)
    }

    private fun pushUndo(state: GameState) {
        undoStack.addLast(state)
        while (undoStack.size > MAX_UNDO_STEPS) undoStack.removeFirst()
        redoStack.clear()
    }

    private fun setGame(
        state: GameState,
        save: Boolean = true,
        clearRedo: Boolean = true,
    ) {
        _uiState.value = GameScreenState.Ready(state)
        if (clearRedo) redoStack.clear()
        if (save && state.status != GameStatus.COMPLETED) persist(state)
    }

    private fun persist(state: GameState) {
        viewModelScope.launch {
            repository.saveActiveGame(GameStateCodec.encode(state))
        }
    }

    private companion object {
        const val MAX_UNDO_STEPS = 200
    }
}
