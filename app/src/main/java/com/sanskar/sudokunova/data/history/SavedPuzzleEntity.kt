package com.sanskar.sudokunova.data.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_puzzles",
    indices = [
        Index(value = ["puzzle"], unique = true),
        Index(value = ["difficulty"]),
        Index(value = ["isFavorite"]),
    ],
)
data class SavedPuzzleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val puzzle: String,
    val solution: String?,
    val title: String?,
    val difficulty: String,
    val source: String,
    val createdAtEpochMillis: Long,
    val isFavorite: Boolean = false,
)
