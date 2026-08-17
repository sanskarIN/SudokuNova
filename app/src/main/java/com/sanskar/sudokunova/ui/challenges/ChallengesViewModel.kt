package com.sanskar.sudokunova.ui.challenges

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.challenge.ChallengeDescriptor
import com.sanskar.sudokunova.data.challenge.ChallengeKeys
import com.sanskar.sudokunova.data.challenge.ChallengeRepository
import com.sanskar.sudokunova.data.challenge.ChallengeResultEntity
import com.sanskar.sudokunova.data.challenge.ChallengeType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ChallengeEntry(
    val descriptor: ChallengeDescriptor,
    val displayDate: LocalDate,
    val result: ChallengeResultEntity?,
    val current: Boolean,
)

data class ChallengesUiState(
    val selectedType: ChallengeType = ChallengeType.DAILY,
    val entries: List<ChallengeEntry> = emptyList(),
)

class ChallengesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChallengeRepository(application.applicationContext)
    private val selectedType = MutableStateFlow(ChallengeType.DAILY)
    private val today = LocalDate.now()

    val uiState: StateFlow<ChallengesUiState> = combine(
        selectedType,
        repository.observeAll(),
    ) { type, results ->
        ChallengesUiState(
            selectedType = type,
            entries = buildEntries(type, results),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChallengesUiState(
            entries = buildEntries(ChallengeType.DAILY, emptyList()),
        ),
    )

    fun selectType(type: ChallengeType) {
        selectedType.value = type
    }

    private fun buildEntries(
        type: ChallengeType,
        results: List<ChallengeResultEntity>,
    ): List<ChallengeEntry> {
        val byKey = results
            .asSequence()
            .filter { it.challengeType == type.name }
            .associateBy { it.challengeKey }

        return when (type) {
            ChallengeType.DAILY -> (0 until DAILY_ARCHIVE_DAYS).map { offset ->
                val date = today.minusDays(offset.toLong())
                val key = ChallengeKeys.daily(date)
                ChallengeEntry(
                    descriptor = ChallengeDescriptor(
                        type = type,
                        key = key,
                        difficulty = ChallengeRepository.difficultyFor(type),
                    ),
                    displayDate = date,
                    result = byKey[key],
                    current = offset == 0,
                )
            }

            ChallengeType.WEEKLY -> {
                val currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                (0 until WEEKLY_ARCHIVE_WEEKS).map { offset ->
                    val weekStart = currentWeekStart.minusWeeks(offset.toLong())
                    val key = ChallengeKeys.weekly(weekStart)
                    ChallengeEntry(
                        descriptor = ChallengeDescriptor(
                            type = type,
                            key = key,
                            difficulty = ChallengeRepository.difficultyFor(type),
                        ),
                        displayDate = weekStart,
                        result = byKey[key],
                        current = offset == 0,
                    )
                }
            }
        }
    }

    private companion object {
        const val DAILY_ARCHIVE_DAYS = 31
        const val WEEKLY_ARCHIVE_WEEKS = 13
    }
}
