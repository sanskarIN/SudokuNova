package com.sanskar.sudokunova.shared.android

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanskar.sudokunova.shared.PuzzleExchangeLimits
import com.sanskar.sudokunova.shared.PuzzleExchangePlatform
import com.sanskar.sudokunova.shared.PuzzleExchangeResult
import com.sanskar.sudokunova.shared.PuzzleExchangeTextResult
import java.io.ByteArrayOutputStream

/** Android clipboard/share/document-provider adapter for validated puzzle codes. */
class AndroidPuzzleExchangePlatform(
    private val activity: Activity,
) : PuzzleExchangePlatform {
    private var pendingImport: ((String) -> Unit)? = null
    private var pendingExportText: String? = null

    override fun copyText(text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            return PuzzleExchangeResult.Failed("Puzzle code is too large.")
        }
        return runCatching {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("SudokuNova puzzle", text))
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("Clipboard is unavailable.") }
    }

    override fun pasteText(): PuzzleExchangeTextResult = runCatching {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip ?: return PuzzleExchangeTextResult.Empty
        val text = clip.getItemAt(0).coerceToText(activity).toString()
        when {
            text.isBlank() -> PuzzleExchangeTextResult.Empty
            !PuzzleExchangeLimits.isBoundedCode(text) -> PuzzleExchangeTextResult.Failed("Clipboard text is too large.")
            else -> PuzzleExchangeTextResult.Success(text)
        }
    }.getOrElse { PuzzleExchangeTextResult.Failed("Clipboard is unavailable.") }

    override fun shareText(text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedCode(text)) {
            return PuzzleExchangeResult.Failed("Puzzle code is too large.")
        }
        return runCatching {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            activity.startActivity(Intent.createChooser(intent, "Share SudokuNova puzzle"))
            PuzzleExchangeResult.Success
        }.getOrElse { PuzzleExchangeResult.Failed("No compatible share target is available.") }
    }

    override fun importTextFile(onText: (String) -> Unit): PuzzleExchangeResult {
        pendingImport = onText
        return runCatching {
            activity.startActivityForResult(
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                },
                REQUEST_IMPORT,
            )
            PuzzleExchangeResult.Success
        }.getOrElse {
            pendingImport = null
            PuzzleExchangeResult.Failed("Unable to open the document picker.")
        }
    }

    override fun exportTextFile(fileName: String, text: String): PuzzleExchangeResult {
        if (!PuzzleExchangeLimits.isBoundedFileText(text)) {
            return PuzzleExchangeResult.Failed("Puzzle file is too large.")
        }
        pendingExportText = text
        return runCatching {
            activity.startActivityForResult(
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, fileName)
                },
                REQUEST_EXPORT,
            )
            PuzzleExchangeResult.Success
        }.getOrElse {
            pendingExportText = null
            PuzzleExchangeResult.Failed("Unable to open the save dialog.")
        }
    }

    /** Forward Activity.onActivityResult here; invalid file text is discarded. */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PuzzleExchangeResult? {
        if (requestCode != REQUEST_IMPORT && requestCode != REQUEST_EXPORT) return null
        if (resultCode != Activity.RESULT_OK) {
            pendingImport = null
            pendingExportText = null
            return PuzzleExchangeResult.Failed("File operation was cancelled.")
        }
        val uri = data?.data ?: run {
            pendingImport = null
            pendingExportText = null
            return PuzzleExchangeResult.Failed("No file was selected.")
        }
        return when (requestCode) {
            REQUEST_IMPORT -> completeImport(uri)
            REQUEST_EXPORT -> completeExport(uri)
            else -> null
        }
    }

    private fun completeImport(uri: Uri): PuzzleExchangeResult = runCatching {
        val text = readBoundedText(uri)
            ?: return PuzzleExchangeResult.Failed("Unable to read the selected file.")
        pendingImport?.invoke(text)
        pendingImport = null
        PuzzleExchangeResult.Success
    }.getOrElse {
        pendingImport = null
        PuzzleExchangeResult.Failed("Unable to read the selected file.")
    }

    private fun readBoundedText(uri: Uri): String? {
        val input = activity.contentResolver.openInputStream(uri) ?: return null
        return input.use { stream ->
            val output = ByteArrayOutputStream(PuzzleExchangeLimits.MAX_FILE_TEXT_LENGTH)
            val buffer = ByteArray(128)
            var total = 0
            while (true) {
                val count = stream.read(buffer)
                if (count < 0) break
                total += count
                if (total > PuzzleExchangeLimits.MAX_FILE_TEXT_LENGTH) {
                    throw IllegalArgumentException("Puzzle file is too large.")
                }
                output.write(buffer, 0, count)
            }
            output.toByteArray().toString(Charsets.UTF_8)
        }
    }

    private fun completeExport(uri: Uri): PuzzleExchangeResult = runCatching {
        val text = pendingExportText ?: return PuzzleExchangeResult.Failed("No pending export exists.")
        activity.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(text.toByteArray(Charsets.UTF_8))
        } ?: return PuzzleExchangeResult.Failed("Unable to write the selected file.")
        pendingExportText = null
        PuzzleExchangeResult.Success
    }.getOrElse {
        pendingExportText = null
        PuzzleExchangeResult.Failed("Unable to write the selected file.")
    }

    private companion object {
        const val REQUEST_IMPORT = 24151
        const val REQUEST_EXPORT = 24152
    }
}
