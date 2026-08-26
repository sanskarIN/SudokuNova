package com.sanskar.sudokunova.shared

object SharedSettingsCodec {
    private const val VERSION = "SNS1"
    private const val MAX_PAYLOAD_LENGTH = 512

    private val fieldNames = listOf(
        "theme",
        "dynamicColor",
        "inputMode",
        "highlightPeers",
        "highlightSameNumbers",
        "autoCheckMistakes",
        "autoRemoveNotes",
        "showTimer",
        "haptics",
        "sounds",
        "reducedMotion",
        "highContrast",
        "mistakeLimit",
    )

    fun encode(settings: SharedUserSettings): String = buildString {
        append(VERSION)
        append("|theme=").append(settings.theme.name)
        append("|dynamicColor=").append(settings.dynamicColor.asToken())
        append("|inputMode=").append(settings.inputMode.name)
        append("|highlightPeers=").append(settings.highlightPeers.asToken())
        append("|highlightSameNumbers=").append(settings.highlightSameNumbers.asToken())
        append("|autoCheckMistakes=").append(settings.autoCheckMistakes.asToken())
        append("|autoRemoveNotes=").append(settings.autoRemoveNotes.asToken())
        append("|showTimer=").append(settings.showTimer.asToken())
        append("|haptics=").append(settings.haptics.asToken())
        append("|sounds=").append(settings.sounds.asToken())
        append("|reducedMotion=").append(settings.reducedMotion.asToken())
        append("|highContrast=").append(settings.highContrast.asToken())
        append("|mistakeLimit=").append(settings.mistakeLimit)
    }.also(::requireBounded)

    fun decode(payload: String): SharedUserSettings {
        requireBounded(payload)
        val tokens = payload.split('|')
        require(tokens.firstOrNull() == VERSION) { "Unsupported shared settings version." }
        require(tokens.size == fieldNames.size + 1) { "Shared settings field count is invalid." }

        val values = linkedMapOf<String, String>()
        tokens.drop(1).forEach { token ->
            val separator = token.indexOf('=')
            require(separator > 0 && separator < token.lastIndex) { "Malformed shared settings field." }
            val key = token.substring(0, separator)
            val value = token.substring(separator + 1)
            require(key in fieldNames) { "Unknown shared settings field: $key" }
            require(values.put(key, value) == null) { "Duplicate shared settings field: $key" }
        }
        require(values.keys == fieldNames.toSet()) { "Shared settings fields are incomplete." }

        return SharedUserSettings(
            theme = parseEnum(values.getValue("theme"), SharedTheme.entries),
            dynamicColor = parseBoolean(values.getValue("dynamicColor")),
            inputMode = parseEnum(values.getValue("inputMode"), SharedInputMode.entries),
            highlightPeers = parseBoolean(values.getValue("highlightPeers")),
            highlightSameNumbers = parseBoolean(values.getValue("highlightSameNumbers")),
            autoCheckMistakes = parseBoolean(values.getValue("autoCheckMistakes")),
            autoRemoveNotes = parseBoolean(values.getValue("autoRemoveNotes")),
            showTimer = parseBoolean(values.getValue("showTimer")),
            haptics = parseBoolean(values.getValue("haptics")),
            sounds = parseBoolean(values.getValue("sounds")),
            reducedMotion = parseBoolean(values.getValue("reducedMotion")),
            highContrast = parseBoolean(values.getValue("highContrast")),
            mistakeLimit = values.getValue("mistakeLimit").toIntOrNull()
                ?: throw IllegalArgumentException("Mistake limit is not an integer."),
        )
    }

    private fun requireBounded(payload: String): String {
        require(payload.isNotEmpty()) { "Shared settings payload is empty." }
        require(payload.length <= MAX_PAYLOAD_LENGTH) { "Shared settings payload is too large." }
        return payload
    }

    private fun parseBoolean(value: String): Boolean = when (value) {
        "1" -> true
        "0" -> false
        else -> throw IllegalArgumentException("Boolean settings values must be 0 or 1.")
    }

    private fun <T : Enum<T>> parseEnum(value: String, entries: List<T>): T =
        entries.firstOrNull { it.name == value }
            ?: throw IllegalArgumentException("Unknown shared settings enum value: $value")

    private fun Boolean.asToken(): Int = if (this) 1 else 0
}
