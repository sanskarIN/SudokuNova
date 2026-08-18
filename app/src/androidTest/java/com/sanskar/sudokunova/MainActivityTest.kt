package com.sanskar.sudokunova

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.ui.game.sudokuCellTestTag
import com.sanskar.sudokunova.ui.learn.LEARN_LIST_TEST_TAG
import com.sanskar.sudokunova.ui.learn.practiceChoiceTestTag
import com.sanskar.sudokunova.ui.learn.practiceTestTag
import com.sanskar.sudokunova.ui.learn.studyTestTag
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeShowsCoreEntryPoints() {
        composeRule.onNodeWithText("SudokuNova").assertIsDisplayed()
        composeRule.onNodeWithText("Daily Challenge").assertIsDisplayed()
        composeRule.onNodeWithText("Easy").assertIsDisplayed()
        composeRule.onNodeWithText("Custom").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Learn").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("History").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Saved Puzzles").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun gameBoardExposesSelectedCellSemantics() {
        composeRule.onNodeWithText("Easy").performClick()

        val firstCell = composeRule.onNodeWithTag(sudokuCellTestTag(0, 0))
        val secondCell = composeRule.onNodeWithTag(sudokuCellTestTag(0, 1))

        firstCell.assertIsDisplayed().performClick().assertIsSelected()
        secondCell.assertIsDisplayed().performClick().assertIsSelected()
        firstCell.assertIsNotSelected()
    }

    @Test
    fun challengeArchiveIsReachable() {
        composeRule.onNodeWithText("Daily Challenge").performClick()
        composeRule.onNodeWithText("Challenges").assertIsDisplayed()
        composeRule.onNodeWithText("Daily Challenge archive").assertIsDisplayed()
        composeRule.onNodeWithText("Weekly").assertIsDisplayed()
        composeRule.onAllNodesWithText("Play challenge").onFirst().assertIsDisplayed()
    }

    @Test
    fun historyAndSavedPuzzlesAreReachable() {
        composeRule.onNodeWithText("History").performScrollTo().performClick()
        composeRule.onNodeWithText("History").assertIsDisplayed()
        composeRule.onNodeWithText("No completed games yet.").assertIsDisplayed()
        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }

        composeRule.onNodeWithText("Saved Puzzles").performScrollTo().performClick()
        composeRule.onNodeWithText("Saved Puzzles").assertIsDisplayed()
        composeRule.onNodeWithText("No saved puzzles yet.").assertIsDisplayed()
    }

    @Test
    fun settingsShowsRestoredInputModeControls() {
        composeRule.onNodeWithText("Settings").performScrollTo().performClick()
        composeRule.onNodeWithText("Input mode").assertIsDisplayed()
        composeRule.onNodeWithText("Cell first").assertIsDisplayed()
        composeRule.onNodeWithText("Number first").assertIsDisplayed()
    }

    @Test
    fun customPuzzleEditorIsReachable() {
        composeRule.onNodeWithText("Custom").performScrollTo().performClick()
        composeRule.onNodeWithText("Custom Puzzle").assertIsDisplayed()
        composeRule.onNodeWithText("Validate").assertIsDisplayed()
        composeRule.onNodeWithText("Save puzzle").assertIsDisplayed()
        composeRule.onNodeWithText("Play puzzle").assertIsDisplayed()
    }

    @Test
    fun learnCenterSupportsLessonAndPracticeFlow() {
        val technique = LogicalTechnique.NAKED_SINGLE

        composeRule.onNodeWithText("Learn").performScrollTo().performClick()
        composeRule.onNodeWithText("Learning progress").assertIsDisplayed()
        composeRule.onNodeWithTag(LEARN_LIST_TEST_TAG).performScrollToIndex(5)

        composeRule.onNodeWithTag(studyTestTag(technique)).assertIsDisplayed().performClick()
        composeRule.onAllNodesWithText("Naked Single").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("Close practice").performClick()

        composeRule.onNodeWithTag(practiceTestTag(technique)).assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Technique practice").assertIsDisplayed()
        composeRule.onNodeWithText("Which technique explains this vetted logical step?").assertIsDisplayed()
        composeRule.onNodeWithTag(practiceChoiceTestTag(technique)).performClick()
        composeRule.onNodeWithText("Correct. This evidence matches Naked Single.").assertIsDisplayed()
    }
}
