package com.sanskar.sudokunova.data.challenge

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeResultDao {
    @Query(
        """
        SELECT * FROM challenge_results
        ORDER BY completedAtEpochMillis DESC, id DESC
        """,
    )
    fun observeAll(): Flow<List<ChallengeResultEntity>>

    @Query(
        """
        SELECT * FROM challenge_results
        WHERE challengeType = :challengeType
        ORDER BY completedAtEpochMillis DESC, id DESC
        """,
    )
    fun observeType(challengeType: String): Flow<List<ChallengeResultEntity>>

    @Query(
        """
        SELECT * FROM challenge_results
        WHERE challengeType = :challengeType AND challengeKey = :challengeKey
        LIMIT 1
        """,
    )
    suspend fun get(challengeType: String, challengeKey: Long): ChallengeResultEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ChallengeResultEntity): Long

    @Query("DELETE FROM challenge_results")
    suspend fun deleteAll()
}
