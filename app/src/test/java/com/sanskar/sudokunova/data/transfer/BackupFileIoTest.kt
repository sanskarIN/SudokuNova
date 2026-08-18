package com.sanskar.sudokunova.data.transfer

import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class BackupFileIoTest {
    @Test
    fun boundedReaderReturnsUtf8WithinLimit() {
        val text = "SudokuNova backup ✓"
        val result = BackupFileIo.readBoundedUtf8(
            ByteArrayInputStream(text.toByteArray(Charsets.UTF_8)),
            128,
        )
        assertEquals(text, result)
    }

    @Test
    fun boundedReaderRejectsEmptyAndOversizedStreams() {
        assertNull(BackupFileIo.readBoundedUtf8(ByteArrayInputStream(byteArrayOf()), 16))
        assertNull(
            BackupFileIo.readBoundedUtf8(
                ByteArrayInputStream(ByteArray(17) { 1 }),
                16,
            ),
        )
    }

    @Test
    fun boundedReaderAcceptsPayloadExactlyAtLimit() {
        val text = "12345678"

        assertEquals(
            text,
            BackupFileIo.readBoundedUtf8(
                ByteArrayInputStream(text.toByteArray(Charsets.UTF_8)),
                8,
            ),
        )
    }

    @Test
    fun boundedReaderRequiresPositiveLimit() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupFileIo.readBoundedUtf8(
                ByteArrayInputStream(byteArrayOf(1)),
                0,
            )
        }
    }
}
