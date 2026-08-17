package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateCodecTest {
    @Test
    fun roundTripPreservesReplayAndChallengeState() {
        val state = GameState(
            puzzle = puzzle(),
            solution = solution(),
            board = puzzle().withValue(2, 4),
            notes = List(SudokuBoard.CELL_COUNT) { index ->
                if (index == 3) setOf(2, 6, 8) else emptySet()
            },
            selectedIndex = 3,
            selectedNumber = 8,
            notesMode = true,
            elapsedSeconds = 93,
            mistakes = 1,
            hintsUsed = 2,
            difficulty = Difficulty.HARD,
            seed = 42L,
            isPaused = true,
            isDailyChallenge = false,
            replayOfHistoryId = 44L,
            challengeType = ChallengeType.WEEKLY.name,
            challengeKey = 202633L,
        )

        assertEquals(state, GameStateCodec.decode(GameStateCodec.encode(state)))
    }

    @Test
    fun versionOneSaveMigratesWithoutNewMetadata() {
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

        val restored = GameStateCodec.decode(encoded)

        assertEquals(2, restored?.selectedIndex)
        assertNull(restored?.selectedNumber)
        assertNull(restored?.replayOfHistoryId)
        assertNull(restored?.challengeType)
        assertNull(restored?.challengeKey)
    }

    @Test
    fun versionThreeReplaySaveMigratesWithoutChallengeMetadata() {
        val notes = List(SudokuBoard.CELL_COUNT) { "" }.joinToString("/")
        val encoded = listOf(
            "3",
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
            "false",
            "77",
        ).joinToString("|")

        val restored = GameStateCodec.decode(encoded)

        assertEquals(8, restored?.selectedNumber)
        assertEquals(77L, restored?.replayOfHistoryId)
        assertNull(restored?.challengeType)
        assertNull(restored?.challengeKey)
    }

    @Test
    fun malformedStateIsRejected() {
        assertNull(GameStateCodec.decode("not-a-valid-save"))

        val corrupt = GameStateCodec.encode(GameState(puzzle(), solution(), puzzle()))
            .split('|')
            .toMutableList()
            .also { it[5] = "-5" }
            .joinToString("|")
        assertNull(GameStateCodec.decode(corrupt))
    }

    private fun puzzle() = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )

    private fun solution() = SudokuBoard.parse(
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
    )
}
