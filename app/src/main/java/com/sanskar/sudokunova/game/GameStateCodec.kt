package com.sanskar.sudokunova.game

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard

object GameStateCodec {
    private const val VERSION = "1"

    fun encode(state: GameState): String {
        val notes = state.notes.joinToString(separator = "/") { candidates ->
            candidates.sorted().joinToString(separator = "")
        }
        return listOf(
            VERSION,
            state.puzzle.toPuzzleString(),
            state.solution.toPuzzleString(),
            state.board.toPuzzleString(),
            notes,
            state.selectedIndex.toString(),
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
        require(parts.size == 15 && parts[0] == VERSION)
        val decodedNotes = parts[4].split('/').map { cell ->
            cell.map { it.digitToInt() }.filter { it in 1..9 }.toSet()
        }
        require(decodedNotes.size == SudokuBoard.CELL_COUNT)

        GameState(
            puzzle = SudokuBoard.parse(parts[1]),
            solution = SudokuBoard.parse(parts[2]),
            board = SudokuBoard.parse(parts[3]),
            notes = decodedNotes,
            selectedIndex = parts[5].toInt().coerceIn(0, SudokuBoard.CELL_COUNT - 1),
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
    }.getOrNull()
}
