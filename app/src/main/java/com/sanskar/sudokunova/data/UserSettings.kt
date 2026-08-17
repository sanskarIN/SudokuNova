package com.sanskar.sudokunova.data

import com.sanskar.sudokunova.ui.theme.AppTheme

enum class InputMode {
    CELL_FIRST,
    NUMBER_FIRST,
}

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val inputMode: InputMode = InputMode.CELL_FIRST,
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
)
