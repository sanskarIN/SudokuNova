package com.sanskar.sudokunova.ui.custom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.sanskar.sudokunova.data.history.HistoryRepository
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import kotlinx.coroutines.launch

@Composable
fun CustomPuzzleRoute(
    onBack: () -> Unit,
    onPlay: (String) -> Unit,
    viewModel: CustomPuzzleViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val repository = remember(context) { HistoryRepository(context.applicationContext) }
    val scope = rememberCoroutineScope()
    var saveStatus by remember { mutableStateOf<String?>(null) }
    val savedText = stringResource(R.string.v05_saved_success)
    val duplicateText = stringResource(R.string.v05_saved_duplicate)

    CustomPuzzleScreen(
        state = state,
        saveStatus = saveStatus,
        onBack = onBack,
        onSelect = viewModel::select,
        onInput = viewModel::input,
        onErase = viewModel::erase,
        onClear = viewModel::clear,
        onValidate = viewModel::validate,
        onSolve = viewModel::showSolution,
        onSave = {
            if (!state.isUnique) return@CustomPuzzleScreen
            val puzzle = state.board.toPuzzleString()
            scope.launch {
                val existing = repository.findSavedPuzzle(puzzle)
                if (existing != null) {
                    saveStatus = duplicateText
                } else {
                    val id = repository.savePuzzle(
                        puzzle = puzzle,
                        solution = state.solution?.toPuzzleString(),
                        title = null,
                        difficulty = Difficulty.MEDIUM,
                        source = "custom",
                    )
                    saveStatus = if (id > 0L) savedText else duplicateText
                }
            }
        },
        onPlay = { onPlay(state.board.toPuzzleString()) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomPuzzleScreen(
    state: CustomPuzzleUiState,
    saveStatus: String?,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    onInput: (Int) -> Unit,
    onErase: () -> Unit,
    onClear: () -> Unit,
    onValidate: () -> Unit,
    onSolve: () -> Unit,
    onSave: () -> Unit,
    onPlay: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v04_custom_puzzle)) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(state.message, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            EditorBoard(
                board = state.displayedBoard,
                selectedIndex = state.selectedIndex,
                onSelect = onSelect,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                (1..9).forEach { number ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clickable { onInput(number) },
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(number.toString(), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onErase, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.v04_erase)) }
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.v04_clear)) }
                Button(onClick = onValidate, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.v04_validate)) }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onSolve, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.v04_solve)) }
                OutlinedButton(
                    onClick = onSave,
                    enabled = state.isUnique,
                    modifier = Modifier.weight(1f),
                ) { Text(stringResource(R.string.v05_save_puzzle)) }
                Button(
                    onClick = onPlay,
                    enabled = state.isUnique,
                    modifier = Modifier.weight(1f),
                ) { Text(stringResource(R.string.v04_play_puzzle)) }
            }
            if (saveStatus != null) {
                Text(
                    saveStatus,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditorBoard(
    board: SudokuBoard,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        repeat(9) { row ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(9) { column ->
                    val index = row * 9 + column
                    val value = board.valueAt(index)
                    val selected = index == selectedIndex
                    val conflict = board.hasConflict(index)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(
                                start = if (column % 3 == 0) 1.5.dp else 0.5.dp,
                                top = if (row % 3 == 0) 1.5.dp else 0.5.dp,
                                end = if (column == 8) 1.5.dp else 0.5.dp,
                                bottom = if (row == 8) 1.5.dp else 0.5.dp,
                            ),
                        onClick = { onSelect(index) },
                        color = when {
                            conflict -> MaterialTheme.colorScheme.errorContainer
                            selected -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (value != SudokuBoard.EMPTY) {
                                Text(value.toString(), style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
