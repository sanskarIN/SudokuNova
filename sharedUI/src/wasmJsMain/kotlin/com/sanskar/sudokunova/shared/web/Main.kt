package com.sanskar.sudokunova.shared.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "webApp") {
        SudokuNovaSharedApp()
    }
}
