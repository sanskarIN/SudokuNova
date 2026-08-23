package com.sanskar.sudokunova.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.HintTechnique
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.shared.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun SudokuNovaSharedApp(
    state: SharedGameState = remember { SharedGameState() },
) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Header(state)
                DifficultyPicker(state)
                SudokuGrid(state)
                NumberPad(state)
                GameActions(state)
                Text(
                    text = statusLabel(state.status),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.footer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun Header(state: SharedGameState) {
    Row(
        modifier = Modifier.fillMaxWidth().sizeIn(maxWidth = 720.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Button(onClick = { state.newGame() }) {
            Text(stringResource(Res.string.new_game))
        }
    }
}

@Composable
private fun DifficultyPicker(state: SharedGameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(maxWidth = 720.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Difficulty.entries.forEach { difficulty ->
            val selected = difficulty == state.difficulty
            val label = difficultyLabel(difficulty)
            if (selected) {
                Button(onClick = { state.setDifficulty(difficulty) }) {
                    Text(label)
                }
            } else {
                OutlinedButton(onClick = { state.setDifficulty(difficulty) }) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun difficultyLabel(difficulty: Difficulty): String = stringResource(
    when (difficulty) {
        Difficulty.BEGINNER -> Res.string.difficulty_beginner
        Difficulty.EASY -> Res.string.difficulty_easy
        Difficulty.MEDIUM -> Res.string.difficulty_medium
        Difficulty.HARD -> Res.string.difficulty_hard
        Difficulty.EXPERT -> Res.string.difficulty_expert
        Difficulty.MASTER -> Res.string.difficulty_master
        Difficulty.EXTREME -> Res.string.difficulty_extreme
    },
)

@Composable
private fun statusLabel(status: SharedGameStatus): String = when (status) {
    SharedGameStatus.SelectCell -> stringResource(Res.string.status_select_cell)
    SharedGameStatus.FixedCellSelected -> stringResource(Res.string.status_fixed_cell_selected)
    is SharedGameStatus.CellSelected -> stringResource(
        Res.string.status_cell_selected,
        status.row,
        status.column,
    )
    is SharedGameStatus.NewPuzzle -> stringResource(
        Res.string.status_new_puzzle,
        difficultyLabel(status.difficulty),
    )
    SharedGameStatus.NotesEnabled -> stringResource(Res.string.status_notes_enabled)
    SharedGameStatus.NotesDisabled -> stringResource(Res.string.status_notes_disabled)
    SharedGameStatus.SelectEditableCell -> stringResource(Res.string.status_select_editable_cell)
    SharedGameStatus.FixedClue -> stringResource(Res.string.status_fixed_clue)
    SharedGameStatus.NotesUpdated -> stringResource(Res.string.status_notes_updated)
    SharedGameStatus.Solved -> stringResource(Res.string.status_solved)
    SharedGameStatus.Conflict -> stringResource(Res.string.status_conflict)
    SharedGameStatus.Incorrect -> stringResource(Res.string.status_incorrect)
    SharedGameStatus.GoodMove -> stringResource(Res.string.status_good_move)
    SharedGameStatus.CellCleared -> stringResource(Res.string.status_cell_cleared)
    SharedGameStatus.NothingToUndo -> stringResource(Res.string.status_nothing_to_undo)
    SharedGameStatus.MoveUndone -> stringResource(Res.string.status_move_undone)
    SharedGameStatus.AlreadySolved -> stringResource(Res.string.status_already_solved)
    SharedGameStatus.NoSafeHint -> stringResource(Res.string.status_no_safe_hint)
    is SharedGameStatus.Hint -> stringResource(
        Res.string.status_hint,
        hintTechniqueLabel(status.technique),
    )
    SharedGameStatus.Reset -> stringResource(Res.string.status_reset)
}

@Composable
private fun hintTechniqueLabel(technique: HintTechnique): String = stringResource(
    when (technique) {
        HintTechnique.NAKED_SINGLE -> Res.string.technique_naked_single
        HintTechnique.HIDDEN_SINGLE -> Res.string.technique_hidden_single
        HintTechnique.NAKED_PAIR -> Res.string.technique_naked_pair
        HintTechnique.POINTING_PAIR_OR_TRIPLE -> Res.string.technique_pointing_pair_or_triple
        HintTechnique.BOX_LINE_REDUCTION -> Res.string.technique_box_line_reduction
        HintTechnique.HIDDEN_PAIR -> Res.string.technique_hidden_pair
        HintTechnique.NAKED_TRIPLE -> Res.string.technique_naked_triple
        HintTechnique.HIDDEN_TRIPLE -> Res.string.technique_hidden_triple
        HintTechnique.X_WING -> Res.string.technique_x_wing
        HintTechnique.REVEAL -> Res.string.technique_reveal
    },
)

@Composable
private fun SudokuGrid(state: SharedGameState) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().sizeIn(maxWidth = 720.dp),
        contentAlignment = Alignment.Center,
    ) {
        val gridSize = if (maxWidth < 560.dp) maxWidth else 560.dp
        Column(
            modifier = Modifier
                .width(gridSize)
                .aspectRatio(1f)
                .border(2.dp, MaterialTheme.colorScheme.outline),
        ) {
            repeat(SudokuBoard.SIZE) { row ->
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    repeat(SudokuBoard.SIZE) { column ->
                        val index = row * SudokuBoard.SIZE + column
                        SudokuCell(
                            state = state,
                            index = index,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SudokuCell(
    state: SharedGameState,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val row = index / SudokuBoard.SIZE
    val column = index % SudokuBoard.SIZE
    val value = state.board.valueAt(index)
    val selected = state.selectedIndex == index
    val fixed = state.isFixed(index)
    val conflict = value != SudokuBoard.EMPTY && state.board.hasConflict(index)
    val background = when {
        conflict -> MaterialTheme.colorScheme.errorContainer
        selected -> MaterialTheme.colorScheme.primaryContainer
        fixed -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val rightBorder = if ((column + 1) % SudokuBoard.BOX_SIZE == 0 && column != 8) 2.dp else 0.5.dp
    val bottomBorder = if ((row + 1) % SudokuBoard.BOX_SIZE == 0 && row != 8) 2.dp else 0.5.dp

    Box(
        modifier = modifier
            .background(background)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            .clickable { state.select(index) },
        contentAlignment = Alignment.Center,
    ) {
        if (value != SudokuBoard.EMPTY) {
            Text(
                text = value.toString(),
                fontSize = 20.sp,
                fontWeight = if (fixed) FontWeight.Bold else FontWeight.Medium,
                color = if (conflict) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
            )
        } else {
            val notes = state.notes[index].orEmpty().sorted()
            if (notes.isNotEmpty()) {
                Text(
                    text = notes.joinToString(" "),
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(2.dp),
                )
            }
        }

        if (rightBorder > 0.dp) {
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .width(rightBorder)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outline),
            )
        }
        if (bottomBorder > 0.dp) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .height(bottomBorder)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outline),
            )
        }
    }
}

@Composable
private fun NumberPad(state: SharedGameState) {
    Column(
        modifier = Modifier.fillMaxWidth().sizeIn(maxWidth = 560.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            (1..5).forEach { value -> NumberButton(value, state, Modifier.weight(1f)) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            (6..9).forEach { value -> NumberButton(value, state, Modifier.weight(1f)) }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumberButton(value: Int, state: SharedGameState, modifier: Modifier = Modifier) {
    Button(
        onClick = { state.enter(value) },
        modifier = modifier,
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Text(value.toString())
    }
}

@Composable
private fun GameActions(state: SharedGameState) {
    Column(
        modifier = Modifier.fillMaxWidth().sizeIn(maxWidth = 720.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val notesLabel = stringResource(Res.string.action_notes) + if (state.notesMode) " ✓" else ""
            ActionButton(
                label = notesLabel,
                onClick = state::toggleNotesMode,
                modifier = Modifier.weight(1f),
            )
            ActionButton(stringResource(Res.string.action_erase), state::erase, Modifier.weight(1f))
            ActionButton(stringResource(Res.string.action_undo), state::undo, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionButton(stringResource(Res.string.action_hint), state::hint, Modifier.weight(1f))
            ActionButton(stringResource(Res.string.action_reset), state::reset, Modifier.weight(1f))
            ActionButton(stringResource(Res.string.new_game), { state.newGame() }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        Text(label, maxLines = 1)
    }
}
