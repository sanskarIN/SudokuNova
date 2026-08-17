package com.sanskar.sudokunova

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
        composeRule.onNodeWithText("Custom").assertIsDisplayed()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun settingsShowsInputAndFeedbackControls() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Input mode").assertIsDisplayed()
        composeRule.onNodeWithText("Cell first").assertIsDisplayed()
        composeRule.onNodeWithText("Number first").assertIsDisplayed()
        composeRule.onNodeWithText("Haptics").assertIsDisplayed()
        composeRule.onNodeWithText("Sounds").assertIsDisplayed()
    }

    @Test
    fun customPuzzleEditorIsReachable() {
        composeRule.onNodeWithText("Custom").performClick()
        composeRule.onNodeWithText("Custom Puzzle").assertIsDisplayed()
        composeRule.onNodeWithText("Validate").assertIsDisplayed()
        composeRule.onNodeWithText("Play puzzle").assertIsDisplayed()
    }
}
