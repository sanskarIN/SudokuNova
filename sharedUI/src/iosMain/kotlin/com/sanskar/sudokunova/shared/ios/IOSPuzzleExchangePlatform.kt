package com.sanskar.sudokunova.shared.ios

import com.sanskar.sudokunova.shared.PuzzleExchangeLimits
import com.sanskar.sanskar.sudokunova.shared.PuzzleExchangePlatform
import com.sanskar.sudokunova.shared.PuzzleExchangeResult
import com.sanskar.sudokunova.shared.PuzzleExchangeTextResult

/** iOS exchange seam. Native UIKit/SwiftUI hosts provide the concrete transport. */
class IOSPuzzleExchangePlatform : PuzzleExchangePlatform {
    override fun copyText(text: String): PuzzleExchangeResult =
        if (PuzzleExchangeLimits.isBoundedCode(text)) PuzzleExchangeResult.Unsupported
        else PuzzleExchangeResult.Failed("Puzzle code is too large.")

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
