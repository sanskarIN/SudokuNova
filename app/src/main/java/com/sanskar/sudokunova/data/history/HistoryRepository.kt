package com.sanskar.sudokunova.data.history

import android.content.Context
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.game.GameState
import kotlinx.coroutines.flow.Flow

class HistoryRepository(context: Context) {
    private val database = SudokuNovaDatabase.get(context)
    private val historyDao = database.gameHistoryDao()
    private val savedPuzzleDao = database.savedPuzzleDao()

    fun observeHistory(): Flow<List<GameHistoryEntity>> = historyDao.observeAll()

    fun observeHistory(difficulty: Difficulty): Flow<List<GameHistoryEntity>> =
        historyDao.observeDifficulty(difficulty.name)

    fun observeFavoriteHistory(): Flow<List<GameHistoryEntity>> = historyDao.observeFavorites()

    fun observeDifficultySummaries(): Flow<List<DifficultyHistorySummary>> =
        historyDao.observeDifficultySummaries()

    fun observeSavedPuzzles(): Flow<List<SavedPuzzleEntity>> = savedPuzzleDao.observeAll()

    fun observeFavoriteSavedPuzzles(): Flow<List<SavedPuzzleEntity>> = savedPuzzleDao.observeFavorites()

    suspend fun recordCompletedGame(
        state: GameState,
        completedAtEpochMillis: Long = System.currentTimeMillis(),
        replayOfHistoryId: Long? = null,
    ): Long {
        val elapsedMillis = state.elapsedSeconds.coerceAtLeast(0) * 1_000L
        val startedAtEpochMillis = (completedAtEpochMillis - elapsedMillis).coerceAtLeast(0L)
        return historyDao.insert(
            GameHistoryEntity(
                puzzle = state.puzzle.toPuzzleString(),
                solution = state.solution.toPuzzleString(),
                difficulty = state.difficulty.name,
                completed = state.board == state.solution,
                elapsedSeconds = state.elapsedSeconds.coerceAtLeast(0),
                mistakes = state.mistakes.coerceAtLeast(0),
                hintsUsed = state.hintsUsed.coerceAtLeast(0),
                startedAtEpochMillis = startedAtEpochMillis,
                completedAtEpochMillis = completedAtEpochMillis,
                isDailyChallenge = state.isDailyChallenge,
                isPerfect = state.mistakes == 0 && state.hintsUsed == 0,
                replayOfHistoryId = replayOfHistoryId,
            ),
        )
    }

    suspend fun setHistoryFavorite(id: Long, favorite: Boolean) {
        historyDao.setFavorite(id, favorite)
    }

    suspend fun deleteHistory(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun clearHistory() {
        historyDao.deleteAll()
    }

    suspend fun savePuzzle(
        puzzle: String,
        solution: String?,
        title: String?,
        difficulty: Difficulty,
        source: String,
        favorite: Boolean = false,
        createdAtEpochMillis: Long = System.currentTimeMillis(),
    ): Long = savedPuzzleDao.insert(
        SavedPuzzleEntity(
            puzzle = puzzle,
            solution = solution,
            title = title?.trim()?.takeIf(String::isNotEmpty),
            difficulty = difficulty.name,
            source = source,
            createdAtEpochMillis = createdAtEpochMillis,
            isFavorite = favorite,
        ),
    )

    suspend fun findSavedPuzzle(puzzle: String): SavedPuzzleEntity? =
        savedPuzzleDao.getByPuzzle(puzzle)

    suspend fun setSavedPuzzleFavorite(id: Long, favorite: Boolean) {
        savedPuzzleDao.setFavorite(id, favorite)
    }

    suspend fun deleteSavedPuzzle(id: Long) {
        savedPuzzleDao.deleteById(id)
    }

    suspend fun clearSavedPuzzles() {
        savedPuzzleDao.deleteAll()
    }
}
