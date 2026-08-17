package in.sanskar.sudokunova.data

import in.sanskar.sudokunova.ui.theme.AppTheme

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
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
