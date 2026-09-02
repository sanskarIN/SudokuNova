package com.sanskar.sudokunova.shared

/**
 * Small common-layer coordinator that keeps UI actions independent from the
 * concrete Android, Desktop, Web, or Apple exchange implementation.
 */
class PuzzleExchangeCoordinator(
    private val platform: PuzzleExchangePlatform,
) {
    fun copyCurrentPuzzle(code: String): PuzzleExchangeResult =
        platform.safeCopyText(code)

    fun shareCurrentPuzzle(code: String): PuzzleExchangeResult =
        platform.safeShareText(code)

    fun pastePuzzleCode(onText: (String) -> Unit): PuzzleExchangeTextResult {
        return when (val result = platform.pasteText()) {
            is PuzzleExchangeTextResult.Success -> {
                if (PuzzleExchangeLimits.isBoundedCode(result.text)) {
                    onText(result.text)
                    result
                } else {
                    PuzzleExchangeTextResult.Failed("Puzzle code is too large.")
                }
            }
            else -> result
        }
    }

    fun importPuzzleFile(onText: (String) -> Unit): PuzzleExchangeResult =
        platform.importTextFile { text ->
            if (PuzzleExchangeLimits.isBoundedFileText(text)) {
                onText(text)
            }
        }

    fun exportCurrentPuzzle(
        code: String,
        fileName: String = PuzzleExchangeLimits.DEFAULT_FILE_NAME,
    ): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedFileText(code)) {
            return PuzzleExchangeResult.Failed("Puzzle file is too large.")
        }
        return platform.exportTextFile(fileName, code)
    }
}
