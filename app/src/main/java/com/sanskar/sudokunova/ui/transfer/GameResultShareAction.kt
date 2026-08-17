package com.sanskar.sudokunova.ui.transfer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R

@Composable
fun GameResultShareAction(
    difficultyLabel: String,
    timeLabel: String,
    mistakes: Int,
    hints: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val chooserTitle = stringResource(R.string.v07_share_title)
    val resultText = stringResource(
        R.string.v07_result_text,
        difficultyLabel,
        timeLabel,
        mistakes,
        hints,
    )
    OutlinedButton(
        onClick = { sharePlainText(context, resultText, chooserTitle) },
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.v07_share_result))
    }
}
