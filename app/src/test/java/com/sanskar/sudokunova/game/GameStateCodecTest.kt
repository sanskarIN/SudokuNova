package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateCodecTest {
    @Test
    fun roundTripPreservesPlayableState() {
        val puzzle = puzzle()
        val solution = solution()
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
            challengeType = ChallengeType.DAILY.name,
            challengeKey = 20_000L,
        )

        assertEquals(state, GameStateCodec.decode(GameStateCodec.encode(state)))
    }

    @Test
    fun versionOneSaveMigratesWithoutSelectedNumber() {
        val notes = List(SudokuBoard.CELL_COUNT) { "" }.joinToString("/")
        val encoded = listOf(
            "1",
            puzzle().toPuzzleString(),
            solution().toPuzzleString(),
            puzzle().toPuzzleString(),
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
        assertNull(state?.challengeType)
        assertNull(state?.challengeKey)
    }

    @Test
    fun versionTwoSaveMigratesWithoutChallengeProvenance() {
        val notes = List(SudokuBoard.CELL_COUNT) { "" }.joinToString("/")
        val encoded = listOf(
            "2",
            puzzle().toPuzzleString(),
            solution().toPuzzleString(),
            puzzle().toPuzzleString(),
            notes,
            "4",
            "8",
            "false",
            "44",
            "1",
            "0",
            Difficulty.MEDIUM.name,
            "1234",
            "false",
            GameStatus.PLAYING.name,
            "true",
        ).joinToString("|")

        val state = GameStateCodec.decode(encoded)

        assertEquals(4, state?.selectedIndex)
        assertEquals(8, state?.selectedNumber)
        assertEquals(true, state?.isDailyChallenge)
        assertNull(state?.challengeType)
        assertNull(state?.challengeKey)
    }

    @Test
    fun challengeRoundTripPreservesWeeklyKey() {
        val state = GameState(
            puzzle = puzzle(),
            solution = solution(),
            board = puzzle(),
            difficulty = Difficulty.HARD,
            challengeType = ChallengeType.WEEKLY.name,
            challengeKey = 202633L,
        )

        val restored = GameStateCodec.decode(GameStateCodec.encode(state))

        assertEquals(ChallengeType.WEEKLY.name, restored?.challengeType)
        assertEquals(202633L, restored?.challengeKey)
    }

    @Test
    fun malformedStateIsRejected() {
        assertNull(GameStateCodec.decode("not-a-valid-save"))
    }

    private fun puzzle() = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )

    private fun solution() = SudokuBoard.parse(
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
    )
}
