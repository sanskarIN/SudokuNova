package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.SudokuBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        assertEquals(SharedGameStatus.FixedClue, state.status)

        state.select(editableIndex)
        state.toggleNotesMode()
        assertTrue(state.notesMode)
        state.enter(correctValue)
        assertEquals(SudokuBoard.EMPTY, state.board.valueAt(editableIndex))
        assertTrue(correctValue in state.notes.getValue(editableIndex))
        assertEquals(SharedGameStatus.NotesUpdated, state.status)

        state.toggleNotesMode()
        assertFalse(state.notesMode)
        state.enter(correctValue)
        assertEquals(correctValue, state.board.valueAt(editableIndex))
        assertFalse(state.notes.containsKey(editableIndex))

        state.undo()
        assertEquals(SudokuBoard.EMPTY, state.board.valueAt(editableIndex))
        assertTrue(correctValue in state.notes.getValue(editableIndex))
        assertEquals(SharedGameStatus.MoveUndone, state.status)

        state.reset()
        assertEquals(startingBoard, state.board)
        assertTrue(state.notes.isEmpty())
        assertEquals(SharedGameStatus.Reset, state.status)
    }

    @Test
    fun hintAppliesAProgressingPlacement() {
        val state = SharedGameState()
        val before = state.board

        state.hint()

        assertNotEquals(before, state.board)
        assertTrue(state.board.isValid())
        assertTrue(state.selectedIndex != null)
        assertTrue(state.status is SharedGameStatus.Hint)
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
        assertEquals(SharedGameStatus.NewPuzzle(nextDifficulty), state.status)
    }

    @Test
    fun activeGameSnapshotRoundTripsDeterministically() {
        val state = SharedGameState()
        val editableIndex = state.board.emptyIndices().first()
        val candidate = state.board.candidates(editableIndex).first()

        state.select(editableIndex)
        state.toggleNotesMode()
        state.enter(candidate)
        val snapshot = state.snapshot()

        state.newGame(Difficulty.HARD)

        assertTrue(state.restore(snapshot))
        assertEquals(snapshot.difficulty, state.difficulty)
        assertEquals(snapshot.seed, state.generated.seed)
        assertEquals(snapshot.board, state.board.toPuzzleString())
        assertEquals(snapshot.notes, state.notes)
        assertEquals(snapshot.selectedIndex, state.selectedIndex)
        assertEquals(snapshot.notesMode, state.notesMode)
    }

    @Test
    fun restoreRejectsChangedStartingCluesWithoutMutatingCurrentGame() {
        val state = SharedGameState()
        val original = state.snapshot()
        val fixedIndex = (0 until SudokuBoard.CELL_COUNT).first(state::isFixed)
        val fixedValue = state.board.valueAt(fixedIndex)
        val replacement = if (fixedValue == 9) '1' else ('0'.code + fixedValue + 1).toChar()
        val corruptedBoard = original.board.toCharArray().also { it[fixedIndex] = replacement }.concatToString()

        assertFalse(state.restore(original.copy(board = corruptedBoard)))
        assertEquals(original, state.snapshot())
    }

    @Test
    fun restoreRejectsNotesOnFixedClues() {
        val state = SharedGameState()
        val original = state.snapshot()
        val fixedIndex = (0 until SudokuBoard.CELL_COUNT).first(state::isFixed)
        val corrupted = original.copy(notes = mapOf(fixedIndex to setOf(1)))

        assertFalse(state.restore(corrupted))
        assertEquals(original, state.snapshot())
    }

    @Test
    fun gridNavigationStartsPredictablyAndClampsAtEdges() {
        val state = SharedGameState()

        state.moveSelection(rowDelta = 0, columnDelta = 1)
        assertEquals(0, state.selectedIndex)

        state.moveSelection(rowDelta = 0, columnDelta = 1)
        assertEquals(1, state.selectedIndex)

        state.moveSelection(rowDelta = 1, columnDelta = 0)
        assertEquals(10, state.selectedIndex)

        state.select(0)
        state.moveSelection(rowDelta = -1, columnDelta = 0)
        state.moveSelection(rowDelta = 0, columnDelta = -1)
        assertEquals(0, state.selectedIndex)

        state.select(SudokuBoard.CELL_COUNT - 1)
        state.moveSelection(rowDelta = 1, columnDelta = 0)
        state.moveSelection(rowDelta = 0, columnDelta = 1)
        assertEquals(SudokuBoard.CELL_COUNT - 1, state.selectedIndex)

        assertFailsWith<IllegalArgumentException> { state.moveSelection(0, 0) }
        assertFailsWith<IllegalArgumentException> { state.moveSelection(2, 0) }
    }
}