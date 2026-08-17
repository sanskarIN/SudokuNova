package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard

object GameStateCodec {
    private const val VERSION_1 = "1"
    private const val VERSION_2 = "2"
    private const val VERSION_3 = "3"

    fun encode(state: GameState): String {
        val notes = state.notes.joinToString(separator = "/") { candidates ->
            candidates.sorted().joinToString(separator = "")
        }
        return listOf(
            VERSION_3,
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
            else -> error("Unsupported saved-game version.")
        }
    }.getOrNull()

    private fun decodeV1(parts: List<String>): GameState {
        require(parts.size == 15)
        return GameState(
            puzzle = SudokuBoard.parse(parts[1]),
            solution = SudokuBoard.parse(parts[2]),
            board = SudokuBoard.parse(parts[3]),
            notes = decodeNotes(parts[4]),
            selectedIndex = parseSelectedIndex(parts[5]),
            selectedNumber = null,
            notesMode = parts[6].toBooleanStrict(),
            elapsedSeconds = parseNonNegativeLong(parts[7]),
            mistakes = parseNonNegativeInt(parts[8]),
            hintsUsed = parseNonNegativeInt(parts[9]),
            difficulty = Difficulty.valueOf(parts[10]),
            seed = parts[11].toLong(),
            isPaused = parts[12].toBooleanStrict(),
            status = GameStatus.valueOf(parts[13]),
            isDailyChallenge = parts[14].toBooleanStrict(),
        )
    }

    private fun decodeV2(parts: List<String>): GameState {
        require(parts.size == 16)
        val selectedNumber = parts[6].takeIf(String::isNotBlank)?.toInt()
        require(selectedNumber == null || selectedNumber in 1..9)
        return GameState(
            puzzle = SudokuBoard.parse(parts[1]),
            solution = SudokuBoard.parse(parts[2]),
            board = SudokuBoard.parse(parts[3]),
            notes = decodeNotes(parts[4]),
            selectedIndex = parseSelectedIndex(parts[5]),
            selectedNumber = selectedNumber,
            notesMode = parts[7].toBooleanStrict(),
            elapsedSeconds = parseNonNegativeLong(parts[8]),
            mistakes = parseNonNegativeInt(parts[9]),
            hintsUsed = parseNonNegativeInt(parts[10]),
            difficulty = Difficulty.valueOf(parts[11]),
            seed = parts[12].toLong(),
            isPaused = parts[13].toBooleanStrict(),
            status = GameStatus.valueOf(parts[14]),
            isDailyChallenge = parts[15].toBooleanStrict(),
        )
    }

    private fun decodeV3(parts: List<String>): GameState {
        require(parts.size == 18)
        val selectedNumber = parts[6].takeIf(String::isNotBlank)?.toInt()
        require(selectedNumber == null || selectedNumber in 1..9)
        val challengeType = parts[16].takeIf(String::isNotBlank)
        val challengeKey = parts[17].takeIf(String::isNotBlank)?.toLong()
        require((challengeType == null) == (challengeKey == null))
        return GameState(
            puzzle = SudokuBoard.parse(parts[1]),
            solution = SudokuBoard.parse(parts[2]),
            board = SudokuBoard.parse(parts[3]),
            notes = decodeNotes(parts[4]),
            selectedIndex = parseSelectedIndex(parts[5]),
            selectedNumber = selectedNumber,
            notesMode = parts[7].toBooleanStrict(),
            elapsedSeconds = parseNonNegativeLong(parts[8]),
            mistakes = parseNonNegativeInt(parts[9]),
            hintsUsed = parseNonNegativeInt(parts[10]),
            difficulty = Difficulty.valueOf(parts[11]),
            seed = parts[12].toLong(),
            isPaused = parts[13].toBooleanStrict(),
            status = GameStatus.valueOf(parts[14]),
            isDailyChallenge = parts[15].toBooleanStrict(),
            challengeType = challengeType,
            challengeKey = challengeKey,
        )
    }

    private fun parseSelectedIndex(value: String): Int = value.toInt().also {
        require(it in 0 until SudokuBoard.CELL_COUNT)
    }

    private fun parseNonNegativeLong(value: String): Long = value.toLong().also {
        require(it >= 0L)
    }

    private fun parseNonNegativeInt(value: String): Int = value.toInt().also {
        require(it >= 0)
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
