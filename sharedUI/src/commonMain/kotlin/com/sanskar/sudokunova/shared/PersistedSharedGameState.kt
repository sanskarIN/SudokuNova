package com.sanskar.sudokunova.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Remember one shared game, restore it once from [store], then persist later state changes.
 *
 * Storage failures never replace Sudoku validation rules or make the UI unusable: failed
 * reads leave the fresh in-memory game intact, and failed writes are retried naturally on
 * a later observable snapshot change.
 */
@Composable
fun rememberPersistedSharedGameState(
    store: SharedGameStore,
): SharedGameState {
    val state = remember { SharedGameState() }
    var restoreFinished by remember(store) { mutableStateOf(false) }

    LaunchedEffect(store) {
        runCatching { state.restoreFrom(store) }
        restoreFinished = true
    }

    val snapshot = state.snapshot()
    LaunchedEffect(store, restoreFinished, snapshot) {
        if (restoreFinished) {
            runCatching { state.saveTo(store) }
        }
    }

    return state
}
