package in.sanskar.sudokunova.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import in.sanskar.sudokunova.data.AppPreferencesRepository
import in.sanskar.sudokunova.data.UserSettings
import in.sanskar.sudokunova.ui.theme.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppPreferencesRepository(application.applicationContext)

    val settings = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettings(),
    )

    fun setTheme(value: AppTheme) = launchUpdate { repository.setTheme(value) }
    fun setDynamicColor(value: Boolean) = launchUpdate { repository.setDynamicColor(value) }
    fun setHighlightPeers(value: Boolean) = launchUpdate { repository.setHighlightPeers(value) }
    fun setHighlightSameNumbers(value: Boolean) = launchUpdate { repository.setHighlightSameNumbers(value) }
    fun setAutoCheck(value: Boolean) = launchUpdate { repository.setAutoCheck(value) }
    fun setAutoRemoveNotes(value: Boolean) = launchUpdate { repository.setAutoRemoveNotes(value) }
    fun setShowTimer(value: Boolean) = launchUpdate { repository.setShowTimer(value) }
    fun setHaptics(value: Boolean) = launchUpdate { repository.setHaptics(value) }
    fun setSounds(value: Boolean) = launchUpdate { repository.setSounds(value) }
    fun setReducedMotion(value: Boolean) = launchUpdate { repository.setReducedMotion(value) }
    fun setHighContrast(value: Boolean) = launchUpdate { repository.setHighContrast(value) }
    fun setMistakeLimit(value: Int) = launchUpdate { repository.setMistakeLimit(value) }
    fun resetStatistics() = launchUpdate { repository.resetStatistics() }

    private fun launchUpdate(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
