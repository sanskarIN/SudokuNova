package com.sanskar.sudokunova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.sanskar.sudokunova.shared.EncodedSharedGameStore
import com.sanskar.sudokunova.shared.EncodedSharedSettingsStore
import com.sanskar.sudokunova.shared.SharedGameState
import com.sanskar.sudokunova.shared.SharedSettingsState
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp
import com.sanskar.sudokunova.shared.restoreFrom
import com.sanskar.sudokunova.shared.saveTo
import kotlinx.coroutines.launch

/**
 * Android host for the same Compose Multiplatform UI used by Desktop, iOS, and Web.
 *
 * [MainActivity] remains the production launcher while Android-only capabilities are
 * migrated behind multiplatform abstractions without reducing the mature Android app.
 */
class CrossPlatformActivity : ComponentActivity() {
    private val sharedState = SharedGameState()
    private val sharedSettingsState = SharedSettingsState()
    private lateinit var gameStore: EncodedSharedGameStore
    private lateinit var settingsStore: EncodedSharedSettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        gameStore = EncodedSharedGameStore(CrossPlatformSharedPreferencesGameTextStore(this))
        settingsStore = EncodedSharedSettingsStore(CrossPlatformSharedPreferencesSettingsTextStore(this))
        lifecycleScope.launch {
            sharedState.restoreFrom(gameStore)
            runCatching { sharedSettingsState.restoreFrom(settingsStore) }
        }

        setContent {
            SudokuNovaSharedApp(
                state = sharedState,
                settingsState = sharedSettingsState,
            )
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            sharedState.saveTo(gameStore)
            runCatching { sharedSettingsState.saveTo(settingsStore) }
        }
    }
}
