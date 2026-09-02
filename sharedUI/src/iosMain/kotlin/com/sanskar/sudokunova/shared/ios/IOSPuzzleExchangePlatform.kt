package com.sanskar.sudokunova.shared.ios

import com.sanskar.sudokunova.shared.PuzzleExchangeLimits
import com.sanskar.sudokunova.shared.PuzzleExchangePlatform
import com.sanskar.sudokunova.shared.PuzzleExchangeResult
import com.sanskar.sudokunova.shared.PuzzleExchangeTextResult
import platform.UIKit.UIPasteboard

/**
 * iOS exchange adapter for synchronous clipboard operations.
 *
 * Share and document operations remain host-owned because UIKit presents those
 * controllers asynchronously and the common exchange contract is synchronous.
 */
class IOSPuzzleExchangePlatform : PuzzleExchangePlatform {
    override fun copyText(text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            return PuzzleExchangeResult.Failed("Puzzle code is too large.")
        }
        return runCatching {
            UIPasteboard.generalPasteboard.string = text
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("Clipboard is unavailable.") }
    }

    override fun pasteText(): PuzzleExchangeTextResult = runCatching {
        val text = UIPasteboard.generalPasteboard.string
            ?: return PuzzleExchangeTextResult.Empty
        if (text.isBlank()) PuzzleExchangeTextResult.Empty
        else if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            PuzzleExchangeTextResult.Failed("Clipboard text is too large.")
        } else {
            PuzzleExchangeTextResult.Success(text)
        }
    }.getOrElse { PuzzleExchangeTextResult.Failed("Clipboard is unavailable.") }

    override fun shareText(text: String): PuzzleExchangeResult =
        if (PuzzleExchangeLimits.isBoundedCode(text)) PuzzleExchangeResult.Unsupported
        else PuzzleExchangeResult.Failed("Puzzle code is too large.")

    override fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult =
        PuzzleExchangeResult.Unsupported

    override fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult =
        if (PuzzleExchangeLimits.isBoundedFileText(text)) PuzzleExchangeResult.Unsupported
        else PuzzleExchangeResult.Failed("Puzzle file is too large.")
}
