package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateCodecCorruptionTest {
    private val puzzle =
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
    private val solution =
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179"
    private val emptyNotes = List(SudokuBoard.CELL_COUNT) { "" }.joinToString("/")

    @Test
    fun rejectsOutOfRangeSelectedIndex() {
        val encoded = versionTwoPayload(
            board = puzzle,
            selectedIndex = "999",
        )

        assertNull(GameStateCodec.decode(encoded))
    }

    @Test
    fun rejectsSaveThatMutatesOriginalClue() {
        val corruptedBoard = "9" + puzzle.drop(1)
        val encoded = versionTwoPayload(
            board = corruptedBoard,
            selectedIndex = "0",
        )

        assertNull(GameStateCodec.decode(encoded))
    }

    @Test
    fun rejectsCompletedStatusForIncompleteBoard() {
        val encoded = versionTwoPayload(
            board = puzzle,
            selectedIndex = "0",
            status = GameStatus.COMPLETED.name,
        )

        assertNull(GameStateCodec.decode(encoded))
    }

    private fun versionTwoPayload(
        board: String,
        selectedIndex: String,
        status: String = GameStatus.PLAYING.name,
    ): String = listOf(
        "2",
        puzzle,
        solution,
        board,
        emptyNotes,
        selectedIndex,
        "",
        "false",
        "10",
        "0",
        "0",
        Difficulty.EASY.name,
        "42",
        "false",
        status,
        "false",
    ).joinToString("|")
}
