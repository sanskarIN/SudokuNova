package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard

/**
 * Deterministic, platform-neutral text encoding for one active shared Sudoku game.
 *
 * The codec intentionally owns only transport validation. [SharedGameState.restore]
 * remains the authority for validating that a decoded board still matches the
 * generated puzzle identified by its difficulty and seed.
 */
object SharedGameSnapshotCodec {
    private const val VERSION = "SNG1"
    private const val FIELD_SEPARATOR = '|'
    private const val NOTES_SEPARATOR = ','
    private const val NOTE_VALUE_SEPARATOR = ':'
    private const val NO_SELECTION = -1
    private const val MAX_ENCODED_LENGTH = 2048

    fun encode(snapshot: SharedGameSnapshot): String {
        require(snapshot.board.length == SudokuBoard.CELL_COUNT) { "Board must contain exactly 81 cells." }
        require(snapshot.board.all { it in '0'..'9' }) { "Board must contain only digits 0..9." }
        require(snapshot.selectedIndex == null || snapshot.selectedIndex in 0 until SudokuBoard.CELL_COUNT) {
            "Selected cell index is outside the Sudoku board."
        }
        validateNotes(snapshot.notes)

        val notes = snapshot.notes
            .entries
            .sortedBy { it.key }
            .joinToString(NOTES_SEPARATOR.toString()) { (index, values) ->
                val digits = values.sorted().joinToString(separator = "")
                "$index$NOTE_VALUE_SEPARATOR$digits"
            }
        val selected = snapshot.selectedIndex ?: NO_SELECTION
        val notesMode = if (snapshot.notesMode) 1 else 0

        return listOf(
            VERSION,
            snapshot.difficulty.name,
            snapshot.seed.toString(),
            snapshot.board,
            selected.toString(),
            notesMode.toString(),
            notes,
        ).joinToString(FIELD_SEPARATOR.toString()).also {
            require(it.length <= MAX_ENCODED_LENGTH) { "Encoded snapshot exceeds the supported size limit." }
        }
    }

    fun decode(encoded: String): SharedGameSnapshot? = runCatching {
        require(encoded.length <= MAX_ENCODED_LENGTH) { "Encoded snapshot exceeds the supported size limit." }
        val fields = encoded.split(FIELD_SEPARATOR, limit = 7)
        require(fields.size == 7) { "Snapshot field count is invalid." }
        require(fields[0] == VERSION) { "Snapshot version is unsupported." }

        val difficulty = Difficulty.entries.firstOrNull { it.name == fields[1] }
            ?: error("Snapshot difficulty is unsupported.")
        val seed = fields[2].toLong()
        val board = fields[3]
        require(board.length == SudokuBoard.CELL_COUNT) { "Board must contain exactly 81 cells." }
        require(board.all { it in '0'..'9' }) { "Board must contain only digits 0..9." }

        val selectedRaw = fields[4].toInt()
        val selectedIndex = when (selectedRaw) {
            NO_SELECTION -> null
            in 0 until SudokuBoard.CELL_COUNT -> selectedRaw
            else -> error("Selected cell index is outside the Sudoku board.")
        }
        val notesMode = when (fields[5]) {
            "0" -> false
            "1" -> true
            else -> error("Notes-mode flag is invalid.")
        }
        val notes = decodeNotes(fields[6])

        SharedGameSnapshot(
            difficulty = difficulty,
            seed = seed,
            board = board,
            notes = notes,
            selectedIndex = selectedIndex,
            notesMode = notesMode,
        )
    }.getOrNull()

    private fun decodeNotes(encoded: String): Map<Int, Set<Int>> {
        if (encoded.isEmpty()) return emptyMap()

        val result = linkedMapOf<Int, Set<Int>>()
        encoded.split(NOTES_SEPARATOR).forEach { entry ->
            val parts = entry.split(NOTE_VALUE_SEPARATOR, limit = 2)
            require(parts.size == 2) { "Note entry is invalid." }
            val index = parts[0].toInt()
            require(index in 0 until SudokuBoard.CELL_COUNT) { "Note cell index is outside the Sudoku board." }
            require(index !in result) { "Duplicate note cell index is not allowed." }
            val values = parts[1].map { char ->
                require(char in '1'..'9') { "Note values must be digits 1..9." }
                char.digitToInt()
            }.toSet()
            require(values.isNotEmpty()) { "Note values cannot be empty." }
            require(values.size == parts[1].length) { "Duplicate note values are not allowed." }
            result[index] = values
        }
        validateNotes(result)
        return result
    }

    private fun validateNotes(notes: Map<Int, Set<Int>>) {
        require(notes.size <= SudokuBoard.CELL_COUNT) { "Too many note cells." }
        notes.forEach { (index, values) ->
            require(index in 0 until SudokuBoard.CELL_COUNT) { "Note cell index is outside the Sudoku board." }
            require(values.isNotEmpty()) { "Note values cannot be empty." }
            require(values.all { it in 1..9 }) { "Note values must be digits 1..9." }
        }
    }
}
