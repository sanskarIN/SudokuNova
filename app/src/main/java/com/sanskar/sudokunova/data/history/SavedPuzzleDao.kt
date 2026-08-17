package com.sanskar.sudokunova.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPuzzleDao {
    @Query("SELECT * FROM saved_puzzles ORDER BY createdAtEpochMillis DESC, id DESC")
    fun observeAll(): Flow<List<SavedPuzzleEntity>>

    @Query("SELECT * FROM saved_puzzles WHERE isFavorite = 1 ORDER BY createdAtEpochMillis DESC, id DESC")
    fun observeFavorites(): Flow<List<SavedPuzzleEntity>>

    @Query("SELECT * FROM saved_puzzles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SavedPuzzleEntity?

    @Query("SELECT * FROM saved_puzzles WHERE puzzle = :puzzle LIMIT 1")
    suspend fun getByPuzzle(puzzle: String): SavedPuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: SavedPuzzleEntity): Long

    @Query("UPDATE saved_puzzles SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("DELETE FROM saved_puzzles WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_puzzles")
    suspend fun deleteAll()
}
