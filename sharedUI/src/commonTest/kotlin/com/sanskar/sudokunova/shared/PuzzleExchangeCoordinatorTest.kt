package com.sanskar.sudokunova.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PuzzleExchangeCoordinatorTest {
    private class FakePlatform : PuzzleExchangePlatform {
        var copied: String? = null
        var shared: String? = null
        var exported: Pair<String, String>? = null
        var pasted: PuzzleExchangeTextResult = PuzzleExchangeTextResult.Empty
        var imported: ((String) -> Unit)? = null

        override fun copyText(text: String): PuzzleExchangeResult {
            copied = text
            return PuzzleExchangeResult.Success
        }

        override fun pasteText(): PuzzleExchangeTextResult = pasted

        override fun shareText(text: String): PuzzleExchangeResult {
            shared = text
            return PuzzleExchangeResult.Success
        }

        override fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult {
            imported = onText
            return PuzzleExchangeResult.Success
        }

        override fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult {
            exported = fileName to text
            return PuzzleExchangeResult.Success
        }
    }

    @Test
    fun pasteDeliversOnlyBoundedCode() {
        val platform = FakePlatform().apply {
            pasted = PuzzleExchangeTextResult.Success("SNP1:example")
        }
        val coordinator = PuzzleExchangeCoordinator(platform)
        var received: String? = null

        val result = coordinator.pastePuzzleCode { received = it }

        assertEquals(PuzzleExchangeTextResult.Success("SNP1:example"), result)
        assertEquals("SNP1:example", received)
    }

    @Test
    fun oversizedPasteIsRejectedByCoordinator() {
        val platform = FakePlatform().apply {
            pasted = PuzzleExchangeTextResult.Success("x".repeat(PuzzleExchangeLimits.MAX_CODE_LENGTH + 1))
        }
        val coordinator = PuzzleExchangeCoordinator(platform)
        var received: String? = null

        val result = coordinator.pastePuzzleCode { received = it }

        assertTrue(result is PuzzleExchangeTextResult.Failed)
        assertEquals(null, received)
    }

    @Test
    fun exportUsesDefaultSnp1FileName() {
        val platform = FakePlatform()
        val coordinator = PuzzleExchangeCoordinator(platform)

        val result = coordinator.exportCurrentPuzzle("SNP1:example")

        assertEquals(PuzzleExchangeResult.Success, result)
        assertEquals(
            PuzzleExchangeLimits.DEFAULT_FILE_NAME to "SNP1:example",
            platform.exported,
        )
    }

    @Test
    fun importCallbackFiltersOversizedFilePayload() {
        val platform = FakePlatform()
        val coordinator = PuzzleExchangeCoordinator(platform)
        var received: String? = null

        assertEquals(PuzzleExchangeResult.Success, coordinator.importPuzzleFile { received = it })
        platform.imported?.invoke("x".repeat(PuzzleExchangeLimits.MAX_FILE_TEXT_LENGTH + 1))

        assertEquals(null, received)
    }
}
