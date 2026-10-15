package com.sanskar.sudokunova.shared

import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.engine.HintTechnique

sealed interface SharedGameStatus {
    data object SelectCell : SharedGameStatus
    data object FixedCellSelected : SharedGameStatus
    data class CellSelected(val row: Int, val column: Int) : SharedGameStatus
    data class NewPuzzle(val difficulty: Difficulty) : SharedGameStatus
    data class ImportedPuzzle(val difficulty: Difficulty) : SharedGameStatus
    data object InvalidPuzzleCode : SharedGameStatus
    data object NotesEnabled : SharedGameStatus
    data object NotesDisabled : SharedGameStatus
    data object SelectEditableCell : SharedGameStatus
    data object FixedClue : SharedGameStatus
    data object NotesUpdated : SharedGameStatus
    data object Solved : SharedGameStatus
    data object Conflict : SharedGameStatus
    data object Incorrect : SharedGameStatus
    data object GoodMove : SharedGameStatus
    data object CellCleared : SharedGameStatus
    data object NothingToUndo : SharedGameStatus
    data object MoveUndone : SharedGameStatus
    data object AlreadySolved : SharedGameStatus
    data object NoSafeHint : SharedGameStatus
    data class Hint(val technique: HintTechnique) : SharedGameStatus
    data object Reset : SharedGameStatus
}
