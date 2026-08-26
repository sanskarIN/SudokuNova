package com.sanskar.sudokunova.shared.web

import com.sanskar.sudokunova.shared.SHARED_SETTINGS_STORAGE_KEY
import com.sanskar.sudokunova.shared.SharedSettingsTextStore
import kotlinx.browser.window

class WebLocalStorageSettingsTextStore(
    private val key: String = SHARED_SETTINGS_STORAGE_KEY,
) : SharedSettingsTextStore {
    override suspend fun read(): String? = window.localStorage.getItem(key)

    override suspend fun write(value: String) {
        window.localStorage.setItem(key, value)
    }

    override suspend fun clear() {
        window.localStorage.removeItem(key)
    }
}
