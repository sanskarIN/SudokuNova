package com.sanskar.sudokunova.shared.desktop

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.EncodedSharedSettingsStore
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.rememberPersistedSharedGameState
import com.sanskar.sudokunova.shared.rememberPersistedSharedSettingsState

/**
 * Desktop-first window size, native menu commands, and keyboard accelerators.
 * Existing local game/settings persistence remains unchanged.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val isMac = System.getProperty("os.name").contains("mac", ignoreCase = true)
    Window(
        onCloseRequest = ::exitApplication,
        title = "SudokuNova",
        state = rememberWindowState(width = 1100.dp, height = 820.dp),
    ) {
        val gameStore = remember {
            EncodedSharedGameStore(DesktopPreferencesGameTextStore())
        }
        val settingsStore = remember {
            EncodedSharedSettingsStore(DesktopPreferencesSettingsTextStore())
        }
        val state = rememberPersistedSharedGameState(gameStore)
        val settingsState = rememberPersistedSharedSettingsState(settingsStore)
        val exchangePlatform = remember { DesktopPuzzleExchangePlatform() }
        var failedWebsite by remember { mutableStateOf<String?>(null) }

        fun openWebsite(url: String) {
            if (!DesktopBrowser.openApprovedLink(url)) failedWebsite = url
        }

        MenuBar {
            Menu("Game", mnemonic = 'G') {
                Item(
                    "New puzzle",
                    onClick = { state.newGame() },
                    shortcut = KeyShortcut(Key.N, ctrl = !isMac, meta = isMac),
                )
                Item(
                    "Undo move",
                    onClick = { state.undo() },
                    shortcut = KeyShortcut(Key.Z, ctrl = !isMac, meta = isMac),
                )
                Item(
                    "Toggle notes",
                    onClick = { state.toggleNotesMode() },
                    shortcut = KeyShortcut(Key.M, ctrl = !isMac, meta = isMac),
                )
                Item(
                    "Hint",
                    onClick = { state.hint() },
                    shortcut = KeyShortcut(Key.H, ctrl = !isMac, meta = isMac),
                )
                Separator()
                Item("Reset puzzle", onClick = { state.reset() })
                Separator()
                Item("Exit", onClick = ::exitApplication)
            }
            Menu("Help", mnemonic = 'H') {
                Item(
                    "Developer website — sanskarin.github.io",
                    onClick = { openWebsite(DesktopBrowser.DEVELOPER_WEBSITE) },
                )
                Item(
                    "SudokuNova on GitHub",
                    onClick = { openWebsite(DesktopBrowser.SOURCE_REPOSITORY) },
                )
            }
        }

        SudokuNovaSharedApp(
            state = state,
            settingsState = settingsState,
            exchangePlatform = exchangePlatform,
        )

        failedWebsite?.let { url ->
            AlertDialog(
                onDismissRequest = { failedWebsite = null },
                title = { Text("Unable to open browser") },
                text = { Text("Open this address in your browser: $url") },
                confirmButton = {
                    TextButton(onClick = { failedWebsite = null }) { Text("OK") }
                },
            )
        }
    }
}
