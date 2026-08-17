package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.SudokuBoard

object GameStateIntegrity {
    fun isValid(state: GameState): Boolean {
        if (!state.puzzle.isValid()) return false
        if (!state.solution.isValid() || !state.solution.isComplete) return false
        if (state.selectedIndex !in 0 until SudokuBoard.CELL_COUNT) return false
        if (state.selectedNumber != null && state.selectedNumber !in 1..9) return false
        if (state.notes.size != SudokuBoard.CELL_COUNT) return false
        if (state.notes.any { candidates -> candidates.any { it !in 1..9 } }) return false
        if (state.elapsedSeconds < 0 || state.mistakes < 0 || state.hintsUsed < 0) return false

        for (index in 0 until SudokuBoard.CELL_COUNT) {
            val clue = state.puzzle.valueAt(index)
            if (clue != SudokuBoard.EMPTY) {
                if (state.solution.valueAt(index) != clue) return false
                if (state.board.valueAt(index) != clue) return false
                if (state.notes[index].isNotEmpty()) return false
            }
        }

        if (state.status == GameStatus.COMPLETED && state.board != state.solution) return false
        return true
    }
}
