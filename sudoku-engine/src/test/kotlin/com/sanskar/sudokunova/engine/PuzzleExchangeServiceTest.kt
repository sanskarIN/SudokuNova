package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PuzzleExchangeServiceTest {
    private val service = PuzzleExchangeService()
    private val puzzle = SudokuBoard.parse(
        "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
    )

    @Test
    fun uniquePuzzleCodeImportsWithSolvedBoardDifficultyAndAssessment() {
        val code = service.exportCode(puzzle, Difficulty.HARD)

        val imported = service.importCode(code)

        assertNotNull(imported)
        assertEquals(puzzle, imported.puzzle)
        assertEquals(Difficulty.HARD, imported.difficulty)
        assertEquals(true, imported.solution.isComplete)
        assertEquals(Difficulty.BEGINNER, imported.assessment.estimatedDifficulty)
        assertEquals(0, imported.assessment.metrics.guesses)
    }

    @Test
    fun exportRemainsCompatibleWithExistingSnp1Format() {
        assertEquals(
            "SNP1.HARD.530070000600195000098000060800060003400803001700020006060000280000419005000080079.8B59A2D7",
            service.exportCode(puzzle, Difficulty.HARD),
        )
    }

    @Test
    fun ambiguousAndMalformedPuzzleCodesAreRejected() {
        val ambiguousCode = service.exportCode(SudokuBoard.empty(), Difficulty.BEGINNER)

        assertNull(service.importCode(ambiguousCode))
        assertNull(service.importCode("not-a-sudoku-code"))
    }
}