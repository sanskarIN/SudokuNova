package in.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SudokuBoardTest {
    @Test
    fun parseAndSerializeRoundTrip() {
        val puzzle = "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        assertEquals(puzzle, SudokuBoard.parse(puzzle).toPuzzleString())
    }

    @Test
    fun detectsRowConflict() {
        val board = SudokuBoard.empty()
            .withValue(0, 0, 5)
            .withValue(0, 1, 5)

        assertFalse(board.isValid())
        assertTrue(board.hasConflict(0))
        assertTrue(board.hasConflict(1))
    }

    @Test
    fun calculatesCandidates() {
        val board = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )

        assertEquals(setOf(1, 2, 4), board.candidates(0, 2))
    }
}
