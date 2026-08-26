package com.sanskar.sudokunova.shared.desktop

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.EncodedSharedSettingsStore
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.rememberPersistedSharedGameState
import com.sanskar.sudokunova.shared.rememberPersistedSharedSettingsState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SudokuNova",
    ) {
        val gameStore = remember {
            EncodedSharedGameStore(DesktopPreferencesGameTextStore())
        }
        val settingsStore = remember {
            EncodedSharedSettingsStore(DesktopPreferencesSettingsTextStore())
        }
        val state = rememberPersistedSharedGameState(gameStore)
        val settingsState = rememberPersistedSharedSettingsState(settingsStore)
        SudokuNovaSharedApp(
            state = state,
            settingsState = settingsState,
        )
    }
}
