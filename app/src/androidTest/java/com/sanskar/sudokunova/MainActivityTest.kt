package com.sanskar.sudokunova

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
        composeRule.onNodeWithText("History").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Saved Puzzles").performScrollTo().assertIsDisplayed()
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
    fun learningProgressIsReachableFromLearn() {
        composeRule.onNodeWithText("Learn").performScrollTo().performClick()
        composeRule.onNodeWithText("Learn Sudoku").assertIsDisplayed()
        composeRule.onNodeWithText("Open learning progress").performScrollTo().performClick()
        composeRule.onNodeWithText("Learning Progress").assertIsDisplayed()
        composeRule.onNodeWithText("Naked Single").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Hint views: 0").performScrollTo().assertIsDisplayed()
    }
}
