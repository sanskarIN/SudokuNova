package com.sanskar.sudokunova.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Restore shared user settings once and persist later observable changes.
 *
 * Settings storage is intentionally best-effort. Corrupt or unavailable storage does not
 * prevent gameplay; the portable defaults remain active until a valid settings payload exists.
 */
@Composable
fun rememberPersistedSharedSettingsState(
    store: SharedSettingsStore,
): SharedSettingsState {
    val state = remember { SharedSettingsState() }
    var restoreFinished by remember(store) { mutableStateOf(false) }

    LaunchedEffect(store) {
        runCatching { state.restoreFrom(store) }
        restoreFinished = true
    }

    val settings = state.settings
    LaunchedEffect(store, restoreFinished, settings) {
        if (restoreFinished) {
            runCatching { state.saveTo(store) }
        }
    }

    return state
}
