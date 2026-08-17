package in.sanskar.sudokunova.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import in.sanskar.sudokunova.data.AppPreferencesRepository
import in.sanskar.sudokunova.game.GameStateCodec
import in.sanskar.sudokunova.game.GameStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppPreferencesRepository(application.applicationContext)

    val hasActiveGame = repository.activeGame
        .map { encoded ->
            encoded?.let(GameStateCodec::decode)?.status == GameStatus.PLAYING
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
