package com.sanskar.sudokunova.shared.ios

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.EncodedSharedSettingsStore
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.rememberPersistedSharedGameState
import com.sanskar.sudokunova.shared.rememberPersistedSharedSettingsState
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val gameStore = remember {
        EncodedSharedGameStore(AppleUserDefaultsGameTextStore())
    }
    val settingsStore = remember {
        EncodedSharedSettingsStore(AppleUserDefaultsSettingsTextStore())
    }
    val state = rememberPersistedSharedGameState(gameStore)
    val settingsState = rememberPersistedSharedSettingsState(settingsStore)
    val exchangePlatform = remember { IOSPuzzleExchangePlatform() }
    SudokuNovaSharedApp(
        state = state,
        settingsState = settingsState,
        exchangePlatform = exchangePlatform,
    )
}
