package com.sanskar.sudokunova.data.challenge

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "challenge_results",
    indices = [
        Index(value = ["challengeType", "challengeKey"], unique = true),
        Index(value = ["completedAtEpochMillis"]),
    ],
)
data class ChallengeResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeType: String,
    val challengeKey: Long,
    val difficulty: String,
    val puzzle: String,
    val elapsedSeconds: Long,
    val mistakes: Int,
    val hintsUsed: Int,
    val completedAtEpochMillis: Long,
    val perfect: Boolean,
)
