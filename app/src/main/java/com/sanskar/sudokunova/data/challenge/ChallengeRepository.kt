package com.sanskar.sudokunova.data.challenge

import android.content.Context
import com.sanskar.sudokunova.data.history.SudokuNovaDatabase
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.game.GameState
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(context: Context) {
    private val dao = SudokuNovaDatabase.get(context).challengeResultDao()

    fun observeAll(): Flow<List<ChallengeResultEntity>> = dao.observeAll()

    fun observeType(type: ChallengeType): Flow<List<ChallengeResultEntity>> =
        dao.observeType(type.name)

    suspend fun get(type: ChallengeType, key: Long): ChallengeResultEntity? =
        dao.get(type.name, key)

    suspend fun recordCompletion(
        state: GameState,
        completedAtEpochMillis: Long = System.currentTimeMillis(),
    ): Long {
        val type = state.challengeType
            ?.let { runCatching { ChallengeType.valueOf(it) }.getOrNull() }
            ?: return -1L
        val key = state.challengeKey ?: return -1L
        return dao.insert(
            ChallengeResultEntity(
                challengeType = type.name,
                challengeKey = key,
                difficulty = state.difficulty.name,
                puzzle = state.puzzle.toPuzzleString(),
                elapsedSeconds = state.elapsedSeconds.coerceAtLeast(0),
                mistakes = state.mistakes.coerceAtLeast(0),
                hintsUsed = state.hintsUsed.coerceAtLeast(0),
                completedAtEpochMillis = completedAtEpochMillis,
                perfect = state.mistakes == 0 && state.hintsUsed == 0,
            ),
        )
    }

    suspend fun clear() {
        dao.deleteAll()
    }

    companion object {
        fun difficultyFor(type: ChallengeType): Difficulty = when (type) {
            ChallengeType.DAILY -> Difficulty.MEDIUM
            ChallengeType.WEEKLY -> Difficulty.HARD
        }
    }
}
