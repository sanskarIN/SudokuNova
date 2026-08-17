package com.sanskar.sudokunova.ui.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.PuzzleCodeCodec
import com.sanskar.sudokunova.engine.SudokuBoard

@Composable
fun PuzzleShareActions(
    puzzle: String,
    difficulty: Difficulty,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val code = remember(puzzle, difficulty) {
        runCatching {
            PuzzleCodeCodec.encode(SudokuBoard.parse(puzzle), difficulty)
        }.getOrNull()
    }
    if (code == null) return

    val chooserTitle = stringResource(R.string.v07_share_title)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = { copyPlainText(context, "SudokuNova puzzle", code) },
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(R.string.v07_copy_puzzle_code))
        }
        OutlinedButton(
            onClick = { sharePlainText(context, code, chooserTitle) },
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(R.string.v07_share_puzzle_code))
        }
    }
}
