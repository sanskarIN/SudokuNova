package com.sanskar.sudokunova.shared.ios

import com.sanskar.sudokunova.shared.SHARED_ACTIVE_GAME_STORAGE_KEY
import com.sanskar.sudokunova.shared.SharedGameTextStore
import platform.Foundation.NSUserDefaults

class AppleUserDefaultsGameTextStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
    private val key: String = SHARED_ACTIVE_GAME_STORAGE_KEY,
) : SharedGameTextStore {
    override suspend fun read(): String? = defaults.stringForKey(key)

    override suspend fun write(value: String) {
        defaults.setObject(value, forKey = key)
    }

    override suspend fun clear() {
        defaults.removeObjectForKey(key)
    }
}
