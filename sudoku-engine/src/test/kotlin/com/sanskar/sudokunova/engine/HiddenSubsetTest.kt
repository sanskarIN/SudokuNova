package com.sanskar.sudokunova.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HiddenSubsetTest {
    private val row = (0 until 9).toList()

    @Test
    fun detectsHiddenPairAndRemovesOtherCandidatesFromItsCells() {
        val candidates = mapOf(
            0 to setOf(1, 2, 4),
            1 to setOf(1, 2, 5),
            2 to setOf(3, 4, 5),
            3 to setOf(3, 6),
        )

        val match = findHiddenSubset(row, candidates, subsetSize = 2)

        assertEquals(listOf(0, 1), match?.sourceCells)
        assertEquals(setOf(1, 2), match?.values)
        assertEquals(
            listOf(
                CandidateElimination(0, 4),
                CandidateElimination(1, 5),
            ),
            match?.eliminations,
        )
    }

    @Test
    fun detectsHiddenTripleAcrossExactlyThreeCells() {
        val candidates = mapOf(
            0 to setOf(1, 2, 4),
            1 to setOf(1, 3, 5),
            2 to setOf(2, 3, 6),
            3 to setOf(4, 5, 6, 7),
            4 to setOf(7, 8),
        )

        val match = findHiddenSubset(row, candidates, subsetSize = 3)

        assertEquals(listOf(0, 1, 2), match?.sourceCells)
        assertEquals(setOf(1, 2, 3), match?.values)
        assertEquals(
            listOf(
                CandidateElimination(0, 4),
                CandidateElimination(1, 5),
                CandidateElimination(2, 6),
            ),
            match?.eliminations,
        )
    }

    @Test
    fun doesNotInventHiddenPairWhenValuesOccupyThreeCells() {
        val candidates = mapOf(
            0 to setOf(1, 2, 4),
            1 to setOf(1, 2, 5),
            2 to setOf(1, 3, 6),
            3 to setOf(2, 3, 7),
        )

        assertNull(findHiddenSubset(row, candidates, subsetSize = 2))
    }

    @Test
    fun hiddenSubsetWithoutAnyCandidateRemovalIsNotReported() {
        val candidates = mapOf(
            0 to setOf(1, 2),
            1 to setOf(1, 2),
            2 to setOf(3, 4),
        )

        assertNull(findHiddenSubset(row, candidates, subsetSize = 2))
    }
}
