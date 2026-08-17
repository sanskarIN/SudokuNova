package com.sanskar.sudokunova.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.engine.SudokuBoard as EngineBoard
import com.sanskar.sudokunova.game.GameState

@Composable
fun SudokuBoardView(
    game: GameState,
    settings: UserSettings,
    onCellSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedValue = game.board.valueAt(game.selectedIndex)
    val selectedRow = game.selectedIndex / 9
    val selectedColumn = game.selectedIndex % 9
    val selectedBoxRow = selectedRow / 3
    val selectedBoxColumn = selectedColumn / 3

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        val boardSize = maxWidth.coerceAtMost(560.dp)
        Box(modifier = Modifier.size(boardSize)) {
            if (game.isPaused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Paused",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    repeat(9) { row ->
                        Row(modifier = Modifier.weight(1f)) {
                            repeat(9) { column ->
                                val index = row * 9 + column
                                val value = game.board.valueAt(index)
                                val isSelected = index == game.selectedIndex
                                val isPeer = settings.highlightPeers && (
                                    row == selectedRow ||
                                        column == selectedColumn ||
                                        (row / 3 == selectedBoxRow && column / 3 == selectedBoxColumn)
                                    )
                                val isSameValue = settings.highlightSameNumbers &&
                                    selectedValue != EngineBoard.EMPTY && value == selectedValue
                                val isWrong = value != EngineBoard.EMPTY &&
                                    !game.isOriginal(index) &&
                                    settings.autoCheckMistakes &&
                                    value != game.solution.valueAt(index)
                                val isConflict = game.board.hasConflict(index) || isWrong

                                SudokuCell(
                                    value = value,
                                    notes = game.notes[index],
                                    original = game.isOriginal(index),
                                    selected = isSelected,
                                    peer = isPeer,
                                    sameValue = isSameValue,
                                    conflict = isConflict,
                                    row = row,
                                    column = column,
                                    onClick = { onCellSelected(index) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellWidth = size.width / 9f
                    val cellHeight = size.height / 9f
                    for (i in 0..9) {
                        val major = i % 3 == 0
                        val strokeWidth = if (major) 3.dp.toPx() else 1.dp.toPx()
                        drawLine(
                            color = gridColor,
                            start = androidx.compose.ui.geometry.Offset(i * cellWidth, 0f),
                            end = androidx.compose.ui.geometry.Offset(i * cellWidth, size.height),
                            strokeWidth = strokeWidth,
                        )
                        drawLine(
                            color = gridColor,
                            start = androidx.compose.ui.geometry.Offset(0f, i * cellHeight),
                            end = androidx.compose.ui.geometry.Offset(size.width, i * cellHeight),
                            strokeWidth = strokeWidth,
                        )
                    }
                    drawRect(color = gridColor, style = Stroke(width = 3.dp.toPx()))
                }
            }
        }
    }
}

@Composable
private fun SudokuCell(
    value: Int,
    notes: Set<Int>,
    original: Boolean,
    selected: Boolean,
    peer: Boolean,
    sameValue: Boolean,
    conflict: Boolean,
    row: Int,
    column: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val background = when {
        conflict -> scheme.errorContainer
        selected -> scheme.primaryContainer
        sameValue -> scheme.secondaryContainer
        peer -> scheme.surfaceVariant.copy(alpha = 0.55f)
        else -> Color.Transparent
    }
    val description = buildString {
        append("Row ${row + 1}, column ${column + 1}. ")
        if (value == 0) append("Empty") else append("Value $value")
        if (original) append(", original clue")
        if (conflict) append(", conflict")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .semantics { contentDescription = description }
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            value != EngineBoard.EMPTY -> Text(
                text = value.toString(),
                fontSize = 22.sp,
                fontWeight = if (original) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    conflict -> scheme.onErrorContainer
                    original -> scheme.onSurface
                    else -> scheme.primary
                },
            )
            notes.isNotEmpty() -> NotesGrid(notes)
        }
    }
}

@Composable
private fun NotesGrid(notes: Set<Int>) {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(3) { row ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(3) { column ->
                    val number = row * 3 + column + 1
                    Box(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (number in notes) {
                            Text(
                                text = number.toString(),
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
