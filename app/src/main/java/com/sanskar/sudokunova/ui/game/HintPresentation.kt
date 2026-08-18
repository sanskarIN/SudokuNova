package com.sanskar.sudokunova.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.HintTechnique
import com.sanskar.sudokunova.engine.SudokuHint
import com.sanskar.sudokunova.engine.SudokuUnitRef
import com.sanskar.sudokunova.engine.SudokuUnitType

val HintTechnique.displayName: String
    @Composable get() = localizedHintTechnique(this)

val SudokuHint.explanation: String
    @Composable get() = localizedHintExplanation(this)

@Composable
fun localizedHintTechnique(technique: HintTechnique): String = stringResource(
    when (technique) {
        HintTechnique.NAKED_SINGLE -> R.string.v08_hint_naked_single
        HintTechnique.HIDDEN_SINGLE -> R.string.v08_hint_hidden_single
        HintTechnique.NAKED_PAIR -> R.string.v08_hint_naked_pair
        HintTechnique.POINTING_PAIR_OR_TRIPLE -> R.string.v08_hint_pointing
        HintTechnique.BOX_LINE_REDUCTION -> R.string.v08_hint_box_line
        HintTechnique.HIDDEN_PAIR -> R.string.v08_hint_hidden_pair
        HintTechnique.NAKED_TRIPLE -> R.string.v08_hint_naked_triple
        HintTechnique.HIDDEN_TRIPLE -> R.string.v08_hint_hidden_triple
        HintTechnique.X_WING -> R.string.v08_hint_x_wing
        HintTechnique.REVEAL -> R.string.v08_hint_reveal
    },
)

@Composable
fun localizedHintExplanation(hint: SudokuHint): String {
    val placement = hint.placement
    val placementCell = localizedCellLabel(placement.cellIndex)

    if (hint.technique == HintTechnique.REVEAL) {
        return stringResource(R.string.v08_hint_reveal_body, placement.value, placementCell)
    }

    val finalStep = hint.teachingSteps.last()
    val base = when (hint.technique) {
        HintTechnique.NAKED_SINGLE -> stringResource(
            R.string.v08_hint_naked_single_body,
            placement.value,
            placementCell,
        )
        HintTechnique.HIDDEN_SINGLE -> stringResource(
            R.string.v08_hint_hidden_single_body,
            localizedUnitLabel(finalStep.sourceUnit),
            placement.value,
            placementCell,
        )
        else -> {
            val eliminations = hint.teachingSteps.sumOf { it.candidateEliminations.size }
            val targets = hint.teachingSteps
                .flatMap { it.candidateEliminations }
                .map { it.cellIndex }
                .distinct()
                .size
            stringResource(
                R.string.v08_hint_elimination_body,
                localizedHintTechnique(hint.technique),
                eliminations,
                targets,
                placement.value,
                placementCell,
            )
        }
    }

    if (hint.teachingSteps.size <= 1) return base
    val chain = stringResource(R.string.v08_hint_chain_steps, hint.teachingSteps.size)
    val advanced = if (hint.usesAdvancedElimination) {
        "\n" + stringResource(R.string.v08_hint_chain_advanced)
    } else {
        ""
    }
    return "$base\n\n$chain$advanced"
}

@Composable
fun localizedCellLabel(cellIndex: Int): String {
    val row = cellIndex / 9 + 1
    val column = cellIndex % 9 + 1
    return stringResource(R.string.v08_cell_label, row, column)
}

@Composable
private fun localizedUnitLabel(unit: SudokuUnitRef?): String {
    if (unit == null) return ""
    return when (unit.type) {
        SudokuUnitType.ROW -> stringResource(R.string.v08_unit_row, unit.index + 1)
        SudokuUnitType.COLUMN -> stringResource(R.string.v08_unit_column, unit.index + 1)
        SudokuUnitType.BOX -> stringResource(R.string.v08_unit_box, unit.index + 1)
    }
}
