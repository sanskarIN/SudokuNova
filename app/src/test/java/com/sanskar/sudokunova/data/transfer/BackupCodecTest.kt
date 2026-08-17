package com.sanskar.sudokunova.data.transfer

import com.sanskar.sudokunova.data.InputMode
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecTest {
    private val puzzle =
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
    private val solution =
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179"

    @Test
    fun roundTripPreservesSupportedBackupData() {
        val backup = sampleBackup()
        val encoded = BackupCodec.encode(backup)
        val decoded = BackupCodec.decode(encoded)

        assertEquals(backup, decoded)
        assertTrue(encoded.toByteArray().size <= BackupCodec.MAX_BACKUP_BYTES)
    }

    @Test
    fun checksumTamperingIsRejectedBeforeImport() {
        val encoded = BackupCodec.encode(sampleBackup())
        val tampered = encoded.replace("MEDIUM", "HARD", ignoreCase = false)

        assertNull(BackupCodec.decode(tampered))
    }

    @Test
    fun oversizedAndUnknownRecordTypesAreRejected() {
        assertNull(BackupCodec.decode("x".repeat(BackupCodec.MAX_BACKUP_BYTES + 1)))

        val valid = BackupCodec.encode(sampleBackup())
        val lines = valid.lines().toMutableList()
        lines.add(lines.lastIndex, "X|unexpected")
        assertNull(BackupCodec.decode(lines.joinToString("\n")))
    }

    @Test
    fun invalidPuzzleSolutionRelationshipIsRejected() {
        val invalid = sampleBackup().copy(
            history = sampleBackup().history.map { record ->
                record.copy(solution = "634578912572196348198342567859761423426853791713924856961537284287419635345286179")
            },
        )

        runCatching { BackupCodec.encode(invalid) }
            .onSuccess { throw AssertionError("Invalid backup should not encode") }
    }

    private fun sampleBackup() = SudokuNovaBackup(
        settings = UserSettings(
            theme = AppTheme.DARK,
            dynamicColor = false,
            inputMode = InputMode.NUMBER_FIRST,
            mistakeLimit = 5,
        ),
        history = listOf(
            BackupHistoryRecord(
                puzzle = puzzle,
                solution = solution,
                difficulty = "MEDIUM",
                completed = true,
                elapsedSeconds = 123,
                mistakes = 1,
                hintsUsed = 0,
                startedAtEpochMillis = 1_700_000_000_000L,
                completedAtEpochMillis = 1_700_000_123_000L,
                isDailyChallenge = false,
                isPerfect = false,
                isFavorite = true,
                isReplay = true,
            ),
        ),
        savedPuzzles = listOf(
            BackupSavedPuzzleRecord(
                puzzle = puzzle,
                solution = solution,
                title = "Practice puzzle",
                difficulty = "HARD",
                source = "custom",
                createdAtEpochMillis = 1_700_000_000_000L,
                isFavorite = true,
            ),
        ),
        challengeResults = listOf(
            BackupChallengeRecord(
                challengeType = "DAILY",
                challengeKey = 20_000L,
                difficulty = "MEDIUM",
                puzzle = puzzle,
                elapsedSeconds = 150,
                mistakes = 0,
                hintsUsed = 0,
                completedAtEpochMillis = 1_700_000_150_000L,
                perfect = true,
            ),
        ),
    )
}
