package com.sanskar.sudokunova.shared.desktop

import java.awt.Desktop
import java.net.URI

/** External URLs that can be opened from the desktop Help menu. */
internal object DesktopBrowser {
    const val DEVELOPER_WEBSITE = "https://sanskarin.github.io"
    const val SOURCE_REPOSITORY = "https://github.com/sanskarIN/SudokuNova"

    fun isApprovedLink(value: String): Boolean =
        value == DEVELOPER_WEBSITE || value == SOURCE_REPOSITORY

    /** Do not launch arbitrary URLs, and report unsupported desktop/browser environments. */
    fun openApprovedLink(value: String): Boolean {
        if (!isApprovedLink(value)) return false
        return runCatching {
            if (!Desktop.isDesktopSupported()) return false
            val desktop = Desktop.getDesktop()
            if (!desktop.isSupported(Desktop.Action.BROWSE)) return false
            desktop.browse(URI(value))
            true
        }.getOrDefault(false)
    }
}
