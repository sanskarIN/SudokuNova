package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PuzzleCodeCodecTest {
    private val puzzle = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )

    @Test
    fun roundTripPreservesPuzzleAndDifficulty() {
        val encoded = PuzzleCodeCodec.encode(puzzle, Difficulty.HARD)
        val decoded = PuzzleCodeCodec.decode(encoded)

        assertEquals(puzzle, decoded?.puzzle)
        assertEquals(Difficulty.HARD, decoded?.difficulty)
        assertTrue(encoded.length <= PuzzleCodeCodec.MAX_CODE_LENGTH)
    }

    @Test
    fun tamperedChecksumIsRejected() {
        val encoded = PuzzleCodeCodec.encode(puzzle, Difficulty.MEDIUM)
        val tampered = encoded.dropLast(1) + if (encoded.last() == 'A') 'B' else 'A'

        assertNull(PuzzleCodeCodec.decode(tampered))
    }

    @Test
    fun oversizedUnknownAndInvalidCodesAreRejected() {
        assertNull(PuzzleCodeCodec.decode("x".repeat(PuzzleCodeCodec.MAX_CODE_LENGTH + 1)))
        assertNull(PuzzleCodeCodec.decode("SNP9.EASY.${"0".repeat(81)}.00000000"))
        assertNull(PuzzleCodeCodec.decode("SNP1.EASY.${"5".repeat(81)}.00000000"))
    }
}
