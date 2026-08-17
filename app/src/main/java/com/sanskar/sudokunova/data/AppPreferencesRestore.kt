package com.sanskar.sudokunova.data

suspend fun AppPreferencesRepository.restoreSettings(settings: UserSettings) {
    setTheme(settings.theme)
    setDynamicColor(settings.dynamicColor)
    setInputMode(settings.inputMode)
    setHighlightPeers(settings.highlightPeers)
    setHighlightSameNumbers(settings.highlightSameNumbers)
    setAutoCheck(settings.autoCheckMistakes)
    setAutoRemoveNotes(settings.autoRemoveNotes)
    setShowTimer(settings.showTimer)
    setHaptics(settings.haptics)
    setSounds(settings.sounds)
    setReducedMotion(settings.reducedMotion)
    setHighContrast(settings.highContrast)
    setMistakeLimit(settings.mistakeLimit)
}
