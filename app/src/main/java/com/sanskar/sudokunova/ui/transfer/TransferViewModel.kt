package com.sanskar.sudokunova.ui.transfer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.transfer.BackupCodec
import com.sanskar.sudokunova.data.transfer.BackupImportResult
import com.sanskar.sudokunova.data.transfer.BackupRepository
import com.sanskar.sudokunova.engine.PuzzleCodeCodec
import com.sanskar.sudokunova.engine.SharedPuzzleCode
import com.sanskar.sudokunova.engine.SudokuSolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class TransferStatus {
    IDLE,
    BACKUP_FAILED,
    RESTORE_INVALID,
    PUZZLE_INVALID,
}

data class TransferUiState(
    val puzzleCodeInput: String = "",
    val validatedPuzzle: SharedPuzzleCode? = null,
    val importResult: BackupImportResult? = null,
    val status: TransferStatus = TransferStatus.IDLE,
    val busy: Boolean = false,
)

class TransferViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BackupRepository(application.applicationContext)
    private val solver = SudokuSolver()
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    fun setPuzzleCode(value: String) {
        if (value.length > PuzzleCodeCodec.MAX_CODE_LENGTH) return
        _uiState.value = _uiState.value.copy(
            puzzleCodeInput = value,
            validatedPuzzle = null,
            status = TransferStatus.IDLE,
        )
    }

    fun validatePuzzleCode() {
        val raw = _uiState.value.puzzleCodeInput
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(busy = true, status = TransferStatus.IDLE)
            val decoded = withContext(Dispatchers.Default) {
                PuzzleCodeCodec.decode(raw)?.takeIf { shared ->
                    val analysis = solver.analyze(shared.puzzle, solutionLimit = 2)
                    analysis.solutionCount == 1 && analysis.solution != null
                }
            }
            _uiState.value = _uiState.value.copy(
                validatedPuzzle = decoded,
                status = if (decoded == null) TransferStatus.PUZZLE_INVALID else TransferStatus.IDLE,
                busy = false,
            )
        }
    }

    fun createBackup(onReady: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(busy = true, status = TransferStatus.IDLE)
            val text = runCatching { withContext(Dispatchers.IO) { repository.exportText() } }.getOrNull()
            _uiState.value = _uiState.value.copy(
                busy = false,
                status = if (text == null) TransferStatus.BACKUP_FAILED else TransferStatus.IDLE,
            )
            if (text != null) onReady(text)
        }
    }

    fun restoreBackup(raw: String) {
        if (raw.length > BackupCodec.MAX_BACKUP_BYTES) {
            _uiState.value = _uiState.value.copy(importResult = null, status = TransferStatus.RESTORE_INVALID)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(busy = true, importResult = null, status = TransferStatus.IDLE)
            val result = runCatching { withContext(Dispatchers.IO) { repository.importText(raw) } }.getOrNull()
            _uiState.value = _uiState.value.copy(
                busy = false,
                importResult = result,
                status = if (result == null) TransferStatus.RESTORE_INVALID else TransferStatus.IDLE,
            )
        }
    }

    fun clearImportResult() {
        _uiState.value = _uiState.value.copy(importResult = null, status = TransferStatus.IDLE)
    }
}
