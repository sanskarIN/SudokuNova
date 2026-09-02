package com.sanskar.sudokunova.shared

/**
 * Platform boundary for moving validated SudokuNova puzzle codes between the
 * application and native text/file/share facilities.
 *
 * Implementations must treat puzzle codes as opaque bounded text. Validation
 * belongs to [SharedGameState.importPuzzleCode]; adapters must never make an
 * unvalidated payload playable.
 */
interface PuzzleExchangePlatform {
    fun copyText(text: String): PuzzleExchangeResult
    fun pasteText(): PuzzleExchangeTextResult
    fun shareText(text: String): PuzzleExchangeResult
    fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult
    fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult
}

sealed interface PuzzleExchangeResult {
    data object Success : PuzzleExchangeResult
    data class Failed(val reason: String) : PuzzleExchangeResult
    data object Unsupported : PuzzleExchangeResult
}

sealed interface PuzzleExchangeTextResult {
    data class Success(val text: String) : PuzzleExchangeTextResult
    data class Failed(val reason: String) : PuzzleExchangeTextResult
    data object Empty : PuzzleExchangeTextResult
    data object Unsupported : PuzzleExchangeTextResult
}

object PuzzleExchangeLimits {
    const val MAX_CODE_LENGTH = 256
    const val MAX_FILE_TEXT_LENGTH = 512
    const val DEFAULT_FILE_NAME = "sudokunova-puzzle.snp1"

    fun isBoundedCode(text: String): Boolean =
        text.length <= MAX_CODE_LENGTH

    fun isBoundedFileText(text: String): Boolean =
        text.length <= MAX_FILE_TEXT_LENGTH
}

fun PuzzleExchangePlatform.safeCopyText(text: String): PuzzleExchangeResult =
    if (PuzzleExchangeLimits.isBoundedCode(text)) copyText(text)
    else PuzzleExchangeResult.Failed("Puzzle code is too large.")

fun PuzzleExchangePlatform.safeShareText(text: String): PuzzleExchangeResult =
    if (PuzzleExchangeLimits.isBoundedCode(text)) shareText(text)
    else PuzzleExchangeResult.Failed("Puzzle code is too large.")
