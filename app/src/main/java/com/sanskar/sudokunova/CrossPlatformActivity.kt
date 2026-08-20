package com.sanskar.sudokunova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sanskar.sudokunova.shared.SudokuNovaSharedApp

/**
 * Android host for the same Compose Multiplatform UI used by Desktop, iOS, and Web.
 *
 * [MainActivity] remains the production launcher while Android-only capabilities are
 * migrated behind multiplatform abstractions without reducing the mature Android app.
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
