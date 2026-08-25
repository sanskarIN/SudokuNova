package com.sanskar.sudokunova.shared

const val SHARED_SETTINGS_STORAGE_KEY = "sudokunova.shared.settings.v1"

interface SharedSettingsStore {
    suspend fun load(): SharedUserSettings?

    suspend fun save(settings: SharedUserSettings)

    suspend fun clear()
}

interface SharedSettingsTextStore {
    suspend fun read(): String?

    suspend fun write(value: String)

    suspend fun clear()
}

class EncodedSharedSettingsStore(
    private val textStore: SharedSettingsTextStore,
) : SharedSettingsStore {
    override suspend fun load(): SharedUserSettings? =
        textStore.read()?.let(SharedSettingsCodec::decode)

    override suspend fun save(settings: SharedUserSettings) {
        textStore.write(SharedSettingsCodec.encode(settings))
    }

    override suspend fun clear() {
        textStore.clear()
    }
}
