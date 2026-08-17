package in.sanskar.sudokunova.data

import kotlin.test.Test
import kotlin.test.assertEquals

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
