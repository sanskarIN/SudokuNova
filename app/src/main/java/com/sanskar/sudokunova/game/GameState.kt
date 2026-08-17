package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.GeneratedPuzzle
import com.sanskar.sudokunova.engine.SudokuBoard

enum class GameStatus { PLAYING, COMPLETED, FAILED }

data class GameState(
    val puzzle: SudokuBoard,
    val solution: SudokuBoard,
    val board: SudokuBoard,
    val notes: List<Set<Int>> = List(SudokuBoard.CELL_COUNT) { emptySet() },
    val selectedIndex: Int = 0,
    val selectedNumber: Int? = null,
    val notesMode: Boolean = false,
    val elapsedSeconds: Long = 0,
    val mistakes: Int = 0,
    val hintsUsed: Int = 0,
    val difficulty: Difficulty = Difficulty.EASY,
    val seed: Long = 0L,
    val isPaused: Boolean = false,
    val status: GameStatus = GameStatus.PLAYING,
    val isDailyChallenge: Boolean = false,
    val replayOfHistoryId: Long? = null,
    val challengeType: String? = null,
    val challengeKey: Long? = null,
) {
    init {
        require(selectedIndex in 0 until SudokuBoard.CELL_COUNT)
        require(selectedNumber == null || selectedNumber in 1..9)
        require(replayOfHistoryId == null || replayOfHistoryId > 0L)
        require((challengeType == null) == (challengeKey == null))
    }

    val progressPercent: Int
        get() {
            val original = puzzle.clueCount
            val filledByPlayer = board.clueCount - original
            val totalToFill = SudokuBoard.CELL_COUNT - original
            return if (totalToFill == 0) 100 else (filledByPlayer * 100 / totalToFill).coerceIn(0, 100)
        }

    fun isOriginal(index: Int): Boolean = puzzle.valueAt(index) != SudokuBoard.EMPTY

    companion object {
        fun fromGenerated(
            generated: GeneratedPuzzle,
            dailyChallenge: Boolean = false,
            challengeType: String? = null,
            challengeKey: Long? = null,
        ): GameState = GameState(
            puzzle = generated.puzzle,
            solution = generated.solution,
            board = generated.puzzle,
            difficulty = generated.difficulty,
            seed = generated.seed,
            isDailyChallenge = dailyChallenge,
            challengeType = challengeType,
            challengeKey = challengeKey,
        )
    }
}
