package com.sanskar.sudokunova.shared.ios

import androidx.compose.ui.window.ComposeUIViewController
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    SudokuNovaSharedApp()
}
