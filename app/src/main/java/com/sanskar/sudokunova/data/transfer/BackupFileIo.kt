package com.sanskar.sudokunova.data.transfer

import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

object BackupFileIo {
    const val MIME_TYPE = "text/plain"
    const val DEFAULT_FILE_NAME = "SudokuNova-backup.snb"

    fun write(context: Context, uri: Uri, backupText: String): Boolean = runCatching {
        val bytes = backupText.toByteArray(StandardCharsets.UTF_8)
        require(bytes.isNotEmpty() && bytes.size <= BackupCodec.MAX_BACKUP_BYTES)
        context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
            output.write(bytes)
            output.flush()
        } ?: error("Unable to open backup output")
        true
    }.getOrDefault(false)

    fun read(context: Context, uri: Uri): String? = runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            readBoundedUtf8(input, BackupCodec.MAX_BACKUP_BYTES)
        } ?: error("Unable to open backup input")
    }.getOrNull()

    fun readBoundedUtf8(input: InputStream, maxBytes: Int): String? {
        require(maxBytes > 0)
        val output = ByteArrayOutputStream(minOf(maxBytes, 16 * 1024))
        val buffer = ByteArray(8 * 1024)
        var total = 0
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            if (read == 0) continue
            total += read
            if (total > maxBytes) return null
            output.write(buffer, 0, read)
        }
        if (total == 0) return null
        return output.toString(StandardCharsets.UTF_8.name())
    }
}
