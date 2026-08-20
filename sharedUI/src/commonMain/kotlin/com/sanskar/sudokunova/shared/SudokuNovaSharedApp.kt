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
import com.sanskar.sudokunova.engine.SudokuBoard

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
                    text = state.statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Made by the Sanskar • Offline Sudoku core • Shared Kotlin/Compose UI",
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
                text = "SudokuNova",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Think. Solve. Master the Grid.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Button(onClick = { state.newGame() }) {
            Text("New game")
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
            if (selected) {
                Button(onClick = { state.setDifficulty(difficulty) }) {
                    Text(difficulty.displayName)
                }
            } else {
                OutlinedButton(onClick = { state.setDifficulty(difficulty) }) {
                    Text(difficulty.displayName)
                }
            }
        }
    }
}

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
            ActionButton(
                label = if (state.notesMode) "Notes on" else "Notes",
                onClick = state::toggleNotesMode,
                modifier = Modifier.weight(1f),
            )
            ActionButton("Erase", state::erase, Modifier.weight(1f))
            ActionButton("Undo", state::undo, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionButton("Hint", state::hint, Modifier.weight(1f))
            ActionButton("Reset", state::reset, Modifier.weight(1f))
            ActionButton("New", { state.newGame() }, Modifier.weight(1f))
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
