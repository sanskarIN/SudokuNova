package com.sanskar.sudokunova.ui.game

import android.view.SoundEffectConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.InputMode
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.game.GameState
import com.sanskar.sudokunova.game.GameStatus
import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel

@Composable
fun GameRoute(
    onBack: () -> Unit,
    onNewGame: () -> Unit,
    viewModel: GameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val hint by viewModel.pendingHint.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current
    val view = LocalView.current

    fun playFeedback() {
        if (settings.haptics) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        if (settings.sounds) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    GameScreen(
        uiState = uiState,
        settings = settings,
        onBack = onBack,
        onNewGame = onNewGame,
        onCellSelected = { index ->
            val selectedNumber = (uiState as? GameScreenState.Ready)?.game?.selectedNumber
            viewModel.selectCell(index)
            if (settings.inputMode == InputMode.NUMBER_FIRST && selectedNumber != null) {
                viewModel.enterNumber(selectedNumber)
            }
            playFeedback()
        },
        onMoveSelection = viewModel::selectCell,
        onNumber = { number ->
            viewModel.selectNumber(number)
            if (settings.inputMode == InputMode.CELL_FIRST) {
                viewModel.enterNumber(number)
            }
            playFeedback()
        },
        onErase = {
            viewModel.erase()
            playFeedback()
        },
        onToggleNotes = {
            viewModel.toggleNotesMode()
            playFeedback()
        },
        onUndo = {
            viewModel.undo()
            playFeedback()
        },
        onRedo = {
            viewModel.redo()
            playFeedback()
        },
        onHint = {
            viewModel.requestHint()
            playFeedback()
        },
        onPause = {
            viewModel.togglePause()
            playFeedback()
        },
        onRestart = {
            viewModel.restart()
            playFeedback()
        },
    )

    if (hint != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissHint,
            title = { Text(hint!!.technique.displayName) },
            text = { Text(hint!!.explanation) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.applyHint()
                        playFeedback()
                    },
                ) { Text(stringResource(R.string.v04_apply_hint)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissHint) { Text(stringResource(R.string.v04_not_now)) }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreen(
    uiState: GameScreenState,
    settings: UserSettings,
    onBack: () -> Unit,
    onNewGame: () -> Unit,
    onCellSelected: (Int) -> Unit,
    onMoveSelection: (Int) -> Unit,
    onNumber: (Int) -> Unit,
    onErase: () -> Unit,
    onToggleNotes: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v04_app_name)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.v04_back))
                    }
                },
            )
        },
    ) { padding ->
        when (uiState) {
            GameScreenState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.v04_generating_unique_puzzle))
                }
            }

            is GameScreenState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.message, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onNewGame) { Text(stringResource(R.string.v04_new_game)) }
                }
            }

            is GameScreenState.Ready -> {
                val game = uiState.game
                GameContent(
                    game = game,
                    settings = settings,
                    modifier = Modifier.padding(padding),
                    onCellSelected = onCellSelected,
                    onMoveSelection = onMoveSelection,
                    onNumber = onNumber,
                    onErase = onErase,
                    onToggleNotes = onToggleNotes,
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onHint = onHint,
                    onPause = onPause,
                    onRestart = onRestart,
                )

                if (game.status == GameStatus.COMPLETED) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text(stringResource(R.string.v04_puzzle_complete)) },
                        text = {
                            Text(
                                "${localizedDifficultyLabel(game.difficulty)} · ${formatTime(game.elapsedSeconds)} · " +
                                    "${game.mistakes} mistake(s) · ${game.hintsUsed} hint(s)",
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = onNewGame) { Text(stringResource(R.string.v04_play_another)) }
                        },
                        dismissButton = {
                            TextButton(onClick = onBack) { Text(stringResource(R.string.v04_home)) }
                        },
                    )
                } else if (game.status == GameStatus.FAILED) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text(stringResource(R.string.v04_mistake_limit_reached)) },
                        text = { Text(stringResource(R.string.v04_restart_try_again)) },
                        confirmButton = {
                            TextButton(onClick = onRestart) { Text(stringResource(R.string.v04_restart)) }
                        },
                        dismissButton = {
                            TextButton(onClick = onBack) { Text(stringResource(R.string.v04_home)) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GameContent(
    game: GameState,
    settings: UserSettings,
    modifier: Modifier = Modifier,
    onCellSelected: (Int) -> Unit,
    onMoveSelection: (Int) -> Unit,
    onNumber: (Int) -> Unit,
    onErase: () -> Unit,
    onToggleNotes: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || game.status != GameStatus.PLAYING || game.isPaused) {
                    return@onPreviewKeyEvent false
                }
                when (val action = resolveGameKeyboardAction(event.key, event.utf16CodePoint)) {
                    is GameKeyboardAction.MoveSelection -> {
                        onMoveSelection(
                            moveSudokuSelection(
                                currentIndex = game.selectedIndex,
                                rowDelta = action.rowDelta,
                                columnDelta = action.columnDelta,
                            ),
                        )
                        true
                    }
                    is GameKeyboardAction.EnterNumber -> {
                        onNumber(action.value)
                        true
                    }
                    GameKeyboardAction.Erase -> {
                        onErase()
                        true
                    }
                    GameKeyboardAction.ToggleNotes -> {
                        onToggleNotes()
                        true
                    }
                    GameKeyboardAction.Hint -> {
                        onHint()
                        true
                    }
                    null -> false
                }
            }
            .focusable(),
    ) {
        val wide = maxWidth >= 720.dp
        if (wide) {
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    GameMeta(game, settings)
                    Spacer(Modifier.height(12.dp))
                    SudokuBoardView(
                        game = game,
                        settings = settings,
                        onCellSelected = onCellSelected,
                    )
                }
                ControlsPanel(
                    game = game,
                    settings = settings,
                    modifier = Modifier.weight(0.8f).verticalScroll(rememberScrollState()),
                    onNumber = onNumber,
                    onErase = onErase,
                    onToggleNotes = onToggleNotes,
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onHint = onHint,
                    onPause = onPause,
                    onRestart = onRestart,
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
            ) {
                GameMeta(game, settings)
                Spacer(Modifier.height(10.dp))
                SudokuBoardView(
                    game = game,
                    settings = settings,
                    onCellSelected = onCellSelected,
                )
                Spacer(Modifier.height(12.dp))
                ControlsPanel(
                    game = game,
                    settings = settings,
                    onNumber = onNumber,
                    onErase = onErase,
                    onToggleNotes = onToggleNotes,
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onHint = onHint,
                    onPause = onPause,
                    onRestart = onRestart,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GameMeta(game: GameState, settings: UserSettings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(localizedDifficultyLabel(game.difficulty), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.v04_percent_complete, game.progressPercent), style = MaterialTheme.typography.bodyLarge)
            Text(
                if (settings.inputMode == InputMode.CELL_FIRST) {
                    stringResource(R.string.v06_cell_first)
                } else {
                    stringResource(R.string.v06_number_first)
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            if (settings.showTimer) Text(formatTime(game.elapsedSeconds))
            Text(
                if (settings.mistakeLimit > 0) {
                    stringResource(R.string.v04_mistakes_limited, game.mistakes, settings.mistakeLimit)
                } else {
                    stringResource(R.string.v04_mistakes_unlimited, game.mistakes)
                },
            )
        }
    }
}

@Composable
private fun ControlsPanel(
    game: GameState,
    settings: UserSettings,
    modifier: Modifier = Modifier,
    onNumber: (Int) -> Unit,
    onErase: () -> Unit,
    onToggleNotes: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NumberPad(
            selectedNumber = game.selectedNumber,
            persistentSelection = settings.inputMode == InputMode.NUMBER_FIRST,
            onNumber = onNumber,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActionButton(stringResource(R.string.v04_undo), Icons.AutoMirrored.Filled.Undo, onUndo)
            ActionButton(stringResource(R.string.v04_redo), Icons.AutoMirrored.Filled.Redo, onRedo)
            ActionButton(stringResource(R.string.v04_erase), Icons.AutoMirrored.Filled.Backspace, onErase)
            ActionButton(
                if (game.notesMode) stringResource(R.string.v04_notes_on) else stringResource(R.string.v04_notes),
                Icons.Default.Edit,
                onToggleNotes,
                selected = game.notesMode,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActionButton(stringResource(R.string.v04_hint), Icons.Default.Lightbulb, onHint)
            ActionButton(
                if (game.isPaused) stringResource(R.string.v04_resume) else stringResource(R.string.v04_pause),
                if (game.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                onPause,
            )
            ActionButton(stringResource(R.string.v04_restart), Icons.Default.Refresh, onRestart)
        }

        Text(
            stringResource(R.string.v06_keyboard_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NumberPad(
    selectedNumber: Int?,
    persistentSelection: Boolean,
    onNumber: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        (1..9).forEach { number ->
            val selected = persistentSelection && selectedNumber == number
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clickable(role = Role.Button) { onNumber(number) },
                shape = MaterialTheme.shapes.medium,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                contentColor = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(number.toString(), style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    selected: Boolean = false,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remaining = seconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, remaining)
    else "%02d:%02d".format(minutes, remaining)
}
