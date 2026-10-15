package com.sanskar.sudokunova.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PuzzleExchangeAsyncPlatformTest {
    @Test
    fun safeCopyRejectsOversizedCodeBeforePlatformCall() {
        val platform = RecordingAsyncPlatform()
        val oversized = "x".repeat(PuzzleExchangeLimits.MAX_CODE_LENGTH + 1)
        var result: PuzzleExchangeAsyncResult? = null

        platform.safeCopyText(oversized) { result = it }

        assertEquals(
            PuzzleExchangeAsyncResult.Failed("Puzzle code is too large."),
            result,
        )
        assertEquals(0, platform.copyCalls)
    }

    @Test
    fun safePasteRejectsOversizedCompletionPayload() {
        val platform = RecordingAsyncPlatform().apply {
            pasteResult = PuzzleExchangeAsyncTextResult.Success(
                "x".repeat(PuzzleExchangeLimits.MAX_CODE_LENGTH + 1),
            )
        }
        var result: PuzzleExchangeAsyncTextResult? = null

        platform.safePasteText { result = it }

        assertEquals(
            PuzzleExchangeAsyncTextResult.Failed("Puzzle code is too large."),
            result,
        )
    }

    @Test
    fun safeImportRejectsOversizedCompletionPayload() {
        val platform = RecordingAsyncPlatform().apply {
            importResult = PuzzleExchangeAsyncTextResult.Success(
                "x".repeat(PuzzleExchangeLimits.MAX_FILE_TEXT_LENGTH + 1),
            )
        }
        var result: PuzzleExchangeAsyncTextResult? = null

        platform.safeImportTextFile { result = it }

        assertEquals(
            PuzzleExchangeAsyncTextResult.Failed("Puzzle file is too large."),
            result,
        )
    }

    @Test
    fun safeExportRejectsOversizedPayloadBeforePlatformCall() {
        val platform = RecordingAsyncPlatform()
        val oversized = "x".repeat(PuzzleExchangeLimits.MAX_FILE_TEXT_LENGTH + 1)
        var result: PuzzleExchangeAsyncResult? = null

        platform.safeExportTextFile("puzzle.snp1", oversized) { result = it }

        assertEquals(
            PuzzleExchangeAsyncResult.Failed("Puzzle file is too large."),
            result,
        )
        assertEquals(0, platform.exportCalls)
    }

    @Test
    fun acceptedCopyDelegatesAndReportsCompletion() {
        val platform = RecordingAsyncPlatform()
        var result: PuzzleExchangeAsyncResult? = null

        platform.safeCopyText("valid") { result = it }

        assertEquals(1, platform.copyCalls)
        assertEquals(PuzzleExchangeAsyncResult.Success, result)
        assertTrue(platform.lastCopiedText == "valid")
    }

    private class RecordingAsyncPlatform : PuzzleExchangeAsyncPlatform {
        var copyCalls = 0
        var exportCalls = 0
        var lastCopiedText: String? = null
        var pasteResult: PuzzleExchangeAsyncTextResult =
            PuzzleExchangeAsyncTextResult.Success("valid")
        var importResult: PuzzleExchangeAsyncTextResult =
            PuzzleExchangeAsyncTextResult.Success("valid")

        override fun copyText(
            text: String,
            onResult: (PuzzleExchangeAsyncResult) -> Unit,
        ) {
            copyCalls += 1
            lastCopiedText = text
            onResult(PuzzleExchangeAsyncResult.Success)
        }

        override fun pasteText(
            onResult: (PuzzleExchangeAsyncTextResult) -> Unit,
        ) = onResult(pasteResult)

        override fun shareText(
            text: String,
            onResult: (PuzzleExchangeAsyncResult) -> Unit,
        ) = onResult(PuzzleExchangeAsyncResult.Success)

        override fun importTextFile(
            onResult: (PuzzleExchangeAsyncTextResult) -> Unit,
        ) = onResult(importResult)

        override fun exportTextFile(
            fileName: String,
            text: String,
            onResult: (PuzzleExchangeAsyncResult) -> Unit,
        ) {
            exportCalls += 1
            onResult(PuzzleExchangeAsyncResult.Success)
        }
    }
}
