package com.sanskar.sudokunova.data.transfer

import com.sanskar.sudokunova.data.UserSettings
import org.junit.Assert.assertNull
import org.junit.Test

class BackupCodecHardeningTest {
    private val puzzle =
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
    private val solution =
        "534678912672195348198342567859761423426853791713924856961537284287419635345286179"

    @Test
    fun inconsistentPerfectMetadataCannotBeEncoded() {
        val backup = SudokuNovaBackup(
            settings = UserSettings(),
            history = listOf(
                BackupHistoryRecord(
                    puzzle = puzzle,
                    solution = solution,
                    difficulty = "EASY",
                    completed = true,
                    elapsedSeconds = 10,
                    mistakes = 1,
                    hintsUsed = 0,
                    startedAtEpochMillis = 1_000,
                    completedAtEpochMillis = 2_000,
                    isDailyChallenge = false,
                    isPerfect = true,
                    isFavorite = false,
                ),
            ),
            savedPuzzles = emptyList(),
            challengeResults = emptyList(),
        )

        val result = runCatching { BackupCodec.encode(backup) }
        check(result.isFailure)
    }

    @Test
    fun negativeChallengeKeyCannotBeEncoded() {
        val backup = SudokuNovaBackup(
            settings = UserSettings(),
            history = emptyList(),
            savedPuzzles = emptyList(),
            challengeResults = listOf(
                BackupChallengeRecord(
                    challengeType = "DAILY",
                    challengeKey = -1,
                    difficulty = "MEDIUM",
                    puzzle = puzzle,
                    elapsedSeconds = 10,
                    mistakes = 0,
                    hintsUsed = 0,
                    completedAtEpochMillis = 2_000,
                    perfect = true,
                ),
            ),
        )

        val result = runCatching { BackupCodec.encode(backup) }
        check(result.isFailure)
    }

    @Test
    fun savedPuzzleTimestampBeyondSupportedRangeCannotBeEncoded() {
        val backup = SudokuNovaBackup(
            settings = UserSettings(),
            history = emptyList(),
            savedPuzzles = listOf(
                BackupSavedPuzzleRecord(
                    puzzle = puzzle,
                    solution = solution,
                    title = "Future puzzle",
                    difficulty = "EASY",
                    source = "custom",
                    createdAtEpochMillis = Long.MAX_VALUE,
                    isFavorite = false,
                ),
            ),
            challengeResults = emptyList(),
        )

        val result = runCatching { BackupCodec.encode(backup) }
        check(result.isFailure)
    }

    @Test
    fun modifiedBackupWithInvalidChronologyIsRejected() {
        val valid = SudokuNovaBackup(
            settings = UserSettings(),
            history = listOf(
                BackupHistoryRecord(
                    puzzle = puzzle,
                    solution = solution,
                    difficulty = "EASY",
                    completed = true,
                    elapsedSeconds = 10,
                    mistakes = 0,
                    hintsUsed = 0,
                    startedAtEpochMillis = 1_000,
                    completedAtEpochMillis = 2_000,
                    isDailyChallenge = false,
                    isPerfect = true,
                    isFavorite = false,
                ),
            ),
            savedPuzzles = emptyList(),
            challengeResults = emptyList(),
        )
        val encoded = BackupCodec.encode(valid)
        val tampered = encoded.replace("|1000|2000|", "|3000|2000|")

        assertNull(BackupCodec.decode(tampered))
    }
}
