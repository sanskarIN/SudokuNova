package com.sanskar.sudokunova.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.history.DifficultyHistorySummary
import com.sanskar.sudokunova.data.history.GameHistoryEntity
import com.sanskar.sudokunova.data.history.HistoryRepository
import com.sanskar.sudokunova.engine.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HistoryScope {
    ALL,
    FAVORITES,
}

data class HistoryUiState(
    val items: List<GameHistoryEntity> = emptyList(),
    val summaries: List<DifficultyHistorySummary> = emptyList(),
    val scope: HistoryScope = HistoryScope.ALL,
    val difficulty: Difficulty? = null,
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HistoryRepository(application.applicationContext)
    private val scopeFilter = MutableStateFlow(HistoryScope.ALL)
    private val difficultyFilter = MutableStateFlow<Difficulty?>(null)

    private val allHistory = repository.observeHistory()
    private val favoriteHistory = repository.observeFavoriteHistory()
    private val summaries = repository.observeDifficultySummaries()

    val uiState: StateFlow<HistoryUiState> = combine(
        scopeFilter,
        difficultyFilter,
        allHistory,
        favoriteHistory,
        summaries,
    ) { scope, difficulty, all, favorites, summaryRows ->
        val source = if (scope == HistoryScope.FAVORITES) favorites else all
        HistoryUiState(
            items = source.filterDifficulty(difficulty),
            summaries = summaryRows,
            scope = scope,
            difficulty = difficulty,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState(),
    )

    fun setScope(scope: HistoryScope) {
        scopeFilter.value = scope
    }

    fun setDifficulty(difficulty: Difficulty?) {
        difficultyFilter.value = difficulty
    }

    fun toggleFavorite(item: GameHistoryEntity) {
        viewModelScope.launch {
            repository.setHistoryFavorite(item.id, !item.isFavorite)
        }
    }

    fun delete(item: GameHistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(item.id)
        }
    }

    private fun List<GameHistoryEntity>.filterDifficulty(difficulty: Difficulty?): List<GameHistoryEntity> =
        if (difficulty == null) this else filter { it.difficulty == difficulty.name }
}
