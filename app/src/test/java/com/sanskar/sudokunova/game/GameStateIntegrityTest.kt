package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateIntegrityTest {
    private val puzzle = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )
    private val solution = SudokuBoard.parse(
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
    )

    @Test
    fun validPlayingStatePassesIntegrityValidation() {
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle.withValue(2, 4),
            selectedIndex = 2,
            selectedNumber = 4,
            difficulty = Difficulty.MEDIUM,
        )

        assertTrue(GameStateIntegrity.isValid(state))
    }

    @Test
    fun changingOriginalClueFailsIntegrityValidation() {
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle.withValue(0, 9),
        )

        assertFalse(GameStateIntegrity.isValid(state))
    }

    @Test
    fun clueThatDisagreesWithSolutionFailsIntegrityValidation() {
        val corruptedPuzzle = puzzle.withValue(0, 4)
        val state = GameState(
            puzzle = corruptedPuzzle,
            solution = solution,
            board = corruptedPuzzle,
        )

        assertFalse(GameStateIntegrity.isValid(state))
    }

    @Test
    fun completedStateRequiresSolvedBoard() {
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle,
            status = GameStatus.COMPLETED,
            isPaused = true,
        )

        assertFalse(GameStateIntegrity.isValid(state))
    }

    @Test
    fun notesOnOriginalClueFailIntegrityValidation() {
        val notes = List(SudokuBoard.CELL_COUNT) { index ->
            if (index == 0) setOf(1, 2) else emptySet()
        }
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle,
            notes = notes,
        )

        assertFalse(GameStateIntegrity.isValid(state))
    }
}
