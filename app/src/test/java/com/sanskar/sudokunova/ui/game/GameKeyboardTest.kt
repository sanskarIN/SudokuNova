package com.sanskar.sudokunova.ui.game

import androidx.compose.ui.input.key.Key
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameKeyboardTest {
    @Test
    fun arrowKeysMapToGridMovement() {
        assertEquals(
            GameKeyboardAction.MoveSelection(0, -1),
            resolveGameKeyboardAction(Key.DirectionLeft, 0),
        )
        assertEquals(
            GameKeyboardAction.MoveSelection(1, 0),
            resolveGameKeyboardAction(Key.DirectionDown, 0),
        )
    }

    @Test
    fun digitsNotesHintsAndEraseAreMapped() {
        assertEquals(GameKeyboardAction.EnterNumber(7), resolveGameKeyboardAction(Key.Unknown, '7'.code))
        assertEquals(GameKeyboardAction.ToggleNotes, resolveGameKeyboardAction(Key.Unknown, 'N'.code))
        assertEquals(GameKeyboardAction.Hint, resolveGameKeyboardAction(Key.Unknown, 'h'.code))
        assertEquals(GameKeyboardAction.Erase, resolveGameKeyboardAction(Key.Backspace, 0))
        assertEquals(GameKeyboardAction.Erase, resolveGameKeyboardAction(Key.Delete, 0))
        assertNull(resolveGameKeyboardAction(Key.Unknown, 'x'.code))
    }

    @Test
    fun movementStaysInsideSudokuGrid() {
        assertEquals(0, moveSudokuSelection(0, -1, -1))
        assertEquals(8, moveSudokuSelection(8, 0, 1))
        assertEquals(72, moveSudokuSelection(72, 1, -1))
        assertEquals(80, moveSudokuSelection(80, 1, 1))
        assertEquals(41, moveSudokuSelection(40, 0, 1))
        assertEquals(49, moveSudokuSelection(40, 1, 0))
    }
}
