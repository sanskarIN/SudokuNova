package com.sanskar.sudokunova.data.transfer

import android.content.Context
import androidx.room.withTransaction
import com.sanskar.sudokunova.data.AppPreferencesRepository
import com.sanskar.sudokunova.data.challenge.ChallengeResultEntity
import com.sanskar.sudokunova.data.history.GameHistoryEntity
import com.sanskar.sudokunova.data.history.SavedPuzzleEntity
import com.sanskar.sudokunova.data.history.SudokuNovaDatabase
import com.sanskar.sudokunova.data.restoreSettings
import kotlinx.coroutines.flow.first

class BackupRepository(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = AppPreferencesRepository(appContext)
    private val database = SudokuNovaDatabase.get(appContext)
    private val historyDao = database.gameHistoryDao()
    private val savedDao = database.savedPuzzleDao()
    private val challengeDao = database.challengeResultDao()

    suspend fun exportBackup(): SudokuNovaBackup = SudokuNovaBackup(
        settings = preferences.settings.first(),
        history = historyDao.observeAll().first().map { entity ->
            BackupHistoryRecord(
                puzzle = entity.puzzle,
                solution = entity.solution,
                difficulty = entity.difficulty,
                completed = entity.completed,
                elapsedSeconds = entity.elapsedSeconds,
                mistakes = entity.mistakes,
                hintsUsed = entity.hintsUsed,
                startedAtEpochMillis = entity.startedAtEpochMillis,
                completedAtEpochMillis = entity.completedAtEpochMillis,
                isDailyChallenge = entity.isDailyChallenge,
                isPerfect = entity.isPerfect,
                isFavorite = entity.isFavorite,
            )
        },
        savedPuzzles = savedDao.observeAll().first().map { entity ->
            BackupSavedPuzzleRecord(
                puzzle = entity.puzzle,
                solution = entity.solution,
                title = entity.title,
                difficulty = entity.difficulty,
                source = entity.source,
                createdAtEpochMillis = entity.createdAtEpochMillis,
                isFavorite = entity.isFavorite,
            )
        },
        challengeResults = challengeDao.observeAll().first().map { entity ->
            BackupChallengeRecord(
                challengeType = entity.challengeType,
                challengeKey = entity.challengeKey,
                difficulty = entity.difficulty,
                puzzle = entity.puzzle,
                elapsedSeconds = entity.elapsedSeconds,
                mistakes = entity.mistakes,
                hintsUsed = entity.hintsUsed,
                completedAtEpochMillis = entity.completedAtEpochMillis,
                perfect = entity.perfect,
            )
        },
    )

    suspend fun exportText(): String = BackupCodec.encode(exportBackup())

    suspend fun importText(raw: String): BackupImportResult? {
        val backup = BackupCodec.decode(raw) ?: return null
        return importBackup(backup)
    }

    suspend fun importBackup(backup: SudokuNovaBackup): BackupImportResult {
        val existingHistory = historyDao.observeAll().first().map(::historySignature).toMutableSet()

        var historyImported = 0
        var historySkipped = 0
        var savedImported = 0
        var savedSkipped = 0
        var challengesImported = 0
        var challengesSkipped = 0

        database.withTransaction {
            backup.history.forEach { record ->
                val entity = GameHistoryEntity(
                    puzzle = record.puzzle,
                    solution = record.solution,
                    difficulty = record.difficulty,
                    completed = record.completed,
                    elapsedSeconds = record.elapsedSeconds,
                    mistakes = record.mistakes,
                    hintsUsed = record.hintsUsed,
                    startedAtEpochMillis = record.startedAtEpochMillis,
                    completedAtEpochMillis = record.completedAtEpochMillis,
                    isDailyChallenge = record.isDailyChallenge,
                    isPerfect = record.isPerfect,
                    isFavorite = record.isFavorite,
                    replayOfHistoryId = null,
                )
                val signature = historySignature(entity)
                if (!existingHistory.add(signature)) {
                    historySkipped++
                } else {
                    historyDao.insert(entity)
                    historyImported++
                }
            }

            backup.savedPuzzles.forEach { record ->
                val id = savedDao.insert(
                    SavedPuzzleEntity(
                        puzzle = record.puzzle,
                        solution = record.solution,
                        title = record.title,
                        difficulty = record.difficulty,
                        source = record.source,
                        createdAtEpochMillis = record.createdAtEpochMillis,
                        isFavorite = record.isFavorite,
                    ),
                )
                if (id > 0L) savedImported++ else savedSkipped++
            }

            backup.challengeResults.forEach { record ->
                val id = challengeDao.insert(
                    ChallengeResultEntity(
                        challengeType = record.challengeType,
                        challengeKey = record.challengeKey,
                        difficulty = record.difficulty,
                        puzzle = record.puzzle,
                        elapsedSeconds = record.elapsedSeconds,
                        mistakes = record.mistakes,
                        hintsUsed = record.hintsUsed,
                        completedAtEpochMillis = record.completedAtEpochMillis,
                        perfect = record.perfect,
                    ),
                )
                if (id > 0L) challengesImported++ else challengesSkipped++
            }
        }

        preferences.restoreSettings(backup.settings)

        return BackupImportResult(
            historyImported = historyImported,
            historySkipped = historySkipped,
            savedPuzzlesImported = savedImported,
            savedPuzzlesSkipped = savedSkipped,
            challengesImported = challengesImported,
            challengesSkipped = challengesSkipped,
            settingsApplied = true,
        )
    }

    private fun historySignature(entity: GameHistoryEntity): String = listOf(
        entity.puzzle,
        entity.solution,
        entity.difficulty,
        entity.completed,
        entity.elapsedSeconds,
        entity.mistakes,
        entity.hintsUsed,
        entity.startedAtEpochMillis,
        entity.completedAtEpochMillis,
        entity.isDailyChallenge,
        entity.isPerfect,
        entity.isFavorite,
    ).joinToString("|")
}
