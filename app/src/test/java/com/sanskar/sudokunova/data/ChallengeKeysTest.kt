package com.sanskar.sudokunova.data

import com.sanskar.sudokunova.data.challenge.ChallengeDescriptor
import com.sanskar.sudokunova.data.challenge.ChallengeKeys
import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.engine.Difficulty
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ChallengeKeysTest {
    @Test
    fun dailyKeyUsesEpochDay() {
        val date = LocalDate.of(2026, 8, 17)
        assertEquals(date.toEpochDay(), ChallengeKeys.daily(date))
    }

    @Test
    fun datesInSameIsoWeekShareWeeklyKey() {
        assertEquals(
            ChallengeKeys.weekly(LocalDate.of(2026, 8, 17)),
            ChallengeKeys.weekly(LocalDate.of(2026, 8, 23)),
        )
    }

    @Test
    fun challengeSeedIsStableAndTypeSeparated() {
        val daily = ChallengeDescriptor(ChallengeType.DAILY, 20_000L, Difficulty.MEDIUM)
        val weekly = ChallengeDescriptor(ChallengeType.WEEKLY, 20_000L, Difficulty.MEDIUM)

        assertEquals(ChallengeKeys.seed(daily), ChallengeKeys.seed(daily))
        assertNotEquals(ChallengeKeys.seed(daily), ChallengeKeys.seed(weekly))
    }
}
