package com.sanskar.sudokunova.ui.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.engine.SudokuSolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class CustomPuzzleMessage {
    ENTER_CLUES,
    CHANGED,
    CONTRADICTION,
    NEED_MORE_CLUES,
    VALIDATING,
    NO_SOLUTION,
    UNIQUE_READY,
    MULTIPLE_SOLUTIONS,
    SOLVING,
    NO_SOLUTION_AVAILABLE,
    SOLVED_PREVIEW,
}

data class CustomPuzzleUiState(
    val board: SudokuBoard = SudokuBoard.empty(),
    val selectedIndex: Int = 0,
    val message: CustomPuzzleMessage = CustomPuzzleMessage.ENTER_CLUES,
    val solution: SudokuBoard? = null,
    val isUnique: Boolean = false,
    val showSolution: Boolean = false,
) {
    val displayedBoard: SudokuBoard
        get() = if (showSolution) solution ?: board else board
}

class CustomPuzzleViewModel : ViewModel() {
    private val solver = SudokuSolver()
    private val _uiState = MutableStateFlow(CustomPuzzleUiState())
    val uiState: StateFlow<CustomPuzzleUiState> = _uiState.asStateFlow()
    private var solverJob: Job? = null

    fun select(index: Int) {
        _uiState.value = _uiState.value.copy(selectedIndex = index.coerceIn(0, 80))
    }

    fun input(value: Int) {
        if (value !in 1..9) return
        cancelSolverWork()
        val state = _uiState.value
        _uiState.value = state.copy(
            board = state.board.withValue(state.selectedIndex, value),
            solution = null,
            isUnique = false,
            showSolution = false,
            message = CustomPuzzleMessage.CHANGED,
        )
    }

    fun erase() {
        cancelSolverWork()
        val state = _uiState.value
        _uiState.value = state.copy(
            board = state.board.withValue(state.selectedIndex, SudokuBoard.EMPTY),
            solution = null,
            isUnique = false,
            showSolution = false,
            message = CustomPuzzleMessage.CHANGED,
        )
    }

    fun clear() {
        cancelSolverWork()
        _uiState.value = CustomPuzzleUiState()
    }

    fun validate() {
        val state = _uiState.value.copy(showSolution = false)
        if (!state.board.isValid()) {
            cancelSolverWork()
            _uiState.value = state.copy(
                solution = null,
                isUnique = false,
                message = CustomPuzzleMessage.CONTRADICTION,
            )
            return
        }
        if (state.board.clueCount < 17) {
            cancelSolverWork()
            _uiState.value = state.copy(
                solution = null,
                isUnique = false,
                message = CustomPuzzleMessage.NEED_MORE_CLUES,
            )
            return
        }

        val requestedBoard = state.board
        solverJob?.cancel()
        _uiState.value = state.copy(
            solution = null,
            isUnique = false,
            message = CustomPuzzleMessage.VALIDATING,
        )
        solverJob = viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                solver.analyze(requestedBoard, solutionLimit = 2)
            }
            val current = _uiState.value
            if (current.board != requestedBoard) return@launch

            _uiState.value = when (result.solutionCount) {
                0 -> current.copy(
                    solution = null,
                    isUnique = false,
                    showSolution = false,
                    message = CustomPuzzleMessage.NO_SOLUTION,
                )
                1 -> current.copy(
                    solution = result.solution,
                    isUnique = true,
                    showSolution = false,
                    message = CustomPuzzleMessage.UNIQUE_READY,
                )
                else -> current.copy(
                    solution = result.solution,
                    isUnique = false,
                    showSolution = false,
                    message = CustomPuzzleMessage.MULTIPLE_SOLUTIONS,
                )
            }
            solverJob = null
        }
    }

    fun showSolution() {
        val state = _uiState.value
        state.solution?.let { solution ->
            _uiState.value = state.copy(
                solution = solution,
                showSolution = true,
                message = CustomPuzzleMessage.SOLVED_PREVIEW,
            )
            return
        }

        val requestedBoard = state.board
        solverJob?.cancel()
        _uiState.value = state.copy(
            showSolution = false,
            message = CustomPuzzleMessage.SOLVING,
        )
        solverJob = viewModelScope.launch {
            val solution = withContext(Dispatchers.Default) {
                solver.solve(requestedBoard).solution
            }
            val current = _uiState.value
            if (current.board != requestedBoard) return@launch

            _uiState.value = if (solution == null) {
                current.copy(
                    solution = null,
                    showSolution = false,
                    message = CustomPuzzleMessage.NO_SOLUTION_AVAILABLE,
                )
            } else {
                current.copy(
                    solution = solution,
                    showSolution = true,
                    message = CustomPuzzleMessage.SOLVED_PREVIEW,
                )
            }
            solverJob = null
        }
    }

    private fun cancelSolverWork() {
        solverJob?.cancel()
        solverJob = null
    }
}
