package com.sanskar.sudokunova.shared.ios

import com.sanskar.sudokunova.shared.SHARED_SETTINGS_STORAGE_KEY
import com.sanskar.sudokunova.shared.SharedSettingsTextStore
import platform.Foundation.NSUserDefaults

class AppleUserDefaultsSettingsTextStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
    private val key: String = SHARED_SETTINGS_STORAGE_KEY,
) : SharedSettingsTextStore {
    override suspend fun read(): String? = defaults.stringForKey(key)

    override suspend fun write(value: String) {
        defaults.setObject(value, forKey = key)
    }

    override suspend fun clear() {
        defaults.removeObjectForKey(key)
    }
}
