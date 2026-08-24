package com.sanskar.sudokunova.shared.web

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.rememberPersistedSharedGameState

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "webApp") {
        val gameStore = remember {
            EncodedSharedGameStore(WebLocalStorageGameTextStore())
        }
        val state = rememberPersistedSharedGameState(gameStore)
        SudokuNovaSharedApp(state = state)
    }
}
