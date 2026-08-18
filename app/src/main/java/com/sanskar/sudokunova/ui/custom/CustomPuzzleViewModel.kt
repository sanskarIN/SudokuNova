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

data class CustomPuzzleUiState(
    val board: SudokuBoard = SudokuBoard.empty(),
    val selectedIndex: Int = 0,
    val message: String = "Enter the given clues, then validate the puzzle.",
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
            message = "Puzzle changed. Validate again before playing.",
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
            message = "Puzzle changed. Validate again before playing.",
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
                message = "The clues contain a row, column, or box contradiction.",
            )
            return
        }
        if (state.board.clueCount < 17) {
            cancelSolverWork()
            _uiState.value = state.copy(
                solution = null,
                isUnique = false,
                message = "Add more clues before uniqueness validation. Standard 9×9 Sudoku needs at least 17 clues for a unique puzzle.",
            )
            return
        }

        val requestedBoard = state.board
        solverJob?.cancel()
        _uiState.value = state.copy(
            solution = null,
            isUnique = false,
            message = "Validating puzzle…",
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
                    message = "This puzzle has no valid solution.",
                )
                1 -> current.copy(
                    solution = result.solution,
                    isUnique = true,
                    showSolution = false,
                    message = "Valid puzzle with exactly one solution. Ready to play.",
                )
                else -> current.copy(
                    solution = result.solution,
                    isUnique = false,
                    showSolution = false,
                    message = "This puzzle has multiple solutions. Add more clues for a unique Sudoku.",
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
                message = "Solved-grid preview. The original puzzle is preserved for Play puzzle.",
            )
            return
        }

        val requestedBoard = state.board
        solverJob?.cancel()
        _uiState.value = state.copy(
            showSolution = false,
            message = "Solving puzzle…",
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
                    message = "No solution is available for this puzzle.",
                )
            } else {
                current.copy(
                    solution = solution,
                    showSolution = true,
                    message = "Solved-grid preview. The original puzzle is preserved for Play puzzle.",
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
