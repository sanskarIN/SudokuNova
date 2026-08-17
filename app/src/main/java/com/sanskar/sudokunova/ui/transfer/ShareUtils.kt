package com.sanskar.sudokunova.ui.transfer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

fun copyPlainText(
    context: Context,
    label: String,
    text: String,
) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}

fun sharePlainText(
    context: Context,
    text: String,
    chooserTitle: String,
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(sendIntent, chooserTitle))
}
