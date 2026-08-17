package com.sanskar.sudokunova.ui.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.history.HistoryRepository
import com.sanskar.sudokunova.data.history.SavedPuzzleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SavedPuzzlesUiState(
    val items: List<SavedPuzzleEntity> = emptyList(),
    val favoritesOnly: Boolean = false,
)

class SavedPuzzlesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HistoryRepository(application.applicationContext)
    private val favoritesOnly = MutableStateFlow(false)

    val uiState: StateFlow<SavedPuzzlesUiState> = combine(
        repository.observeSavedPuzzles(),
        repository.observeFavoriteSavedPuzzles(),
        favoritesOnly,
    ) { all, favorites, onlyFavorites ->
        SavedPuzzlesUiState(
            items = if (onlyFavorites) favorites else all,
            favoritesOnly = onlyFavorites,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SavedPuzzlesUiState(),
    )

    fun setFavoritesOnly(value: Boolean) { favoritesOnly.value = value }

    fun toggleFavorite(item: SavedPuzzleEntity) {
        viewModelScope.launch { repository.setSavedPuzzleFavorite(item.id, !item.isFavorite) }
    }

    fun delete(item: SavedPuzzleEntity) {
        viewModelScope.launch { repository.deleteSavedPuzzle(item.id) }
    }
}
