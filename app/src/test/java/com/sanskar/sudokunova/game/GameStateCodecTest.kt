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

        val corrupt = mutableEncodedState().also { it[5] = "-5" }.joinToString("|")
        assertNull(GameStateCodec.decode(corrupt))
    }

    @Test
    fun corruptedSolutionThatDoesNotMatchOriginalCluesIsRejected() {
        val permutedSolution = solution().toPuzzleString().map { digit ->
            when (digit) {
                '1' -> '2'
                '2' -> '1'
                else -> digit
            }
        }.joinToString("")
        val parts = mutableEncodedState().also { it[2] = permutedSolution }

        assertNull(GameStateCodec.decode(parts.joinToString("|")))
    }

    @Test
    fun currentBoardCannotModifyAnOriginalClue() {
        val tamperedBoard = puzzle().withValue(0, 4).toPuzzleString()
        val parts = mutableEncodedState().also { it[3] = tamperedBoard }

        assertNull(GameStateCodec.decode(parts.joinToString("|")))
    }

    @Test
    fun extremePersistedCountersAreRejectedBeforeTheyCanOverflowLiveState() {
        val elapsed = mutableEncodedState().also { it[8] = Long.MAX_VALUE.toString() }
        val mistakes = mutableEncodedState().also { it[9] = Int.MAX_VALUE.toString() }
        val hints = mutableEncodedState().also { it[10] = Int.MAX_VALUE.toString() }

        assertNull(GameStateCodec.decode(elapsed.joinToString("|")))
        assertNull(GameStateCodec.decode(mistakes.joinToString("|")))
        assertNull(GameStateCodec.decode(hints.joinToString("|")))
    }

    @Test
    fun unsupportedChallengeMetadataIsRejected() {
        val unknownType = mutableEncodedState().also {
            it[17] = "MONTHLY"
            it[18] = "202608"
        }
        val invalidKey = mutableEncodedState().also {
            it[17] = ChallengeType.DAILY.name
            it[18] = "0"
        }

        assertNull(GameStateCodec.decode(unknownType.joinToString("|")))
        assertNull(GameStateCodec.decode(invalidKey.joinToString("|")))
    }

    private fun mutableEncodedState(): MutableList<String> = GameStateCodec.encode(
        GameState(
            puzzle = puzzle(),
            solution = solution(),
            board = puzzle(),
        ),
    ).split('|').toMutableList()

    private fun puzzle() = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )

    private fun solution() = SudokuBoard.parse(
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
    )
}
