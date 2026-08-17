package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LogicalDifficultyAnalyzerTest {
    @Test
    fun solvedBoardProducesSolvedBand() {
        val solved = SudokuBoard.parse(
            "534678912672195348198342567859761423426853791713924856961537284287419635345286179",
        )

        val evidence = LogicalDifficultyAnalyzer.analyze(solved)

        assertEquals(0, evidence.startingEmptyCells)
        assertEquals(0, evidence.unresolvedCells)
        assertEquals(LogicalDifficultyBand.SOLVED, evidence.band)
        assertTrue(evidence.solvedWithSingles)
    }

    @Test
    fun commonPuzzleMakesDeterministicLogicalProgress() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )

        val first = LogicalDifficultyAnalyzer.analyze(puzzle)
        val second = LogicalDifficultyAnalyzer.analyze(puzzle)

        assertEquals(first, second)
        assertTrue(first.startingEmptyCells > 0)
        assertTrue(first.singlesPlaced > 0)
        assertTrue(first.unresolvedCells < first.startingEmptyCells)
        assertTrue(first.logicalScore >= 0)
    }

    @Test
    fun emptyBoardIsRecognizedAsRequiringBeyondSingles() {
        val evidence = LogicalDifficultyAnalyzer.analyze(SudokuBoard.empty())

        assertEquals(81, evidence.startingEmptyCells)
        assertEquals(81, evidence.unresolvedCells)
        assertEquals(0, evidence.singlesPlaced)
        assertEquals(LogicalDifficultyBand.ADVANCED_TECHNIQUES_LIKELY, evidence.band)
        assertTrue(evidence.logicalScore > 1_000)
    }

    @Test
    fun invalidBoardIsRejected() {
        val invalid = SudokuBoard.empty()
            .withValue(0, 0, 5)
            .withValue(0, 1, 5)

        assertFailsWith<IllegalArgumentException> {
            LogicalDifficultyAnalyzer.analyze(invalid)
        }
    }
}
