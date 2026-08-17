package com.sanskar.sudokunova.data.history

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sanskar.sudokunova.engine.Difficulty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SudokuNovaDatabaseTest {
    private lateinit var database: SudokuNovaDatabase
    private lateinit var historyDao: GameHistoryDao
    private lateinit var savedPuzzleDao: SavedPuzzleDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SudokuNovaDatabase::class.java,
        ).allowMainThreadQueries().build()
        historyDao = database.gameHistoryDao()
        savedPuzzleDao = database.savedPuzzleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun historyInsertFavoriteAndDifficultySummaryRoundTrip() = runBlocking {
        val firstId = historyDao.insert(
            history(
                difficulty = Difficulty.EASY,
                elapsedSeconds = 120,
                isPerfect = true,
            ),
        )
        historyDao.insert(
            history(
                difficulty = Difficulty.EASY,
                elapsedSeconds = 180,
                isPerfect = false,
            ),
        )
        historyDao.insert(
            history(
                difficulty = Difficulty.HARD,
                elapsedSeconds = 300,
                isPerfect = false,
            ),
        )

        assertEquals(3, historyDao.observeAll().first().size)

        historyDao.setFavorite(firstId, true)
        val favorite = historyDao.observeFavorites().first().single()
        assertEquals(firstId, favorite.id)
        assertTrue(favorite.isFavorite)

        val easy = historyDao.observeDifficultySummaries().first()
            .single { it.difficulty == Difficulty.EASY.name }
        assertEquals(2L, easy.games)
        assertEquals(120L, easy.bestSeconds)
        assertEquals(150.0, easy.averageSeconds, 0.001)
        assertEquals(1L, easy.perfectGames)
    }

    @Test
    fun savedPuzzleUniqueIndexPreventsDuplicatePuzzleRows() = runBlocking {
        val first = savedPuzzleDao.insert(savedPuzzle())
        val duplicate = savedPuzzleDao.insert(savedPuzzle(title = "Duplicate title"))

        assertTrue(first > 0)
        assertEquals(-1L, duplicate)
        assertEquals(1, savedPuzzleDao.observeAll().first().size)
    }

    @Test
    fun savedPuzzleFavoriteCanBeToggled() = runBlocking {
        val id = savedPuzzleDao.insert(savedPuzzle())
        savedPuzzleDao.setFavorite(id, true)
        assertTrue(savedPuzzleDao.observeFavorites().first().single().isFavorite)

        savedPuzzleDao.setFavorite(id, false)
        assertTrue(savedPuzzleDao.observeFavorites().first().isEmpty())
        assertFalse(requireNotNull(savedPuzzleDao.getById(id)).isFavorite)
    }

    private fun history(
        difficulty: Difficulty,
        elapsedSeconds: Long,
        isPerfect: Boolean,
    ): GameHistoryEntity = GameHistoryEntity(
        puzzle = PUZZLE,
        solution = SOLUTION,
        difficulty = difficulty.name,
        completed = true,
        elapsedSeconds = elapsedSeconds,
        mistakes = if (isPerfect) 0 else 1,
        hintsUsed = 0,
        startedAtEpochMillis = 1_000,
        completedAtEpochMillis = 1_000 + elapsedSeconds * 1_000,
        isDailyChallenge = false,
        isPerfect = isPerfect,
    )

    private fun savedPuzzle(title: String? = "Saved puzzle"): SavedPuzzleEntity = SavedPuzzleEntity(
        puzzle = PUZZLE,
        solution = SOLUTION,
        title = title,
        difficulty = Difficulty.MEDIUM.name,
        source = "test",
        createdAtEpochMillis = 1_000,
    )

    private companion object {
        const val PUZZLE =
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        const val SOLUTION =
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179"
    }
}
