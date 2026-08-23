package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SharedGameStateTest {
    @Test
    fun notesMoveUndoResetAndFixedCluesRemainConsistent() {
        val state = SharedGameState()
        val startingBoard = state.generated.puzzle
        val editableIndex = startingBoard.emptyIndices().first()
        val correctValue = state.generated.solution.valueAt(editableIndex)
        val fixedIndex = (0 until SudokuBoard.CELL_COUNT).first(state::isFixed)
        val fixedValue = state.board.valueAt(fixedIndex)

        state.select(fixedIndex)
        state.enter(if (fixedValue == 9) 1 else fixedValue + 1)
        assertEquals(fixedValue, state.board.valueAt(fixedIndex))

        state.select(editableIndex)
        state.toggleNotesMode()
        assertTrue(state.notesMode)
        state.enter(correctValue)
        assertEquals(SudokuBoard.EMPTY, state.board.valueAt(editableIndex))
        assertTrue(correctValue in state.notes.getValue(editableIndex))

        state.toggleNotesMode()
        assertFalse(state.notesMode)
        state.enter(correctValue)
        assertEquals(correctValue, state.board.valueAt(editableIndex))
        assertFalse(state.notes.containsKey(editableIndex))

        state.undo()
        assertEquals(SudokuBoard.EMPTY, state.board.valueAt(editableIndex))
        assertTrue(correctValue in state.notes.getValue(editableIndex))

        state.reset()
        assertEquals(startingBoard, state.board)
        assertTrue(state.notes.isEmpty())
    }

    @Test
    fun hintAppliesAProgressingPlacement() {
        val state = SharedGameState()
        val before = state.board

        state.hint()

        assertNotEquals(before, state.board)
        assertTrue(state.board.isValid())
        assertTrue(state.selectedIndex != null)
    }

    @Test
    fun changingDifficultyStartsFreshGameAndPublishesReadOnlySelection() {
        val state = SharedGameState()
        val originalDifficulty = state.difficulty
        val nextDifficulty = Difficulty.entries.first { it != originalDifficulty }
        val editableIndex = state.board.emptyIndices().first()

        state.select(editableIndex)
        state.toggleNotesMode()
        state.enter(1)
        assertTrue(state.notes.isNotEmpty())

        state.setDifficulty(nextDifficulty)

        assertEquals(nextDifficulty, state.difficulty)
        assertEquals(state.generated.puzzle, state.board)
        assertEquals(null, state.selectedIndex)
        assertFalse(state.notesMode)
        assertTrue(state.notes.isEmpty())
        assertTrue(state.statusMessage.contains(nextDifficulty.displayName))
    }
}
