package com.sanskar.sudokunova.data

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerStatisticsTest {
    @Test
    fun completionRateIsZeroWithoutStartedGames() {
        assertEquals(0, PlayerStatistics().completionRate)
    }

    @Test
    fun completionRateUsesStartedAndCompletedGames() {
        val statistics = PlayerStatistics(gamesStarted = 8, gamesCompleted = 6)
        assertEquals(75, statistics.completionRate)
    }
}
