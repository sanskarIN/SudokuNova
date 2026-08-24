package com.sanskar.sudokunova.shared.desktop

import com.sanskar.sudokunova.shared.SHARED_ACTIVE_GAME_STORAGE_KEY
import com.sanskar.sudokunova.shared.SharedGameTextStore
import java.util.prefs.Preferences

class DesktopPreferencesGameTextStore(
    private val preferences: Preferences = Preferences.userRoot().node("in/sanskar/sudokunova"),
    private val key: String = SHARED_ACTIVE_GAME_STORAGE_KEY,
) : SharedGameTextStore {
    override suspend fun read(): String? = preferences.get(key, null)

    override suspend fun write(value: String) {
        preferences.put(key, value)
        preferences.flush()
    }

    override suspend fun clear() {
        preferences.remove(key)
        preferences.flush()
    }
}
