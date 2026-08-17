package com.sanskar.sudokunova.data.transfer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sanskar.sudokunova.data.AppPreferencesRepository
import com.sanskar.sudokunova.data.InputMode
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.data.restoreSettings
import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.data.history.SudokuNovaDatabase
import com.sanskar.sudokunova.ui.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: SudokuNovaDatabase
    private lateinit var repository: BackupRepository

    @Before
    fun setUp() = runBlocking {
        database = SudokuNovaDatabase.get(context)
        database.gameHistoryDao().deleteAll()
        database.savedPuzzleDao().deleteAll()
        database.challengeResultDao().deleteAll()
        repository = BackupRepository(context)
    }

    @After
    fun tearDown() = runBlocking {
        database.gameHistoryDao().deleteAll()
        database.savedPuzzleDao().deleteAll()
        database.challengeResultDao().deleteAll()
        AppPreferencesRepository(context).restoreSettings(UserSettings())
    }

    @Test
    fun restoreImportsOnceAndSkipsNaturalDuplicates() = runBlocking {
        val first = repository.importBackup(sampleBackup())
        val second = repository.importBackup(
            sampleBackup().copy(
                history = sampleBackup().history.map { it.copy(isFavorite = false) },
            ),
        )

        assertEquals(1, first.historyImported)
        assertEquals(1, first.savedPuzzlesImported)
        assertEquals(1, first.challengesImported)

        assertEquals(0, second.historyImported)
        assertEquals(1, second.historySkipped)
        assertEquals(0, second.savedPuzzlesImported)
        assertEquals(1, second.savedPuzzlesSkipped)
        assertEquals(0, second.challengesImported)
        assertEquals(1, second.challengesSkipped)

        assertEquals(1, database.gameHistoryDao().observeAll().first().size)
        assertEquals(1, database.savedPuzzleDao().observeAll().first().size)
        assertEquals(1, database.challengeResultDao().observeAll().first().size)
    }

    @Test
    fun restoreAppliesValidatedSettings() = runBlocking {
        repository.importBackup(sampleBackup())
        val settings = AppPreferencesRepository(context).settings.first()

        assertEquals(AppTheme.DARK, settings.theme)
        assertEquals(InputMode.NUMBER_FIRST, settings.inputMode)
        assertEquals(false, settings.dynamicColor)
        assertEquals(5, settings.mistakeLimit)
    }

    private fun sampleBackup(): SudokuNovaBackup = SudokuNovaBackup(
        settings = UserSettings(
            theme = AppTheme.DARK,
            dynamicColor = false,
            inputMode = InputMode.NUMBER_FIRST,
            mistakeLimit = 5,
        ),
        history = listOf(
            BackupHistoryRecord(
                puzzle = PUZZLE,
                solution = SOLUTION,
                difficulty = "MEDIUM",
                completed = true,
                elapsedSeconds = 90,
                mistakes = 0,
                hintsUsed = 0,
                startedAtEpochMillis = 1_700_000_000_000L,
                completedAtEpochMillis = 1_700_000_090_000L,
                isDailyChallenge = false,
                isPerfect = true,
                isFavorite = true,
            ),
        ),
        savedPuzzles = listOf(
            BackupSavedPuzzleRecord(
                puzzle = PUZZLE,
                solution = SOLUTION,
                title = "Backup test",
                difficulty = "MEDIUM",
                source = "custom",
                createdAtEpochMillis = 1_700_000_000_000L,
                isFavorite = true,
            ),
        ),
        challengeResults = listOf(
            BackupChallengeRecord(
                challengeType = ChallengeType.DAILY.name,
                challengeKey = 20_000L,
                difficulty = "MEDIUM",
                puzzle = PUZZLE,
                elapsedSeconds = 90,
                mistakes = 0,
                hintsUsed = 0,
                completedAtEpochMillis = 1_700_000_090_000L,
                perfect = true,
            ),
        ),
    )

    private companion object {
        const val PUZZLE =
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        const val SOLUTION =
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179"
    }
}
