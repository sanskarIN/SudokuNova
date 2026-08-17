package com.sanskar.sudokunova.data.challenge

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sanskar.sudokunova.data.history.SudokuNovaDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChallengeResultDaoTest {
    private lateinit var database: SudokuNovaDatabase
    private lateinit var dao: ChallengeResultDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SudokuNovaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.challengeResultDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun dailyCompletionCanBeStoredAndObserved() = runBlocking {
        val id = dao.insert(result(ChallengeType.DAILY, 20_000L))

        assertNotEquals(-1L, id)
        assertEquals(52L, dao.get(ChallengeType.DAILY.name, 20_000L)?.elapsedSeconds)
        assertEquals(1, dao.observeAll().first().size)
    }

    @Test
    fun challengeTypeAndKeyRemainUnique() = runBlocking {
        val first = dao.insert(result(ChallengeType.WEEKLY, 202633L))
        val duplicate = dao.insert(result(ChallengeType.WEEKLY, 202633L).copy(elapsedSeconds = 99L))

        assertNotEquals(-1L, first)
        assertEquals(-1L, duplicate)
        assertEquals(52L, dao.get(ChallengeType.WEEKLY.name, 202633L)?.elapsedSeconds)
    }

    @Test
    fun dailyAndWeeklyMayShareNumericKey() = runBlocking {
        dao.insert(result(ChallengeType.DAILY, 123L))
        dao.insert(result(ChallengeType.WEEKLY, 123L))

        assertEquals(2, dao.observeAll().first().size)
    }

    private fun result(type: ChallengeType, key: Long) = ChallengeResultEntity(
        challengeType = type.name,
        challengeKey = key,
        difficulty = if (type == ChallengeType.DAILY) "MEDIUM" else "HARD",
        puzzle = "0".repeat(81),
        elapsedSeconds = 52L,
        mistakes = 0,
        hintsUsed = 0,
        completedAtEpochMillis = 1_700_000_000_000L,
        perfect = true,
    )
}
