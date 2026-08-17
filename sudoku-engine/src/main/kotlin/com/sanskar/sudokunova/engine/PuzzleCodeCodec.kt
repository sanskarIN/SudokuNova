package com.sanskar.sudokunova.engine

import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.zip.CRC32

data class SharedPuzzleCode(
    val puzzle: SudokuBoard,
    val difficulty: Difficulty,
)

object PuzzleCodeCodec {
    private const val VERSION = "SNP1"
    const val MAX_CODE_LENGTH = 160

    fun encode(puzzle: SudokuBoard, difficulty: Difficulty): String {
        require(puzzle.isValid()) { "Only valid Sudoku boards can be shared." }
        val body = "${difficulty.name}.${puzzle.toPuzzleString()}"
        return "$VERSION.$body.${checksum(body)}"
    }

    fun decode(raw: String): SharedPuzzleCode? = runCatching {
        val code = raw.trim()
        require(code.isNotEmpty() && code.length <= MAX_CODE_LENGTH)
        val parts = code.split('.')
        require(parts.size == 4)
        require(parts[0] == VERSION)

        val difficulty = Difficulty.valueOf(parts[1])
        val encodedPuzzle = parts[2]
        require(encodedPuzzle.length == SudokuBoard.CELL_COUNT)
        require(encodedPuzzle.all { it in '0'..'9' })

        val body = "${difficulty.name}.$encodedPuzzle"
        require(parts[3].uppercase(Locale.ROOT) == checksum(body))

        val puzzle = SudokuBoard.parse(encodedPuzzle)
        require(puzzle.isValid())
        SharedPuzzleCode(puzzle = puzzle, difficulty = difficulty)
    }.getOrNull()

    private fun checksum(body: String): String {
        val crc = CRC32()
        crc.update(body.toByteArray(StandardCharsets.UTF_8))
        return "%08X".format(Locale.ROOT, crc.value)
    }
}
