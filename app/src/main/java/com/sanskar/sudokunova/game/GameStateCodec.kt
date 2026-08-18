package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard

object GameStateCodec {
    private const val VERSION_1 = "1"
    private const val VERSION_2 = "2"
    private const val VERSION_3 = "3"
    private const val VERSION_4 = "4"
    private const val MAX_ELAPSED_SECONDS = 365L * 24L * 60L * 60L
    private const val MAX_COUNTER = 1_000_000

    fun encode(state: GameState): String {
        val notes = state.notes.joinToString(separator = "/") { candidates ->
            candidates.sorted().joinToString(separator = "")
        }
        return listOf(
            VERSION_4,
            state.puzzle.toPuzzleString(),
            state.solution.toPuzzleString(),
            state.board.toPuzzleString(),
            notes,
            state.selectedIndex.toString(),
            state.selectedNumber?.toString().orEmpty(),
            state.notesMode.toString(),
            state.elapsedSeconds.toString(),
            state.mistakes.toString(),
            state.hintsUsed.toString(),
            state.difficulty.name,
            state.seed.toString(),
            state.isPaused.toString(),
            state.status.name,
            state.isDailyChallenge.toString(),
            state.replayOfHistoryId?.toString().orEmpty(),
            state.challengeType.orEmpty(),
            state.challengeKey?.toString().orEmpty(),
        ).joinToString(separator = "|")
    }

    fun decode(encoded: String): GameState? = runCatching {
        val parts = encoded.split('|')
        when (parts.firstOrNull()) {
            VERSION_1 -> decodeV1(parts)
            VERSION_2 -> decodeV2(parts)
            VERSION_3 -> decodeV3(parts)
            VERSION_4 -> decodeV4(parts)
            else -> error("Unsupported saved-game version")
        }
    }.getOrNull()

    private fun decodeV1(parts: List<String>): GameState {
        require(parts.size == 15)
        return baseState(
            parts = parts,
            selectedNumber = null,
            notesModeIndex = 6,
            elapsedIndex = 7,
            mistakesIndex = 8,
            hintsIndex = 9,
            difficultyIndex = 10,
            seedIndex = 11,
            pausedIndex = 12,
            statusIndex = 13,
            dailyIndex = 14,
        )
    }

    private fun decodeV2(parts: List<String>): GameState {
        require(parts.size == 16)
        val selectedNumber = parseSelectedNumber(parts[6])
        return baseState(
            parts = parts,
            selectedNumber = selectedNumber,
            notesModeIndex = 7,
            elapsedIndex = 8,
            mistakesIndex = 9,
            hintsIndex = 10,
            difficultyIndex = 11,
            seedIndex = 12,
            pausedIndex = 13,
            statusIndex = 14,
            dailyIndex = 15,
        )
    }

    private fun decodeV3(parts: List<String>): GameState {
        require(parts.size == 17)
        val selectedNumber = parseSelectedNumber(parts[6])
        val replay = parts[16].takeIf(String::isNotBlank)?.toLong()
        require(replay == null || replay > 0L)
        return baseState(
            parts = parts,
            selectedNumber = selectedNumber,
            notesModeIndex = 7,
            elapsedIndex = 8,
            mistakesIndex = 9,
            hintsIndex = 10,
            difficultyIndex = 11,
            seedIndex = 12,
            pausedIndex = 13,
            statusIndex = 14,
            dailyIndex = 15,
        ).copy(replayOfHistoryId = replay)
    }

    private fun decodeV4(parts: List<String>): GameState {
        require(parts.size == 19)
        val selectedNumber = parseSelectedNumber(parts[6])
        val replay = parts[16].takeIf(String::isNotBlank)?.toLong()
        require(replay == null || replay > 0L)
        val challengeType = parts[17]
            .takeIf(String::isNotBlank)
            ?.also { ChallengeType.valueOf(it) }
        val challengeKey = parts[18].takeIf(String::isNotBlank)?.toLong()
        require((challengeType == null) == (challengeKey == null))
        require(challengeKey == null || challengeKey > 0L)
        return baseState(
            parts = parts,
            selectedNumber = selectedNumber,
            notesModeIndex = 7,
            elapsedIndex = 8,
            mistakesIndex = 9,
            hintsIndex = 10,
            difficultyIndex = 11,
            seedIndex = 12,
            pausedIndex = 13,
            statusIndex = 14,
            dailyIndex = 15,
        ).copy(
            replayOfHistoryId = replay,
            challengeType = challengeType,
            challengeKey = challengeKey,
        )
    }

    private fun baseState(
        parts: List<String>,
        selectedNumber: Int?,
        notesModeIndex: Int,
        elapsedIndex: Int,
        mistakesIndex: Int,
        hintsIndex: Int,
        difficultyIndex: Int,
        seedIndex: Int,
        pausedIndex: Int,
        statusIndex: Int,
        dailyIndex: Int,
    ): GameState {
        val puzzle = SudokuBoard.parse(parts[1])
        val solution = SudokuBoard.parse(parts[2])
        val board = SudokuBoard.parse(parts[3])
        validateBoardRelationship(puzzle, solution, board)

        return GameState(
            puzzle = puzzle,
            solution = solution,
            board = board,
            notes = decodeNotes(parts[4]),
            selectedIndex = parts[5].toInt().also { require(it in 0 until SudokuBoard.CELL_COUNT) },
            selectedNumber = selectedNumber,
            notesMode = parts[notesModeIndex].toBooleanStrict(),
            elapsedSeconds = parts[elapsedIndex].toLong().also {
                require(it in 0L..MAX_ELAPSED_SECONDS)
            },
            mistakes = parts[mistakesIndex].toInt().also { require(it in 0..MAX_COUNTER) },
            hintsUsed = parts[hintsIndex].toInt().also { require(it in 0..MAX_COUNTER) },
            difficulty = Difficulty.valueOf(parts[difficultyIndex]),
            seed = parts[seedIndex].toLong(),
            isPaused = parts[pausedIndex].toBooleanStrict(),
            status = GameStatus.valueOf(parts[statusIndex]),
            isDailyChallenge = parts[dailyIndex].toBooleanStrict(),
        )
    }

    private fun validateBoardRelationship(
        puzzle: SudokuBoard,
        solution: SudokuBoard,
        board: SudokuBoard,
    ) {
        require(puzzle.isValid())
        require(solution.isComplete && solution.isValid())
        for (index in 0 until SudokuBoard.CELL_COUNT) {
            val clue = puzzle.valueAt(index)
            if (clue != SudokuBoard.EMPTY) {
                require(solution.valueAt(index) == clue)
                require(board.valueAt(index) == clue)
            }
        }
    }

    private fun parseSelectedNumber(value: String): Int? = value.takeIf(String::isNotBlank)?.toInt()?.also {
        require(it in 1..9)
    }

    private fun decodeNotes(encodedNotes: String): List<Set<Int>> {
        val decoded = encodedNotes.split('/').map { cell ->
            require(cell.all { it in '1'..'9' })
            cell.map { it.digitToInt() }.toSet()
        }
        require(decoded.size == SudokuBoard.CELL_COUNT)
        return decoded
    }
}
