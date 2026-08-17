package com.sanskar.sudokunova.ui.learn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.learning.LearningProgressRepository
import com.sanskar.sudokunova.data.learning.LearningProgressSnapshot
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LearningProgressViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository = LearningProgressRepository(application)

    val progress: StateFlow<LearningProgressSnapshot> = repository.progress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LearningProgressSnapshot.empty(),
    )

    fun reset() {
        viewModelScope.launch { repository.reset() }
    }
}
