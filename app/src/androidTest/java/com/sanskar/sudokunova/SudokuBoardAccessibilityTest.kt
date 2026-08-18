package com.sanskar.sudokunova

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.engine.CandidateElimination
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.engine.SudokuBoard
import com.sanskar.sudokunova.engine.SudokuHint
import com.sanskar.sudokunova.engine.SudokuUnitRef
import com.sanskar.sudokunova.engine.SudokuUnitType
import com.sanskar.sudokunova.engine.TeachingPlacement
import com.sanskar.sudokunova.engine.TeachingStep
import com.sanskar.sudokunova.engine.SudokuSolver
import com.sanskar.sudokunova.game.GameState
import com.sanskar.sudokunova.ui.game.SudokuBoardView
import org.junit.Rule
import org.junit.Test

class SudokuBoardAccessibilityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun teachingHintAnnouncesSourceEliminationAndPlacementWithoutColorDependency() {
        val puzzle = SudokuBoard.parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079",
        )
        val solution = requireNotNull(SudokuSolver().solve(puzzle).solution)
        val game = GameState(
            puzzle = puzzle,
            solution = solution,
            board = puzzle,
        )
        val hint = SudokuHint(
            teachingSteps = listOf(
                TeachingStep(
                    technique = LogicalTechnique.NAKED_PAIR,
                    sourceCells = listOf(2, 3),
                    sourceUnit = SudokuUnitRef(SudokuUnitType.ROW, 0),
                    targetCells = listOf(5),
                    candidateEliminations = listOf(CandidateElimination(5, 4)),
                ),
                TeachingStep(
                    technique = LogicalTechnique.NAKED_SINGLE,
                    sourceCells = listOf(6),
                    sourceUnit = null,
                    targetCells = listOf(6),
                    placement = TeachingPlacement(6, solution.valueAt(6)),
                ),
            ),
        )

        composeRule.setContent {
            MaterialTheme {
                SudokuBoardView(
                    game = game,
                    settings = UserSettings(),
                    hint = hint,
                    onCellSelected = {},
                )
            }
        }

        composeRule
            .onNodeWithContentDescription(
                "Teaching source: row 1, column 3",
                substring = true,
            )
            .assertExists()
        composeRule
            .onNodeWithContentDescription(
                "Candidate elimination target: row 1, column 6; remove 4",
                substring = true,
            )
            .assertExists()
        composeRule
            .onNodeWithContentDescription(
                "Hint placement target: row 1, column 7, value ${solution.valueAt(6)}",
                substring = true,
            )
            .assertExists()
    }
}
