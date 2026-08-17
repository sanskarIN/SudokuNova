package com.sanskar.sudokunova.data.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_history",
    indices = [
        Index(value = ["difficulty"]),
        Index(value = ["completedAtEpochMillis"]),
        Index(value = ["isFavorite"]),
    ],
)
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val isFavorite: Boolean = false,
    val replayOfHistoryId: Long? = null,
)

data class DifficultyHistorySummary(
    val difficulty: String,
    val games: Long,
    val averageSeconds: Double,
    val bestSeconds: Long,
    val perfectGames: Long,
)
