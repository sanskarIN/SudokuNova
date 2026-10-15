package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SharedGameSnapshotCodecTest {
    private val board = "0".repeat(81)
    private val puzzleCode =
        "SNP1.HARD.530070000600195000098000060800060003400803001700020006060000280000419005000080079.8B59A2D7"

    @Test
    fun roundTripIsDeterministicAndSortsNotes() {
        val snapshot = SharedGameSnapshot(
            difficulty = Difficulty.HARD,
            seed = 42L,
            board = board,
            notes = mapOf(
                40 to setOf(9, 1, 5),
                3 to setOf(7, 2),
            ),
            selectedIndex = 40,
            notesMode = true,
        )

        val encoded = SharedGameSnapshotCodec.encode(snapshot)

        assertEquals(
            "SNG1|HARD|42|${board}|40|1|3:27,40:159",
            encoded,
        )
        assertEquals(snapshot, SharedGameSnapshotCodec.decode(encoded))
        assertEquals(encoded, SharedGameSnapshotCodec.encode(SharedGameSnapshotCodec.decode(encoded)!!))
    }

    @Test
    fun roundTripSupportsImportedPuzzleProvenance() {
        val snapshot = SharedGameSnapshot(
            difficulty = Difficulty.HARD,
            seed = 0L,
            board = board,
            notes = emptyMap(),
            selectedIndex = null,
            notesMode = false,
            sourceCode = puzzleCode,
        )

        val encoded = SharedGameSnapshotCodec.encode(snapshot)

        assertEquals("SNG2|HARD|0|${board}|-1|0||$puzzleCode", encoded)
        assertEquals(snapshot, SharedGameSnapshotCodec.decode(encoded))
        assertEquals(encoded, SharedGameSnapshotCodec.encode(SharedGameSnapshotCodec.decode(encoded)!!))
    }

    @Test
    fun roundTripSupportsNoSelectionAndNoNotes() {
        val snapshot = SharedGameSnapshot(
            difficulty = Difficulty.BEGINNER,
            seed = -99L,
            board = board,
            notes = emptyMap(),
            selectedIndex = null,
            notesMode = false,
        )

        val encoded = SharedGameSnapshotCodec.encode(snapshot)

        assertEquals(snapshot, SharedGameSnapshotCodec.decode(encoded))
    }

    @Test
    fun decodeSupportsLegacySng1AndRejectsUnsupportedVersions() {
        val valid = "SNG1|MEDIUM|1|${board}|-1|0|"

        assertEquals(null, SharedGameSnapshotCodec.decode(valid.replaceFirst("SNG1", "SNG3")))
        assertEquals(valid, SharedGameSnapshotCodec.encode(SharedGameSnapshotCodec.decode(valid)!!))
        assertNull(SharedGameSnapshotCodec.decode(valid.replace("MEDIUM", "UNKNOWN")))
        assertNull(SharedGameSnapshotCodec.decode(valid.replace("|-1|", "|81|")))
        assertNull(SharedGameSnapshotCodec.decode(valid.replace("|0|", "|2|")))
        assertNull(SharedGameSnapshotCodec.decode("SNG1|MEDIUM|not-a-seed|${board}|-1|0|"))
        assertNull(SharedGameSnapshotCodec.decode("SNG1|MEDIUM|1|short|-1|0|"))
    }

    @Test
    fun decodeRejectsMalformedNotesAndSourceCode() {
        val prefix = "SNG1|MEDIUM|1|${board}|-1|0|"

        assertNull(SharedGameSnapshotCodec.decode(prefix + "81:1"))
        assertNull(SharedGameSnapshotCodec.decode(prefix + "3:"))
        assertNull(SharedGameSnapshotCodec.decode(prefix + "3:0"))
        assertNull(SharedGameSnapshotCodec.decode(prefix + "3:11"))
        assertNull(SharedGameSnapshotCodec.decode(prefix + "3:1,3:2"))
        assertNull(SharedGameSnapshotCodec.decode(prefix + "3:1,broken"))
        assertNull(SharedGameSnapshotCodec.decode("SNG2|MEDIUM|0|${board}|-1|0||"))
    }

    @Test
    fun encodeFailsClosedForInvalidSnapshots() {
        val valid = SharedGameSnapshot(
            difficulty = Difficulty.MEDIUM,
            seed = 1L,
            board = board,
            notes = emptyMap(),
            selectedIndex = null,
            notesMode = false,
        )

        assertFailsWith<IllegalArgumentException> {
            SharedGameSnapshotCodec.encode(valid.copy(board = "0".repeat(80)))
        }
        assertFailsWith<IllegalArgumentException> {
            SharedGameSnapshotCodec.encode(valid.copy(selectedIndex = 81))
        }
        assertFailsWith<IllegalArgumentException> {
            SharedGameSnapshotCodec.encode(valid.copy(notes = mapOf(0 to emptySet())))
        }
        assertFailsWith<IllegalArgumentException> {
            SharedGameSnapshotCodec.encode(valid.copy(notes = mapOf(0 to setOf(10))))
        }
        assertFailsWith<IllegalArgumentException> {
            SharedGameSnapshotCodec.encode(valid.copy(sourceCode = ""))
        }
        assertFailsWith<IllegalArgumentException> {
            SharedGameSnapshotCodec.encode(valid.copy(sourceCode = "bad|code"))
        }
    }

    @Test
    fun decodeRejectsOversizedPayloadBeforeParsing() {
        assertNull(SharedGameSnapshotCodec.decode("x".repeat(2049)))
    }
}
