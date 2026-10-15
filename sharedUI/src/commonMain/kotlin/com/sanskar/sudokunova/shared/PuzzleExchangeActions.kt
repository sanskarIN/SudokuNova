package com.sanskar.sudokunova.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sanskar.sudokunova.shared.resources.Res
import com.sanskar.sudokunova.shared.resources.puzzle_copy
import com.sanskar.sudokunova.shared.resources.puzzle_export_file
import com.sanskar.sudokunova.shared.resources.puzzle_import_file
import com.sanskar.sudokunova.shared.resources.puzzle_paste
import com.sanskar.sudokunova.shared.resources.puzzle_share
import org.jetbrains.compose.resources.stringResource

/**
 * Platform-neutral action controls for the shared puzzle exchange boundary.
 *
 * The host supplies a [PuzzleExchangeCoordinator], while platform adapters
 * remain outside the common Compose UI. Import-file results are delivered
 * through [onImportedCode] so the caller can pass them to SharedGameState's
 * validated SNP1 import path.
 */
@Composable
fun PuzzleExchangeActions(
    coordinator: PuzzleExchangeCoordinator,
    currentCode: String,
    onImportedCode: (String) -> Unit,
    onResult: (PuzzleExchangeResult) -> Unit = {},
    onPasteResult: (PuzzleExchangeTextResult) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = { onResult(coordinator.copyCurrentPuzzle(currentCode)) },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(Res.string.puzzle_copy))
            }
            OutlinedButton(
                onClick = {
                    onPasteResult(
                        coordinator.pastePuzzleCode(onImportedCode),
                    )
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(Res.string.puzzle_paste))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { onResult(coordinator.shareCurrentPuzzle(currentCode)) },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(Res.string.puzzle_share))
            }
            OutlinedButton(
                onClick = {
                    onResult(coordinator.importPuzzleFile(onImportedCode))
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(Res.string.puzzle_import_file))
            }
            OutlinedButton(
                onClick = {
                    onResult(coordinator.exportCurrentPuzzle(currentCode))
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(Res.string.puzzle_export_file))
            }
        }
        Text(
            text = currentCode,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
