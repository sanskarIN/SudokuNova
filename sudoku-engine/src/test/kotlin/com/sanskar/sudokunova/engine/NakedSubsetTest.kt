package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NakedSubsetTest {
    private val row = (0 until 9).toList()

    @Test
    fun detectsDeterministicNakedPairAndOnlyItsExternalEliminations() {
        val candidates = mapOf(
            0 to setOf(1, 2),
            1 to setOf(1, 2),
            2 to setOf(1, 3),
            3 to setOf(3, 4),
        )

        val match = findNakedSubset(row, candidates, subsetSize = 2)

        assertEquals(listOf(0, 1), match?.sourceCells)
        assertEquals(setOf(1, 2), match?.values)
        assertEquals(listOf(CandidateElimination(2, 1)), match?.eliminations)
    }

    @Test
    fun detectsNakedTripleFromThreeDifferentCandidatePairs() {
        val candidates = mapOf(
            0 to setOf(1, 2),
            1 to setOf(1, 3),
            2 to setOf(2, 3),
            3 to setOf(1, 2, 3, 4),
            4 to setOf(3, 5),
            5 to setOf(6, 7),
        )

        val match = findNakedSubset(row, candidates, subsetSize = 3)

        assertEquals(listOf(0, 1, 2), match?.sourceCells)
        assertEquals(setOf(1, 2, 3), match?.values)
        assertEquals(
            listOf(
                CandidateElimination(3, 1),
                CandidateElimination(3, 2),
                CandidateElimination(3, 3),
                CandidateElimination(4, 3),
            ),
            match?.eliminations,
        )
    }

    @Test
    fun tripleCanIncludeAThreeCandidateSourceCell() {
        val candidates = mapOf(
            0 to setOf(1, 2),
            1 to setOf(1, 2, 3),
            2 to setOf(2, 3),
            3 to setOf(1, 4),
        )

        val match = findNakedSubset(row, candidates, subsetSize = 3)

        assertEquals(listOf(0, 1, 2), match?.sourceCells)
        assertEquals(setOf(1, 2, 3), match?.values)
        assertEquals(listOf(CandidateElimination(3, 1)), match?.eliminations)
    }

    @Test
    fun doesNotInventTripleWhenThreeCellsSpanFourValues() {
        val candidates = mapOf(
            0 to setOf(1, 2),
            1 to setOf(2, 3),
            2 to setOf(3, 4),
            3 to setOf(1, 4, 5),
        )

        assertNull(findNakedSubset(row, candidates, subsetSize = 3))
    }

    @Test
    fun subsetWithoutExternalEliminationIsNotReportedAsTeachingStep() {
        val candidates = mapOf(
            0 to setOf(1, 2),
            1 to setOf(1, 3),
            2 to setOf(2, 3),
            3 to setOf(4, 5),
        )

        assertNull(findNakedSubset(row, candidates, subsetSize = 3))
    }
}
