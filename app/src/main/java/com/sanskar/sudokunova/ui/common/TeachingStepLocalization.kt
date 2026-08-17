package com.sanskar.sudokunova.ui.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.engine.LogicalUnit
import com.sanskar.sudokunova.engine.LogicalUnitType
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.engine.TeachingStep

@Composable
fun localizedTeachingTechnique(technique: LogicalTechnique): String {
    val context = LocalContext.current
    return context.getString(technique.titleResource())
}

@Composable
fun localizedTeachingExplanation(step: TeachingStep): String =
    teachingExplanation(LocalContext.current, step)

internal fun teachingExplanation(
    context: Context,
    step: TeachingStep,
): String {
    val affectedCells = step.eliminations.map { it.cellIndex }.toSet()
    return when (step.technique) {
        LogicalTechnique.NAKED_SINGLE -> {
            val placement = requireNotNull(step.placement)
            context.getString(
                R.string.v08_explain_naked_single,
                context.cellName(placement.cellIndex),
                placement.value,
            )
        }
        LogicalTechnique.HIDDEN_SINGLE -> {
            val placement = requireNotNull(step.placement)
            context.getString(
                R.string.v08_explain_hidden_single,
                placement.value,
                context.unitName(requireNotNull(step.sourceUnit)),
                context.cellName(placement.cellIndex),
            )
        }
        LogicalTechnique.NAKED_PAIR -> context.getString(
            R.string.v08_explain_naked_pair,
            context.unitName(requireNotNull(step.sourceUnit)),
            context.cellNames(step.sourceCells),
            step.candidateValues.sorted().joinToString(", "),
            context.cellNames(affectedCells),
        )
        LogicalTechnique.POINTING_PAIR_OR_TRIPLE -> context.getString(
            R.string.v08_explain_pointing,
            context.unitName(requireNotNull(step.sourceUnit)),
            step.candidateValues.single(),
            context.unitName(requireNotNull(step.affectedUnit)),
            context.cellNames(affectedCells),
        )
        LogicalTechnique.BOX_LINE_REDUCTION -> context.getString(
            R.string.v08_explain_box_line,
            context.unitName(requireNotNull(step.sourceUnit)),
            step.candidateValues.single(),
            context.unitName(requireNotNull(step.affectedUnit)),
            context.cellNames(affectedCells),
        )
    }
}

private fun LogicalTechnique.titleResource(): Int = when (this) {
    LogicalTechnique.NAKED_SINGLE -> R.string.v08_technique_naked_single
    LogicalTechnique.HIDDEN_SINGLE -> R.string.v08_technique_hidden_single
    LogicalTechnique.NAKED_PAIR -> R.string.v08_technique_naked_pair
    LogicalTechnique.POINTING_PAIR_OR_TRIPLE -> R.string.v08_technique_pointing_pair_triple
    LogicalTechnique.BOX_LINE_REDUCTION -> R.string.v08_technique_box_line_reduction
}

private fun Context.unitName(unit: LogicalUnit): String = when (unit.type) {
    LogicalUnitType.ROW -> getString(R.string.v08_unit_row, unit.index + 1)
    LogicalUnitType.COLUMN -> getString(R.string.v08_unit_column, unit.index + 1)
    LogicalUnitType.BOX -> getString(R.string.v08_unit_box, unit.index + 1)
}

private fun Context.cellName(index: Int): String {
    require(index in 0 until SudokuBoard.CELL_COUNT)
    val row = index / SudokuBoard.SIZE
    val column = index % SudokuBoard.SIZE
    return getString(R.string.v08_cell_coordinate, row + 1, column + 1)
}

private fun Context.cellNames(indices: Set<Int>): String =
    indices.sorted().joinToString("; ") { cellName(it) }
