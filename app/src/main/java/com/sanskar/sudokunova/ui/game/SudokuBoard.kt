package com.sanskar.sudokunova.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.engine.SudokuHint
import com.sanskar.sudokunova.engine.SudokuBoard as EngineBoard
import com.sanskar.sudokunova.game.GameState

@Composable
fun SudokuBoardView(
    game: GameState,
    settings: UserSettings,
    onCellSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    hint: SudokuHint? = null,
) {
    val selectedValue = game.board.valueAt(game.selectedIndex)
    val selectedRow = game.selectedIndex / 9
    val selectedColumn = game.selectedIndex % 9
    val selectedBoxRow = selectedRow / 3
    val selectedBoxColumn = selectedColumn / 3
    val teachingSources = hint?.teachingSteps.orEmpty().flatMap { it.sourceCells }.toSet()
    val teachingTargets = hint?.teachingSteps.orEmpty().flatMap { it.targetCells }.toSet()
    val hintPlacement = hint?.placement
    val eliminationsByCell = hint?.teachingSteps
        .orEmpty()
        .flatMap { it.candidateEliminations }
        .groupBy { it.cellIndex }
        .mapValues { (_, values) -> values.map { it.candidate }.toSortedSet() }

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
                        text = stringResource(R.string.v04_paused),
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
                                    highContrast = settings.highContrast,
                                    row = row,
                                    column = column,
                                    teachingSource = index in teachingSources,
                                    teachingTarget = index in teachingTargets,
                                    hintPlacementValue = hintPlacement?.takeIf { it.cellIndex == index }?.value,
                                    eliminationCandidates = eliminationsByCell[index].orEmpty(),
                                    onClick = { onCellSelected(index) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                val gridColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (settings.highContrast) 1f else 0.8f,
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellWidth = size.width / 9f
                    val cellHeight = size.height / 9f
                    for (i in 0..9) {
                        val major = i % 3 == 0
                        val strokeWidth = when {
                            settings.highContrast && major -> 4.dp.toPx()
                            settings.highContrast -> 1.5.dp.toPx()
                            major -> 3.dp.toPx()
                            else -> 1.dp.toPx()
                        }
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
                    drawRect(
                        color = gridColor,
                        style = Stroke(width = if (settings.highContrast) 4.dp.toPx() else 3.dp.toPx()),
                    )
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
    highContrast: Boolean,
    row: Int,
    column: Int,
    teachingSource: Boolean,
    teachingTarget: Boolean,
    hintPlacementValue: Int?,
    eliminationCandidates: Set<Int>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val background = when {
        conflict -> scheme.errorContainer
        hintPlacementValue != null -> scheme.tertiaryContainer
        teachingTarget -> scheme.secondaryContainer
        teachingSource -> scheme.primaryContainer
        selected -> scheme.primaryContainer
        sameValue -> if (highContrast) scheme.secondaryContainer else scheme.secondaryContainer.copy(alpha = 0.88f)
        peer -> scheme.surfaceVariant.copy(alpha = if (highContrast) 0.82f else 0.55f)
        else -> Color.Transparent
    }
    val baseDescription = if (value == EngineBoard.EMPTY) {
        stringResource(R.string.v04_cell_empty, row + 1, column + 1)
    } else {
        stringResource(R.string.v04_cell_value, row + 1, column + 1, value)
    }
    val originalSuffix = if (original) stringResource(R.string.v04_original_clue_suffix) else ""
    val conflictSuffix = if (conflict) stringResource(R.string.v04_conflict_suffix) else ""
    val cellLabel = stringResource(R.string.v08_cell_label, row + 1, column + 1)
    val teachingSourceSuffix = if (teachingSource) {
        " " + stringResource(R.string.v08_hint_source_semantics, cellLabel)
    } else {
        ""
    }
    val teachingTargetSuffix = if (teachingTarget) {
        " " + stringResource(R.string.v08_hint_target_semantics, cellLabel)
    } else {
        ""
    }
    val placementSuffix = hintPlacementValue?.let { hintValue ->
        " " + stringResource(R.string.v08_hint_placement_semantics, cellLabel, hintValue)
    }.orEmpty()
    val eliminationSuffix = if (eliminationCandidates.isNotEmpty()) {
        " " + stringResource(
            R.string.v08_hint_elimination_semantics,
            cellLabel,
            eliminationCandidates.joinToString(separator = ", "),
        )
    } else {
        ""
    }
    val description = baseDescription + originalSuffix + conflictSuffix +
        teachingSourceSuffix + teachingTargetSuffix + placementSuffix + eliminationSuffix
    val borderColor = when {
        conflict -> scheme.error
        hintPlacementValue != null -> scheme.tertiary
        teachingTarget -> scheme.secondary
        teachingSource || selected -> scheme.primary
        else -> Color.Transparent
    }
    val emphasized = conflict || selected || teachingSource || teachingTarget || hintPlacementValue != null
    val borderWidth = if (emphasized) {
        if (highContrast) 3.dp else 2.dp
    } else {
        0.dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .border(borderWidth, borderColor)
            .semantics {
                contentDescription = description
                this.selected = selected
            }
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            value != EngineBoard.EMPTY -> Text(
                text = value.toString(),
                fontSize = 22.sp,
                fontWeight = when {
                    original -> FontWeight.Bold
                    highContrast -> FontWeight.SemiBold
                    else -> FontWeight.Medium
                },
                color = when {
                    conflict -> scheme.onErrorContainer
                    original -> scheme.onSurface
                    else -> scheme.primary
                },
            )
            notes.isNotEmpty() -> NotesGrid(notes, highContrast)
        }
    }
}

@Composable
private fun NotesGrid(notes: Set<Int>, highContrast: Boolean) {
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
                                fontSize = if (highContrast) 10.sp else 9.sp,
                                fontWeight = if (highContrast) FontWeight.Medium else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
