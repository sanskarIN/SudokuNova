package com.sanskar.sudokunova.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SharedSettingsCodecTest {
    @Test
    fun defaultsEncodeDeterministicallyAndRoundTrip() {
        val settings = SharedUserSettings()
        val encoded = SharedSettingsCodec.encode(settings)

        assertEquals(
            "SNS1|theme=SYSTEM|dynamicColor=1|inputMode=CELL_FIRST|highlightPeers=1|highlightSameNumbers=1|autoCheckMistakes=1|autoRemoveNotes=1|showTimer=1|haptics=1|sounds=0|reducedMotion=0|highContrast=0|mistakeLimit=3",
            encoded,
        )
        assertEquals(settings, SharedSettingsCodec.decode(encoded))
    }

    @Test
    fun nonDefaultSettingsRoundTrip() {
        val settings = SharedUserSettings(
            theme = SharedTheme.DARK,
            dynamicColor = false,
            inputMode = SharedInputMode.NUMBER_FIRST,
            highlightPeers = false,
            highlightSameNumbers = false,
            autoCheckMistakes = false,
            autoRemoveNotes = false,
            showTimer = false,
            haptics = false,
            sounds = true,
            reducedMotion = true,
            highContrast = true,
            mistakeLimit = 5,
        )

        assertEquals(settings, SharedSettingsCodec.decode(SharedSettingsCodec.encode(settings)))
    }

    @Test
    fun malformedOrUnsupportedPayloadsFailClosed() {
        val valid = SharedSettingsCodec.encode(SharedUserSettings())

        assertFailsWith<IllegalArgumentException> { SharedSettingsCodec.decode("") }
        assertFailsWith<IllegalArgumentException> { SharedSettingsCodec.decode(valid.replaceFirst("SNS1", "SNS2")) }
        assertFailsWith<IllegalArgumentException> { SharedSettingsCodec.decode(valid.replace("dynamicColor=1", "dynamicColor=yes")) }
        assertFailsWith<IllegalArgumentException> { SharedSettingsCodec.decode(valid.replace("theme=SYSTEM", "theme=UNKNOWN")) }
        assertFailsWith<IllegalArgumentException> { SharedSettingsCodec.decode(valid.replace("mistakeLimit=3", "mistakeLimit=4")) }
        assertFailsWith<IllegalArgumentException> { SharedSettingsCodec.decode(valid.substringBeforeLast('|')) }
        assertFailsWith<IllegalArgumentException> {
            SharedSettingsCodec.decode(valid + "|theme=SYSTEM")
        }
        assertFailsWith<IllegalArgumentException> {
            SharedSettingsCodec.decode("SNS1|" + "x".repeat(600))
        }
    }

    @Test
    fun settingsModelRejectsUnsupportedMistakeLimits() {
        assertFailsWith<IllegalArgumentException> { SharedUserSettings(mistakeLimit = -1) }
        assertFailsWith<IllegalArgumentException> { SharedUserSettings(mistakeLimit = 1) }
        assertEquals(0, SharedUserSettings(mistakeLimit = 0).mistakeLimit)
    }
}
