package com.sanskar.sudokunova.data.history

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sanskar.sudokunova.engine.Difficulty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReplayHistoryDatabaseTest {
    private lateinit var database: SudokuNovaDatabase
    private lateinit var dao: GameHistoryDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SudokuNovaDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.gameHistoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun replayRowsRemainVisibleButDoNotInflateNormalSummary() = runBlocking {
        val normalId = dao.insert(history(replayOfHistoryId = null, elapsedSeconds = 100))
        val replayId = dao.insert(history(replayOfHistoryId = normalId, elapsedSeconds = 70))

        assertEquals(2, dao.observeAll().first().size)
        assertEquals(normalId, dao.getById(replayId)?.replayOfHistoryId)

        val summary = dao.observeDifficultySummaries().first().single()
        assertEquals(1L, summary.games)
        assertEquals(100L, summary.bestSeconds)
        assertEquals(100.0, summary.averageSeconds, 0.001)
    }

    private fun history(
        replayOfHistoryId: Long?,
        elapsedSeconds: Long,
    ): GameHistoryEntity = GameHistoryEntity(
        puzzle = PUZZLE,
        solution = SOLUTION,
        difficulty = Difficulty.MEDIUM.name,
        completed = true,
        elapsedSeconds = elapsedSeconds,
        mistakes = 0,
        hintsUsed = 0,
        startedAtEpochMillis = 1_000,
        completedAtEpochMillis = 1_000 + elapsedSeconds * 1_000,
        isDailyChallenge = false,
        isPerfect = true,
        replayOfHistoryId = replayOfHistoryId,
    )

    private companion object {
        const val PUZZLE =
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        const val SOLUTION =
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179"
    }
}
