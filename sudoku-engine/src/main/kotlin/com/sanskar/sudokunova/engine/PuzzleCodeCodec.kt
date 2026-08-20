package com.sanskar.sudokunova.engine

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
        require(parts[3].uppercase() == checksum(body))

        val puzzle = SudokuBoard.parse(encodedPuzzle)
        require(puzzle.isValid())
        SharedPuzzleCode(puzzle = puzzle, difficulty = difficulty)
    }.getOrNull()

    private fun checksum(body: String): String =
        crc32(body.encodeToByteArray())
            .toString(radix = 16)
            .uppercase()
            .padStart(length = 8, padChar = '0')

    private fun crc32(bytes: ByteArray): UInt {
        var crc = 0xFFFFFFFFu
        for (byte in bytes) {
            crc = crc xor byte.toUByte().toUInt()
            repeat(8) {
                crc = if ((crc and 1u) != 0u) {
                    (crc shr 1) xor 0xEDB88320u
                } else {
                    crc shr 1
                }
            }
        }
        return crc xor 0xFFFFFFFFu
    }
}
