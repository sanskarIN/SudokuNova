package com.sanskar.sudokunova.data.transfer

import com.sanskar.sudokunova.data.UserSettings

data class BackupHistoryRecord(
    val puzzle: String,
    val solution: String,
    val difficulty: String,
    val completed: Boolean,
    val elapsedSeconds: Long,
    val mistakes: Int,
    val hintsUsed: Int,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
    val isDailyChallenge: Boolean,
    val isPerfect: Boolean,
    val isFavorite: Boolean,
    val isReplay: Boolean = false,
)

data class BackupSavedPuzzleRecord(
    val puzzle: String,
    val solution: String?,
    val title: String?,
    val difficulty: String,
    val source: String,
    val createdAtEpochMillis: Long,
    val isFavorite: Boolean,
)

data class BackupChallengeRecord(
    val challengeType: String,
    val challengeKey: Long,
    val difficulty: String,
    val puzzle: String,
    val elapsedSeconds: Long,
    val mistakes: Int,
    val hintsUsed: Int,
    val completedAtEpochMillis: Long,
    val perfect: Boolean,
)

data class SudokuNovaBackup(
    val settings: UserSettings,
    val history: List<BackupHistoryRecord>,
    val savedPuzzles: List<BackupSavedPuzzleRecord>,
    val challengeResults: List<BackupChallengeRecord>,
)

data class BackupImportResult(
    val historyImported: Int,
    val historySkipped: Int,
    val savedPuzzlesImported: Int,
    val savedPuzzlesSkipped: Int,
    val challengesImported: Int,
    val challengesSkipped: Int,
    val settingsApplied: Boolean,
)
