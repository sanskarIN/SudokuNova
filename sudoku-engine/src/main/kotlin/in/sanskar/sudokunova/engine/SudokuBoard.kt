package in.sanskar.sudokunova.engine

class SudokuBoard private constructor(
    private val cells: IntArray,
) {
    init {
        require(cells.size == CELL_COUNT) { "A Sudoku board must contain exactly 81 cells." }
        require(cells.all { it in 0..9 }) { "Sudoku values must be in the range 0..9." }
    }

    val clueCount: Int
        get() = cells.count { it != EMPTY }

    val isComplete: Boolean
        get() = cells.none { it == EMPTY } && isValid()

    operator fun get(row: Int, column: Int): Int = cells[indexOf(row, column)]

    fun valueAt(index: Int): Int = cells[checkedIndex(index)]

    fun withValue(row: Int, column: Int, value: Int): SudokuBoard =
        withValue(indexOf(row, column), value)

    fun withValue(index: Int, value: Int): SudokuBoard {
        require(value in 0..9) { "Sudoku values must be in the range 0..9." }
        val copy = cells.copyOf()
        copy[checkedIndex(index)] = value
        return SudokuBoard(copy)
    }

    fun candidates(row: Int, column: Int): Set<Int> = candidates(indexOf(row, column))

    fun candidates(index: Int): Set<Int> {
        val safeIndex = checkedIndex(index)
        if (cells[safeIndex] != EMPTY) return emptySet()

        val row = safeIndex / SIZE
        val column = safeIndex % SIZE
        val used = BooleanArray(10)

        for (i in 0 until SIZE) {
            used[this[row, i]] = true
            used[this[i, column]] = true
        }

        val boxRow = (row / BOX_SIZE) * BOX_SIZE
        val boxColumn = (column / BOX_SIZE) * BOX_SIZE
        for (r in boxRow until boxRow + BOX_SIZE) {
            for (c in boxColumn until boxColumn + BOX_SIZE) {
                used[this[r, c]] = true
            }
        }

        return (1..9).filterNot { used[it] }.toSet()
    }

    fun isValid(): Boolean {
        for (row in 0 until SIZE) {
            if (hasDuplicates((0 until SIZE).map { column -> this[row, column] })) return false
        }
        for (column in 0 until SIZE) {
            if (hasDuplicates((0 until SIZE).map { row -> this[row, column] })) return false
        }
        for (boxRow in 0 until SIZE step BOX_SIZE) {
            for (boxColumn in 0 until SIZE step BOX_SIZE) {
                val values = buildList {
                    for (row in boxRow until boxRow + BOX_SIZE) {
                        for (column in boxColumn until boxColumn + BOX_SIZE) {
                            add(this@SudokuBoard[row, column])
                        }
                    }
                }
                if (hasDuplicates(values)) return false
            }
        }
        return true
    }

    fun hasConflict(index: Int): Boolean {
        val safeIndex = checkedIndex(index)
        val value = cells[safeIndex]
        if (value == EMPTY) return false

        val row = safeIndex / SIZE
        val column = safeIndex % SIZE
        for (i in 0 until SIZE) {
            if (i != column && this[row, i] == value) return true
            if (i != row && this[i, column] == value) return true
        }

        val boxRow = (row / BOX_SIZE) * BOX_SIZE
        val boxColumn = (column / BOX_SIZE) * BOX_SIZE
        for (r in boxRow until boxRow + BOX_SIZE) {
            for (c in boxColumn until boxColumn + BOX_SIZE) {
                if ((r != row || c != column) && this[r, c] == value) return true
            }
        }
        return false
    }

    fun emptyIndices(): List<Int> = cells.indices.filter { cells[it] == EMPTY }

    fun toIntArray(): IntArray = cells.copyOf()

    fun toPuzzleString(): String = cells.joinToString(separator = "")

    override fun equals(other: Any?): Boolean =
        other is SudokuBoard && cells.contentEquals(other.cells)

    override fun hashCode(): Int = cells.contentHashCode()

    override fun toString(): String = toPuzzleString()

    private fun hasDuplicates(values: List<Int>): Boolean {
        val seen = BooleanArray(10)
        for (value in values) {
            if (value == EMPTY) continue
            if (seen[value]) return true
            seen[value] = true
        }
        return false
    }

    private fun indexOf(row: Int, column: Int): Int {
        require(row in 0 until SIZE && column in 0 until SIZE) { "Cell is outside the 9x9 board." }
        return row * SIZE + column
    }

    private fun checkedIndex(index: Int): Int {
        require(index in 0 until CELL_COUNT) { "Cell index must be in 0..80." }
        return index
    }

    companion object {
        const val SIZE = 9
        const val BOX_SIZE = 3
        const val CELL_COUNT = 81
        const val EMPTY = 0

        fun empty(): SudokuBoard = SudokuBoard(IntArray(CELL_COUNT))

        fun from(values: IntArray): SudokuBoard = SudokuBoard(values.copyOf())

        fun from(values: List<Int>): SudokuBoard = SudokuBoard(values.toIntArray())

        fun parse(puzzle: String): SudokuBoard {
            require(puzzle.length == CELL_COUNT) { "Puzzle string must contain exactly 81 characters." }
            val values = IntArray(CELL_COUNT) { index ->
                when (val char = puzzle[index]) {
                    '.', '0' -> EMPTY
                    in '1'..'9' -> char.digitToInt()
                    else -> error("Unsupported puzzle character '$char' at index $index.")
                }
            }
            return SudokuBoard(values)
        }
    }
}
