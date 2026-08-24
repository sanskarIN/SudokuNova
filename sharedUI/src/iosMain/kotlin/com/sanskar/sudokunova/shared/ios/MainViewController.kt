package com.sanskar.sudokunova.shared.ios

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.rememberPersistedSharedGameState
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val gameStore = remember {
        EncodedSharedGameStore(AppleUserDefaultsGameTextStore())
    }
    val state = rememberPersistedSharedGameState(gameStore)
    SudokuNovaSharedApp(state = state)
}
