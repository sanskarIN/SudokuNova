package com.sanskar.sudokunova.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Query(
        """
        SELECT * FROM game_history
        ORDER BY COALESCE(completedAtEpochMillis, startedAtEpochMillis) DESC, id DESC
        """,
    )
    fun observeAll(): Flow<List<GameHistoryEntity>>

    @Query(
        """
        SELECT * FROM game_history
        WHERE difficulty = :difficulty
        ORDER BY COALESCE(completedAtEpochMillis, startedAtEpochMillis) DESC, id DESC
        """,
    )
    fun observeDifficulty(difficulty: String): Flow<List<GameHistoryEntity>>

    @Query(
        """
        SELECT * FROM game_history
        WHERE isFavorite = 1
        ORDER BY COALESCE(completedAtEpochMillis, startedAtEpochMillis) DESC, id DESC
        """,
    )
    fun observeFavorites(): Flow<List<GameHistoryEntity>>

    @Query(
        """
        SELECT
            difficulty AS difficulty,
            COUNT(*) AS games,
            AVG(elapsedSeconds) AS averageSeconds,
            MIN(elapsedSeconds) AS bestSeconds,
            SUM(CASE WHEN isPerfect = 1 THEN 1 ELSE 0 END) AS perfectGames
        FROM game_history
        WHERE completed = 1 AND replayOfHistoryId IS NULL
        GROUP BY difficulty
        ORDER BY difficulty
        """,
    )
    fun observeDifficultySummaries(): Flow<List<DifficultyHistorySummary>>

    @Query("SELECT * FROM game_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): GameHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: GameHistoryEntity): Long

    @Query("UPDATE game_history SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("DELETE FROM game_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM game_history")
    suspend fun deleteAll()
}
