package com.sanskar.sudokunova.ui.game

import androidx.compose.ui.input.key.Key

sealed interface GameKeyboardAction {
    data class MoveSelection(val rowDelta: Int, val columnDelta: Int) : GameKeyboardAction
    data class EnterNumber(val value: Int) : GameKeyboardAction
    data object Erase : GameKeyboardAction
    data object ToggleNotes : GameKeyboardAction
    data object Hint : GameKeyboardAction
}

fun resolveGameKeyboardAction(key: Key, utf16CodePoint: Int): GameKeyboardAction? {
    return when (key) {
        Key.DirectionLeft -> GameKeyboardAction.MoveSelection(rowDelta = 0, columnDelta = -1)
        Key.DirectionRight -> GameKeyboardAction.MoveSelection(rowDelta = 0, columnDelta = 1)
        Key.DirectionUp -> GameKeyboardAction.MoveSelection(rowDelta = -1, columnDelta = 0)
        Key.DirectionDown -> GameKeyboardAction.MoveSelection(rowDelta = 1, columnDelta = 0)
        Key.Backspace, Key.Delete -> GameKeyboardAction.Erase
        else -> when (utf16CodePoint) {
            in '1'.code..'9'.code -> GameKeyboardAction.EnterNumber(utf16CodePoint - '0'.code)
            'n'.code, 'N'.code -> GameKeyboardAction.ToggleNotes
            'h'.code, 'H'.code -> GameKeyboardAction.Hint
            else -> null
        }
    }
}

fun moveSudokuSelection(currentIndex: Int, rowDelta: Int, columnDelta: Int): Int {
    val safeIndex = currentIndex.coerceIn(0, 80)
    val row = safeIndex / 9
    val column = safeIndex % 9
    val nextRow = (row + rowDelta).coerceIn(0, 8)
    val nextColumn = (column + columnDelta).coerceIn(0, 8)
    return nextRow * 9 + nextColumn
}
