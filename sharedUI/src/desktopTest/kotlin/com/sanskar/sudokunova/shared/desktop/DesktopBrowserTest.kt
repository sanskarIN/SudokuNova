package com.sanskar.sudokunova.shared.desktop

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopBrowserTest {
    @Test
    fun developerWebsiteAndSourceRepositoryAreApproved() {
        assertTrue(DesktopBrowser.isApprovedLink("https://sanskarin.github.io"))
        assertTrue(DesktopBrowser.isApprovedLink("https://github.com/sanskarIN/SudokuNova"))
    }

    @Test
    fun unsafeOrUnrecognizedDestinationsAreRejected() {
        for (url in listOf(
            "http://sanskarin.github.io",
            "https://sanskarin.github.io.evil.example",
            "file:///C:/Windows/System32",
            "javascript:alert(1)",
            "https://example.com",
            "https://github.com/sanskarIN/SudokuNova/../../other",
        )) {
            assertFalse(DesktopBrowser.isApprovedLink(url))
            assertFalse(DesktopBrowser.openApprovedLink(url))
        }
    }
}
