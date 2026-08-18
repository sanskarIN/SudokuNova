package com.sanskar.sudokunova.ui.transfer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.transfer.BackupFileIo
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TransferRoute(
    onBack: () -> Unit,
    onPlayImported: (String, Difficulty) -> Unit,
    viewModel: TransferViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TransferScreen(
        state = state,
        onBack = onBack,
        onPuzzleCodeChange = viewModel::setPuzzleCode,
        onValidatePuzzleCode = viewModel::validatePuzzleCode,
        onPlayImported = onPlayImported,
        onCreateBackup = viewModel::createBackup,
        onRestoreBackup = viewModel::restoreBackup,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferScreen(
    state: TransferUiState,
    onBack: () -> Unit,
    onPuzzleCodeChange: (String) -> Unit,
    onValidatePuzzleCode: () -> Unit,
    onPlayImported: (String, Difficulty) -> Unit,
    onCreateBackup: ((String) -> Unit) -> Unit,
    onRestoreBackup: (String) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboard = remember(context) {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    var pendingRestore by remember { mutableStateOf<String?>(null) }
    var transientMessage by remember { mutableStateOf<String?>(null) }
    val copiedMessage = stringResource(R.string.v07_backup_copied)
    val shareTitle = stringResource(R.string.v07_share_title)
    val emptyClipboardMessage = stringResource(R.string.v07_clipboard_empty)
    val exportSuccess = stringResource(R.string.v07_export_file_success)
    val exportFailed = stringResource(R.string.v07_export_file_failed)
    val importFailed = stringResource(R.string.v07_import_file_failed)
    var pendingExportText by remember { mutableStateOf<String?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(BackupFileIo.MIME_TYPE),
    ) { uri ->
        val textToWrite = pendingExportText
        pendingExportText = null
        if (uri != null && textToWrite != null) {
            coroutineScope.launch {
                val written = withContext(Dispatchers.IO) {
                    BackupFileIo.write(context, uri, textToWrite)
                }
                transientMessage = if (written) exportSuccess else exportFailed
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val imported = withContext(Dispatchers.IO) {
                    BackupFileIo.read(context, uri)
                }
                if (imported == null) transientMessage = importFailed else pendingRestore = imported
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v07_backup_transfer)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.v04_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                stringResource(R.string.v07_backup_transfer_desc),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.v07_puzzle_codes), style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = state.puzzleCodeInput,
                        onValueChange = onPuzzleCodeChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.v07_puzzle_code_hint)) },
                        minLines = 3,
                        maxLines = 5,
                    )
                    Button(
                        onClick = onValidatePuzzleCode,
                        enabled = !state.busy && state.puzzleCodeInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.v07_validate_code))
                    }
                    state.validatedPuzzle?.let { shared ->
                        Text(
                            stringResource(R.string.v07_code_valid, localizedDifficultyLabel(shared.difficulty)),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Button(
                            onClick = { onPlayImported(shared.puzzle.toPuzzleString(), shared.difficulty) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.v07_play_imported))
                        }
                    }
                    if (state.status == TransferStatus.PUZZLE_INVALID) {
                        Text(stringResource(R.string.v07_code_invalid), color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.v07_backup), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.v07_backup_info),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedButton(
                        onClick = {
                            onCreateBackup { text ->
                                clipboard.setPrimaryClip(ClipData.newPlainText("SudokuNova backup", text))
                                transientMessage = copiedMessage
                            }
                        },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.v07_copy_backup)) }
                    OutlinedButton(
                        onClick = {
                            onCreateBackup { text ->
                                shareText(context, text, shareTitle)
                            }
                        },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.v07_share_backup)) }
                    Button(
                        onClick = {
                            val text = clipboard.primaryClip
                                ?.takeIf { it.itemCount > 0 }
                                ?.getItemAt(0)
                                ?.coerceToText(context)
                                ?.toString()
                                ?.takeIf(String::isNotBlank)
                            if (text == null) transientMessage = emptyClipboardMessage
                            else pendingRestore = text
                        },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.v07_restore_clipboard))
                    }
                    OutlinedButton(
                        onClick = {
                            onCreateBackup { text ->
                                pendingExportText = text
                                exportLauncher.launch(BackupFileIo.DEFAULT_FILE_NAME)
                            }
                        },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.v07_export_backup_file)) }
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf(BackupFileIo.MIME_TYPE, "application/octet-stream")) },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.v07_import_backup_file)) }
                    Text(
                        stringResource(R.string.v07_file_backup_info),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        stringResource(R.string.v07_safe_format),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (state.busy) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            if (state.status == TransferStatus.BACKUP_FAILED) {
                Text(stringResource(R.string.v07_backup_failed), color = MaterialTheme.colorScheme.error)
            }
            if (state.status == TransferStatus.RESTORE_INVALID) {
                Text(stringResource(R.string.v07_restore_invalid), color = MaterialTheme.colorScheme.error)
            }
            state.importResult?.let { result ->
                Text(
                    stringResource(
                        R.string.v07_restore_success,
                        result.historyImported,
                        result.savedPuzzlesImported,
                        result.challengesImported,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    stringResource(
                        R.string.v07_restore_skipped,
                        result.historySkipped,
                        result.savedPuzzlesSkipped,
                        result.challengesSkipped,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            transientMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }

    pendingRestore?.let { raw ->
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            title = { Text(stringResource(R.string.v07_restore_question)) },
            text = { Text(stringResource(R.string.v07_restore_warning)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingRestore = null
                        onRestoreBackup(raw)
                    },
                ) { Text(stringResource(R.string.v07_restore)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingRestore = null }) {
                    Text(stringResource(R.string.v04_cancel))
                }
            },
        )
    }
}

private fun shareText(context: Context, text: String, chooserTitle: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(sendIntent, chooserTitle))
}
