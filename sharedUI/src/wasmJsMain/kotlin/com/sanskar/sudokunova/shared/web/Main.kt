package com.sanskar.sudokunova.shared.web

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.EncodedSharedSettingsStore
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.rememberPersistedSharedGameState
import com.sanskar.sudokunova.shared.rememberPersistedSharedSettingsState

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "webApp") {
        val gameStore = remember {
            EncodedSharedGameStore(WebLocalStorageGameTextStore())
        }
        val settingsStore = remember {
            EncodedSharedSettingsStore(WebLocalStorageSettingsTextStore())
        }
        val state = rememberPersistedSharedGameState(gameStore)
        val settingsState = rememberPersistedSharedSettingsState(settingsStore)
        val exchangePlatform = remember { WebPuzzleExchangePlatform() }
        SudokuNovaSharedApp(
            state = state,
            settingsState = settingsState,
            exchangePlatform = exchangePlatform,
        )
    }
}
