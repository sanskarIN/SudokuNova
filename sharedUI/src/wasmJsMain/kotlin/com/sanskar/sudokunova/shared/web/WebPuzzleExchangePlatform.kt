package com.sanskar.sudokunova.shared.web

import com.sanskar.sudokunova.shared.PuzzleExchangeLimits
import com.sanskar.sudokunova.shared.PuzzleExchangePlatform
import com.sanskar.sudokunova.shared.PuzzleExchangeResult
import com.sanskar.sudokunova.shared.PuzzleExchangeTextResult
import kotlinx.browser.window

/** Browser exchange seam; hosts can progressively add file/share APIs without changing common UI. */
class WebPuzzleExchangePlatform : PuzzleExchangePlatform {
    override fun copyText(text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            return PuzzleExchangeResult.Failed("Puzzle code is too large.")
        }
        return runCatching {
            window.navigator.clipboard.writeText(text)
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("Clipboard is unavailable in this browser.") }
    }

    override fun pasteText(): PuzzleExchangeTextResult = PuzzleExchangeTextResult.Unsupported

    override fun shareText(text: String): PuzzleExchangeResult =
        if (PuzzleExchangeLimits.isBoundedCode(text)) PuzzleExchangeResult.Unsupported
        else PuzzleExchangeResult.Failed("Puzzle code is too large.")

    override fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult =
        PuzzleExchangeResult.Unsupported

    override fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult =
        if (PuzzleExchangeLimits.isBoundedFileText(text)) PuzzleExchangeResult.Unsupported
        else PuzzleExchangeResult.Failed("Puzzle file is too large.")
}
