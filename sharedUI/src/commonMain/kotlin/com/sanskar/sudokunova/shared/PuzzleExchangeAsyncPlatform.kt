package com.sanskar.sudokunova.shared

/**
 * Additive asynchronous exchange boundary for platform APIs that complete
 * through callbacks, events, or promises rather than synchronously.
 *
 * The existing [PuzzleExchangePlatform] remains unchanged for synchronous
 * targets. Implementations must invoke exactly one callback for every
 * accepted operation and must never report completion before the underlying
 * platform operation has completed.
 */
interface PuzzleExchangeAsyncPlatform {
    fun copyText(
        text: String,
        onResult: (PuzzleExchangeAsyncResult) -> Unit,
    )

    fun pasteText(
        onResult: (PuzzleExchangeAsyncTextResult) -> Unit,
    )

    fun shareText(
        text: String,
        onResult: (PuzzleExchangeAsyncResult) -> Unit,
    )

    fun importTextFile(
        onResult: (PuzzleExchangeAsyncTextResult) -> Unit,
    )

    fun exportTextFile(
        fileName: String,
        text: String,
        onResult: (PuzzleExchangeAsyncResult) -> Unit,
    )
}

sealed interface PuzzleExchangeAsyncResult {
    data object Success : PuzzleExchangeAsyncResult
    data class Failed(val reason: String) : PuzzleExchangeAsyncResult
    data object Unsupported : PuzzleExchangeAsyncResult
}

sealed interface PuzzleExchangeAsyncTextResult {
    data class Success(val text: String) : PuzzleExchangeAsyncTextResult
    data class Failed(val reason: String) : PuzzleExchangeAsyncTextResult
    data object Empty : PuzzleExchangeAsyncTextResult
    data object Unsupported : PuzzleExchangeAsyncTextResult
}

fun PuzzleExchangeAsyncPlatform.safeCopyText(
    text: String,
    onResult: (PuzzleExchangeAsyncResult) -> Unit,
) {
    if (!PuzzleExchangeLimits.isBoundedCode(text)) {
        onResult(PuzzleExchangeAsyncResult.Failed("Puzzle code is too large."))
        return
    }
    copyText(text, onResult)
}

fun PuzzleExchangeAsyncPlatform.safeShareText(
    text: String,
    onResult: (PuzzleExchangeAsyncResult) -> Unit,
) {
    if (!PuzzleExchangeLimits.isBoundedCode(text)) {
        onResult(PuzzleExchangeAsyncResult.Failed("Puzzle code is too large."))
        return
    }
    shareText(text, onResult)
}

fun PuzzleExchangeAsyncPlatform.safePasteText(
    onResult: (PuzzleExchangeAsyncTextResult) -> Unit,
) {
    pasteText { result ->
        onResult(
            when (result) {
                is PuzzleExchangeAsyncTextResult.Success ->
                    if (PuzzleExchangeLimits.isBoundedCode(result.text)) {
                        result
                    } else {
                        PuzzleExchangeAsyncTextResult.Failed("Puzzle code is too large.")
                    }
                else -> result
            },
        )
    }
}

fun PuzzleExchangeAsyncPlatform.safeImportTextFile(
    onResult: (PuzzleExchangeAsyncTextResult) -> Unit,
) {
    importTextFile { result ->
        onResult(
            when (result) {
                is PuzzleExchangeAsyncTextResult.Success ->
                    if (PuzzleExchangeLimits.isBoundedFileText(result.text)) {
                        result
                    } else {
                        PuzzleExchangeAsyncTextResult.Failed("Puzzle file is too large.")
                    }
                else -> result
            },
        )
    }
}

fun PuzzleExchangeAsyncPlatform.safeExportTextFile(
    fileName: String,
    text: String,
    onResult: (PuzzleExchangeAsyncResult) -> Unit,
) {
    if (!PuzzleExchangeLimits.isBoundedFileText(text)) {
        onResult(PuzzleExchangeAsyncResult.Failed("Puzzle file is too large."))
        return
    }
    exportTextFile(fileName, text, onResult)
}
