package com.sanskar.sudokunova.shared.web

import com.sanskar.sudokunova.shared.SHARED_ACTIVE_GAME_STORAGE_KEY
import com.sanskar.sudokunova.shared.SharedGameTextStore
import kotlinx.browser.window

class WebLocalStorageGameTextStore(
    private val key: String = SHARED_ACTIVE_GAME_STORAGE_KEY,
) : SharedGameTextStore {
    override suspend fun read(): String? = window.localStorage.getItem(key)

    override suspend fun write(value: String) {
        window.localStorage.setItem(key, value)
    }

    override suspend fun clear() {
        window.localStorage.removeItem(key)
    }
}
