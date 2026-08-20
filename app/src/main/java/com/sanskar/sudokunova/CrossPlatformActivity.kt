package com.sanskar.sudokunova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp

/**
 * Android host for the same Compose Multiplatform UI used by Desktop, iOS, and Web.
 *
 * The existing [MainActivity] remains the production Android launcher so Android-only
 * features are not removed while feature parity is migrated incrementally.
 */
class CrossPlatformActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuNovaSharedApp()
        }
    }
}
