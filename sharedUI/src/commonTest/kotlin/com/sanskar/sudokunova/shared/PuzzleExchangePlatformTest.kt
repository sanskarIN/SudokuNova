package com.sanskar.sudokunova.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PuzzleExchangePlatformTest {
    @Test
    fun boundedCodeAllowsNormalPuzzlePayloads() {
        val code = "SNP1.MEDIUM." + "0".repeat(81) + ".00000000"

        assertTrue(PuzzleExchangeLimits.isBoundedCode(code))
    }

    @Test
    fun boundedFileTextAllowsLimitBoundary() {
        assertTrue(
            PuzzleExchangeLimits.isBoundedFileText(
                "x".repeat(PuzzleExchangeLimits.MAX_FILE_TEXT_LENGTH),
            ),
        )
    }

    @Test
    fun oversizedCodeIsRejectedBeforePlatformCall() {
        val platform = RecordingPlatform()
        val result = platform.safeCopyText("x".repeat(PuzzleExchangeLimits.MAX_CODE_LENGTH + 1))

        assertIs<PuzzleExchangeResult.Failed>(result)
        assertEquals(0, platform.copyCalls)
    }

    @Test
    fun oversizedShareIsRejectedBeforePlatformCall() {
        val platform = RecordingPlatform()
        val result = platform.safeShareText("x".repeat(PuzzleExchangeLimits.MAX_CODE_LENGTH + 1))

        assertIs<PuzzleExchangeResult.Failed>(result)
        assertEquals(0, platform.shareCalls)
    }

    @Test
    fun defaultFileNameUsesSnp1Extension() {
        assertTrue(PuzzleExchangeLimits.DEFAULT_FILE_NAME.endsWith(".snp1"))
    }

    private class RecordingPlatform : PuzzleExchangePlatform {
        var copyCalls = 0
        var shareCalls = 0

        override fun copyText(text: String): PuzzleExchangeResult {
            copyCalls++
            return PuzzleExchangeResult.Success
        }

        override fun pasteText(): PuzzleExchangeTextResult = PuzzleExchangeTextResult.Empty

        override fun shareText(text: String): PuzzleExchangeResult {
            shareCalls++
            return PuzzleExchangeResult.Success
        }

        override fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult =
            PuzzleExchangeResult.Unsupported

        override fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult =
            PuzzleExchangeResult.Unsupported
    }
}
