package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard

object GameStateCodec {
    private const val VERSION_1 = "1"
    private const val VERSION_2 = "2"

    fun encode(state: GameState): String {
        val notes = state.notes.joinToString(separator = "/") { candidates ->
            candidates.sorted().joinToString(separator = "")
        }
        return listOf(
            VERSION_2,
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
        ).joinToString(separator = "|")
    }

    fun decode(encoded: String): GameState? = runCatching {
        val parts = encoded.split('|')
        when (parts.firstOrNull()) {
            VERSION_1 -> decodeV1(parts)
            VERSION_2 -> decodeV2(parts)
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
            selectedIndex = parts[5].toInt().coerceIn(0, SudokuBoard.CELL_COUNT - 1),
            selectedNumber = null,
            notesMode = parts[6].toBooleanStrict(),
            elapsedSeconds = parts[7].toLong().coerceAtLeast(0),
            mistakes = parts[8].toInt().coerceAtLeast(0),
            hintsUsed = parts[9].toInt().coerceAtLeast(0),
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
            selectedIndex = parts[5].toInt().coerceIn(0, SudokuBoard.CELL_COUNT - 1),
            selectedNumber = selectedNumber,
            notesMode = parts[7].toBooleanStrict(),
            elapsedSeconds = parts[8].toLong().coerceAtLeast(0),
            mistakes = parts[9].toInt().coerceAtLeast(0),
            hintsUsed = parts[10].toInt().coerceAtLeast(0),
            difficulty = Difficulty.valueOf(parts[11]),
            seed = parts[12].toLong(),
            isPaused = parts[13].toBooleanStrict(),
            status = GameStatus.valueOf(parts[14]),
            isDailyChallenge = parts[15].toBooleanStrict(),
        )
    }

    private fun decodeNotes(encodedNotes: String): List<Set<Int>> {
        val decoded = encodedNotes.split('/').map { cell ->
            cell.map { it.digitToInt() }.filter { it in 1..9 }.toSet()
        }
        require(decoded.size == SudokuBoard.CELL_COUNT)
        return decoded
    }
}
