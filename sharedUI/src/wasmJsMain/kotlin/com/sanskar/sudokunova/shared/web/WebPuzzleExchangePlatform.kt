package com.sanskar.sudokunova.shared.web

import com.sanskar.sudokunova.shared.PuzzleExchangeLimits
import com.sanskar.sudokunova.shared.PuzzleExchangePlatform
import com.sanskar.sudokunova.shared.PuzzleExchangeResult
import com.sanskar.sudokunova.shared.PuzzleExchangeTextResult

/**
 * Browser exchange seam.
 *
 * Browser clipboard, share, and file-picker APIs are Promise/event based, while
 * the current common contract is synchronous. The adapter therefore refuses to
 * report success until the contract is made asynchronous rather than faking a
 * completed operation.
 */
class WebPuzzleExchangePlatform : PuzzleExchangePlatform {
    override fun copyText(text: String): PuzzleExchangeResult =
        if (PuzzleExchangeLimits.isBoundedCode(text)) {
            PuzzleExchangeResult.Unsupported
        } else {
            PuzzleExchangeResult.Failed("Puzzle code is too large.")
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
