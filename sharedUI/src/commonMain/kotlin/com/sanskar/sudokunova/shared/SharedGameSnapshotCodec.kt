package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard

/**
 * Deterministic, platform-neutral text encoding for one active shared Sudoku game.
 *
 * SNG1 stores generated-game state and remains fully supported for backward
 * compatibility. SNG2 adds the original validated SNP1 puzzle code so imported
 * sessions can be restored without regenerating a different puzzle.
 *
 * The codec intentionally owns only transport validation. [SharedGameState.restore]
 * remains the authority for validating that a decoded board and provenance are
 * still a valid Sudoku session.
 */
object SharedGameSnapshotCodec {
    private const val VERSION_V1 = "SNG1"
    private const val VERSION_V2 = "SNG2"
    private const val FIELD_SEPARATOR = '|'
    private const val NOTES_SEPARATOR = ','
    private const val NOTE_VALUE_SEPARATOR = ':'
    private const val NO_SELECTION = -1
    private const val MAX_ENCODED_LENGTH = 2048
    private const val MAX_SOURCE_CODE_LENGTH = 256

    fun encode(snapshot: SharedGameSnapshot): String {
        require(snapshot.board.length == SudokuBoard.CELL_COUNT) { "Board must contain exactly 81 cells." }
        require(snapshot.board.all { it in '0'..'9' }) { "Board must contain only digits 0..9." }
        require(snapshot.selectedIndex == null || snapshot.selectedIndex in 0 until SudokuBoard.CELL_COUNT) {
            "Selected cell index is outside the Sudoku board."
        }
        validateNotes(snapshot.notes)
        validateSourceCode(snapshot.sourceCode)

        val notes = snapshot.notes
            .entries
            .sortedBy { it.key }
            .joinToString(NOTES_SEPARATOR.toString()) { (index, values) ->
                val digits = values.sorted().joinToString(separator = "")
                "$index$NOTE_VALUE_SEPARATOR$digits"
            }
        val selected = snapshot.selectedIndex ?: NO_SELECTION
        val notesMode = if (snapshot.notesMode) 1 else 0
        val version = if (snapshot.sourceCode == null) VERSION_V1 else VERSION_V2
        val fields = buildList {
            add(version)
            add(snapshot.difficulty.name)
            add(snapshot.seed.toString())
            add(snapshot.board)
            add(selected.toString())
            add(notesMode.toString())
            add(notes)
            snapshot.sourceCode?.let(::add)
        }

        return fields.joinToString(FIELD_SEPARATOR.toString()).also {
            require(it.length <= MAX_ENCODED_LENGTH) { "Encoded snapshot exceeds the supported size limit." }
        }
    }

    fun decode(encoded: String): SharedGameSnapshot? = runCatching {
        require(encoded.length <= MAX_ENCODED_LENGTH) { "Encoded snapshot exceeds the supported size limit." }
        val fields = encoded.split(FIELD_SEPARATOR)
        val version = fields.firstOrNull() ?: error("Snapshot is empty.")
        val expectedFieldCount = when (version) {
            VERSION_V1 -> 7
            VERSION_V2 -> 8
            else -> error("Snapshot version is unsupported.")
        }
        require(fields.size == expectedFieldCount) { "Snapshot field count is invalid." }

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
        val sourceCode = fields.getOrNull(7)?.also(::validateSourceCode)

        SharedGameSnapshot(
            difficulty = difficulty,
            seed = seed,
            board = board,
            notes = notes,
            selectedIndex = selectedIndex,
            notesMode = notesMode,
            sourceCode = sourceCode,
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

    private fun validateSourceCode(sourceCode: String?) {
        if (sourceCode == null) return
        require(sourceCode.isNotEmpty()) { "Source puzzle code cannot be empty." }
        require(sourceCode.length <= MAX_SOURCE_CODE_LENGTH) { "Source puzzle code exceeds the supported size limit." }
        require(FIELD_SEPARATOR !in sourceCode) { "Source puzzle code cannot contain the field separator." }
    }
}
