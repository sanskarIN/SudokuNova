package com.sanskar.sudokunova.shared.desktop

import com.sanskar.sudokunova.shared.PuzzleExchangeLimits
import com.sanskar.sudokunova.shared.PuzzleExchangePlatform
import com.sanskar.sudokunova.shared.PuzzleExchangeResult
import com.sanskar.sudokunova.shared.PuzzleExchangeTextResult
import java.awt.FileDialog
import java.awt.Frame
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.File

/** Desktop/JVM clipboard and file exchange adapter. */
class DesktopPuzzleExchangePlatform(
    private val owner: Frame? = null,
) : PuzzleExchangePlatform {
    override fun copyText(text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            return PuzzleExchangeResult.Failed("Puzzle code is too large.")
        }
        return runCatching {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("Clipboard is unavailable.") }
    }

    override fun pasteText(): PuzzleExchangeTextResult = runCatching {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        if (!clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return PuzzleExchangeTextResult.Empty
        }
        val text = clipboard.getData(DataFlavor.stringFlavor) as? String
            ?: return PuzzleExchangeTextResult.Empty
        if (text.isBlank()) PuzzleExchangeTextResult.Empty
        else if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            PuzzleExchangeTextResult.Failed("Clipboard text is too large.")
        } else {
            PuzzleExchangeTextResult.Success(text)
        }
    }.getOrElse { PuzzleExchangeTextResult.Failed("Clipboard is unavailable.") }

    override fun shareText(text: String): PuzzleExchangeResult =
        PuzzleExchangeResult.Unsupported

    override fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult {
        val dialog = FileDialog(owner, "Import SudokuNova puzzle", FileDialog.LOAD)
        return runCatching {
            dialog.isVisible = true
            val directory = dialog.directory
            val fileName = dialog.file
            if (directory == null || fileName == null) return PuzzleExchangeResult.Failed("No file selected.")
            val text = File(directory, fileName).readText()
            if (!PuzzleExchangeLimits.isBoundedFileText(text)) {
                return PuzzleExchangeResult.Failed("Puzzle file is too large.")
            }
            onText(text)
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("Unable to read puzzle file.") }
    }

    override fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedFileText(text)) {
            return PuzzleExchangeResult.Failed("Puzzle file is too large.")
        }
        val dialog = FileDialog(owner, "Export SudokuNova puzzle", FileDialog.SAVE)
        return runCatching {
            dialog.file = fileName
            dialog.isVisible = true
            val directory = dialog.directory
            val selectedName = dialog.file
            if (directory == null || selectedName == null) {
                return PuzzleExchangeResult.Failed("No destination selected.")
            }
            File(directory, selectedName).writeText(text)
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("Unable to write puzzle file.") }
    }
}
