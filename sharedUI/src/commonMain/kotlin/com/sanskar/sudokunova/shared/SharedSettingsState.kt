package com.sanskar.sudokunova.shared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SharedSettingsState(
    initial: SharedUserSettings = SharedUserSettings(),
) {
    var settings by mutableStateOf(initial)
        private set

    fun replace(value: SharedUserSettings) {
        settings = value
    }

    fun update(transform: (SharedUserSettings) -> SharedUserSettings) {
        settings = transform(settings)
    }

    suspend fun restoreFrom(store: SharedSettingsStore): Boolean {
        val restored = store.load() ?: return false
        replace(restored)
        return true
    }

    suspend fun saveTo(store: SharedSettingsStore) {
        store.save(settings)
    }

    suspend fun clearStoredSettings(store: SharedSettingsStore) {
        store.clear()
    }
}
