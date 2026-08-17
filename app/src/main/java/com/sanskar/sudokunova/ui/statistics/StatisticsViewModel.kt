package com.sanskar.sudokunova.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.sudokunova.data.AppPreferencesRepository
import com.sanskar.sudokunova.data.PlayerStatistics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppPreferencesRepository(application.applicationContext)

    val statistics = repository.statistics.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlayerStatistics(),
    )
}
