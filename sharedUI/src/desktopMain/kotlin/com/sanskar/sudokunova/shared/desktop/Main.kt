package com.sanskar.sudokunova.shared.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SudokuNova",
    ) {
        SudokuNovaSharedApp()
    }
}
