package com.sanskar.sudokunova.ui.game

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.game.GameState
import com.sanskar.sudokunova.game.GameStatus

@Composable
fun GameRoute(
    onBack: () -> Unit,
    onNewGame: () -> Unit,
    viewModel: GameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val hint by viewModel.pendingHint.collectAsStateWithLifecycle()

    GameScreen(
        uiState = uiState,
        settings = settings,
        onBack = onBack,
        onNewGame = onNewGame,
        onCellSelected = viewModel::selectCell,
        onNumber = viewModel::enterNumber,
        onErase = viewModel::erase,
        onToggleNotes = viewModel::toggleNotesMode,
        onUndo = viewModel::undo,
        onRedo = viewModel::redo,
        onHint = viewModel::requestHint,
        onPause = viewModel::togglePause,
        onRestart = viewModel::restart,
    )

    if (hint != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissHint,
            title = { Text(hint!!.technique.displayName) },
            text = { Text(hint!!.explanation) },
            confirmButton = {
                TextButton(onClick = viewModel::applyHint) { Text("Apply hint") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissHint) { Text("Not now") }
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
                title = { Text("SudokuNova") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    Text("Generating a unique puzzle…")
                }
            }

            is GameScreenState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.message, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onNewGame) { Text("New game") }
                }
            }

            is GameScreenState.Ready -> {
                val game = uiState.game
                GameContent(
                    game = game,
                    settings = settings,
                    modifier = Modifier.padding(padding),
                    onCellSelected = onCellSelected,
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
                        title = { Text("Puzzle complete!") },
                        text = {
                            Text(
                                "${game.difficulty.displayName} solved in ${formatTime(game.elapsedSeconds)} " +
                                    "with ${game.mistakes} mistake(s) and ${game.hintsUsed} hint(s).",
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = onNewGame) { Text("Play another") }
                        },
                        dismissButton = {
                            TextButton(onClick = onBack) { Text("Home") }
                        },
                    )
                } else if (game.status == GameStatus.FAILED) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("Mistake limit reached") },
                        text = { Text("You can restart this puzzle and try again.") },
                        confirmButton = {
                            TextButton(onClick = onRestart) { Text("Restart") }
                        },
                        dismissButton = {
                            TextButton(onClick = onBack) { Text("Home") }
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
    onNumber: (Int) -> Unit,
    onErase: () -> Unit,
    onToggleNotes: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
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
            Text(game.difficulty.displayName, style = MaterialTheme.typography.titleLarge)
            Text("${game.progressPercent}% complete", style = MaterialTheme.typography.bodyLarge)
        }
        Column(horizontalAlignment = Alignment.End) {
            if (settings.showTimer) Text(formatTime(game.elapsedSeconds))
            Text("Mistakes: ${game.mistakes}${if (settings.mistakeLimit > 0) "/${settings.mistakeLimit}" else ""}")
        }
    }
}

@Composable
private fun ControlsPanel(
    game: GameState,
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
        NumberPad(onNumber)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActionButton("Undo", Icons.Default.Undo, onUndo)
            ActionButton("Redo", Icons.Default.Redo, onRedo)
            ActionButton("Erase", Icons.Default.Backspace, onErase)
            ActionButton(
                if (game.notesMode) "Notes on" else "Notes",
                Icons.Default.Edit,
                onToggleNotes,
                selected = game.notesMode,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActionButton("Hint", Icons.Default.Lightbulb, onHint)
            ActionButton(
                if (game.isPaused) "Resume" else "Pause",
                if (game.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                onPause,
            )
            ActionButton("Restart", Icons.Default.Refresh, onRestart)
        }
    }
}

@Composable
private fun NumberPad(onNumber: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        (1..9).forEach { number ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clickable(role = Role.Button) { onNumber(number) },
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
