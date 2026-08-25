package com.sanskar.sudokunova.shared

enum class SharedTheme {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class SharedInputMode {
    CELL_FIRST,
    NUMBER_FIRST,
}

data class SharedUserSettings(
    val theme: SharedTheme = SharedTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val inputMode: SharedInputMode = SharedInputMode.CELL_FIRST,
    val highlightPeers: Boolean = true,
    val highlightSameNumbers: Boolean = true,
    val autoCheckMistakes: Boolean = true,
    val autoRemoveNotes: Boolean = true,
    val showTimer: Boolean = true,
    val haptics: Boolean = true,
    val sounds: Boolean = false,
    val reducedMotion: Boolean = false,
    val highContrast: Boolean = false,
    val mistakeLimit: Int = 3,
) {
    init {
        require(mistakeLimit in ALLOWED_MISTAKE_LIMITS) {
            "Mistake limit must be unlimited (0), 3, or 5."
        }
    }

    companion object {
        val ALLOWED_MISTAKE_LIMITS: Set<Int> = setOf(0, 3, 5)
    }
}
