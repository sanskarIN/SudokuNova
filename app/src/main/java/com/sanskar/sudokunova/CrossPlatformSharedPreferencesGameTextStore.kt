package com.sanskar.sudokunova

import android.content.Context
import com.sanskar.sudokunova.shared.SHARED_ACTIVE_GAME_STORAGE_KEY
import com.sanskar.sudokunova.shared.SharedGameTextStore

class CrossPlatformSharedPreferencesGameTextStore(
    context: Context,
    private val key: String = SHARED_ACTIVE_GAME_STORAGE_KEY,
) : SharedGameTextStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override suspend fun read(): String? = preferences.getString(key, null)

    override suspend fun write(value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override suspend fun clear() {
        preferences.edit().remove(key).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "sudokunova_shared_game"
    }
}
