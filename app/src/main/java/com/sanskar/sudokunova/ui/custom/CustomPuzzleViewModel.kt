package com.sanskar.sudokunova.ui.custom

import androidx.lifecycle.ViewModel
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.engine.SudokuSolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    fun select(index: Int) {
        _uiState.value = _uiState.value.copy(selectedIndex = index.coerceIn(0, 80))
    }

    fun input(value: Int) {
        if (value !in 1..9) return
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
        _uiState.value = CustomPuzzleUiState()
    }

    fun validate() {
        val state = _uiState.value.copy(showSolution = false)
        if (!state.board.isValid()) {
            _uiState.value = state.copy(
                solution = null,
                isUnique = false,
                message = "The clues contain a row, column, or box contradiction.",
            )
            return
        }
        if (state.board.clueCount < 17) {
            _uiState.value = state.copy(
                solution = null,
                isUnique = false,
                message = "Add more clues before uniqueness validation. Standard 9×9 Sudoku needs at least 17 clues for a unique puzzle.",
            )
            return
        }

        val result = solver.analyze(state.board, solutionLimit = 2)
        _uiState.value = when (result.solutionCount) {
            0 -> state.copy(solution = null, isUnique = false, message = "This puzzle has no valid solution.")
            1 -> state.copy(solution = result.solution, isUnique = true, message = "Valid puzzle with exactly one solution. Ready to play.")
            else -> state.copy(solution = result.solution, isUnique = false, message = "This puzzle has multiple solutions. Add more clues for a unique Sudoku.")
        }
    }

    fun showSolution() {
        val state = _uiState.value
        val solution = state.solution ?: solver.solve(state.board).solution
        _uiState.value = if (solution == null) {
            state.copy(showSolution = false, message = "No solution is available for this puzzle.")
        } else {
            state.copy(
                solution = solution,
                showSolution = true,
                message = "Solved-grid preview. The original puzzle is preserved for Play puzzle.",
            )
        }
    }
}
