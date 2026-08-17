package com.sanskar.sudokunova.ui.common

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.engine.LogicalUnit
import com.sanskar.sudokunova.engine.LogicalUnitType
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.engine.TeachingStep

@Composable
fun localizedTeachingTechnique(technique: LogicalTechnique): String =
    stringResource(technique.titleResource())

@Composable
fun localizedTeachingExplanation(step: TeachingStep): String =
    teachingExplanation(LocalResources.current, step)

internal fun teachingExplanation(
    resources: Resources,
    step: TeachingStep,
): String {
    val affectedCells = step.eliminations.map { it.cellIndex }.toSet()
    return when (step.technique) {
        LogicalTechnique.NAKED_SINGLE -> {
            val placement = requireNotNull(step.placement)
            resources.getString(
                R.string.v08_explain_naked_single,
                resources.cellName(placement.cellIndex),
                placement.value,
            )
        }
        LogicalTechnique.HIDDEN_SINGLE -> {
            val placement = requireNotNull(step.placement)
            resources.getString(
                R.string.v08_explain_hidden_single,
                placement.value,
                resources.unitName(requireNotNull(step.sourceUnit)),
                resources.cellName(placement.cellIndex),
            )
        }
        LogicalTechnique.NAKED_PAIR -> resources.getString(
            R.string.v08_explain_naked_pair,
            resources.unitName(requireNotNull(step.sourceUnit)),
            resources.cellNames(step.sourceCells),
            step.candidateValues.sorted().joinToString(", "),
            resources.cellNames(affectedCells),
        )
        LogicalTechnique.POINTING_PAIR_OR_TRIPLE -> resources.getString(
            R.string.v08_explain_pointing,
            resources.unitName(requireNotNull(step.sourceUnit)),
            step.candidateValues.single(),
            resources.unitName(requireNotNull(step.affectedUnit)),
            resources.cellNames(affectedCells),
        )
        LogicalTechnique.BOX_LINE_REDUCTION -> resources.getString(
            R.string.v08_explain_box_line,
            resources.unitName(requireNotNull(step.sourceUnit)),
            step.candidateValues.single(),
            resources.unitName(requireNotNull(step.affectedUnit)),
            resources.cellNames(affectedCells),
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

private fun Resources.unitName(unit: LogicalUnit): String = when (unit.type) {
    LogicalUnitType.ROW -> getString(R.string.v08_unit_row, unit.index + 1)
    LogicalUnitType.COLUMN -> getString(R.string.v08_unit_column, unit.index + 1)
    LogicalUnitType.BOX -> getString(R.string.v08_unit_box, unit.index + 1)
}

private fun Resources.cellName(index: Int): String {
    require(index in 0 until SudokuBoard.CELL_COUNT)
    val row = index / SudokuBoard.SIZE
    val column = index % SudokuBoard.SIZE
    return getString(R.string.v08_cell_coordinate, row + 1, column + 1)
}

private fun Resources.cellNames(indices: Set<Int>): String =
    indices.sorted().joinToString("; ") { cellName(it) }
