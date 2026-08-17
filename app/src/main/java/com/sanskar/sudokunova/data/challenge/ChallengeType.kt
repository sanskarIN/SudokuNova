package com.sanskar.sudokunova.data.challenge

import com.sanskar.sudokunova.engine.Difficulty
import java.time.LocalDate
import java.time.temporal.IsoFields

enum class ChallengeType {
    DAILY,
    WEEKLY,
}

data class ChallengeDescriptor(
    val type: ChallengeType,
    val key: Long,
    val difficulty: Difficulty,
)

object ChallengeKeys {
    fun daily(date: LocalDate): Long = date.toEpochDay()

    fun weekly(date: LocalDate): Long {
        val weekYear = date.get(IsoFields.WEEK_BASED_YEAR)
        val week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        return weekYear * 100L + week
    }

    fun seed(descriptor: ChallengeDescriptor): Long {
        val typeSalt = when (descriptor.type) {
            ChallengeType.DAILY -> 0x44_41_49_4CL
            ChallengeType.WEEKLY -> 0x57_45_45_4BL
        }
        return descriptor.key * 1_000_003L + descriptor.difficulty.ordinal * 9_973L + typeSalt
    }
}
