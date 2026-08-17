package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateCodecTest {
    @Test
    fun roundTripPreservesPlayableState() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val solution = SudokuBoard.parse(
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
        )
        val notes = List(SudokuBoard.CELL_COUNT) { index ->
            if (index == 2) setOf(1, 2, 4) else emptySet()
        }
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle.withValue(0, 2, 4),
            notes = notes,
            selectedIndex = 2,
            selectedNumber = 7,
            notesMode = true,
            elapsedSeconds = 93,
            mistakes = 1,
            hintsUsed = 2,
            difficulty = Difficulty.HARD,
            seed = 42L,
            isPaused = true,
            isDailyChallenge = true,
        )

        assertEquals(state, GameStateCodec.decode(GameStateCodec.encode(state)))
    }

    @Test
    fun versionOneSaveMigratesWithoutSelectedNumber() {
        val notes = List(SudokuBoard.CELL_COUNT) { "" }.joinToString("/")
        val encoded = listOf(
            "1",
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
            notes,
            "2",
            "false",
            "12",
            "0",
            "0",
            Difficulty.EASY.name,
            "9",
            "false",
            GameStatus.PLAYING.name,
            "false",
        ).joinToString("|")

        val state = GameStateCodec.decode(encoded)

        assertEquals(2, state?.selectedIndex)
        assertNull(state?.selectedNumber)
    }

    @Test
    fun malformedStateIsRejected() {
        assertNull(GameStateCodec.decode("not-a-valid-save"))
    }
}
