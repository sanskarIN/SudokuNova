package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReplayGameStateCodecTest {
    private val puzzle = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )
    private val solution = SudokuBoard.parse(
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
    )

    @Test
    fun replaySourceRoundTripsThroughLatestCodec() {
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle,
            difficulty = Difficulty.HARD,
            replayOfHistoryId = 44L,
        )

        val restored = GameStateCodec.decode(GameStateCodec.encode(state))

        assertEquals(44L, restored?.replayOfHistoryId)
        assertEquals(state, restored)
    }

    @Test
    fun ordinaryGameRoundTripKeepsReplaySourceNull() {
        val state = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle,
            difficulty = Difficulty.EASY,
        )

        assertNull(GameStateCodec.decode(GameStateCodec.encode(state))?.replayOfHistoryId)
    }
}
