package com.sanskar.sudokunova.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.Difficulty

@StringRes
fun Difficulty.labelResource(): Int = when (this) {
    Difficulty.BEGINNER -> R.string.difficulty_beginner
    Difficulty.EASY -> R.string.difficulty_easy
    Difficulty.MEDIUM -> R.string.difficulty_medium
    Difficulty.HARD -> R.string.difficulty_hard
    Difficulty.EXPERT -> R.string.difficulty_expert
    Difficulty.MASTER -> R.string.difficulty_master
    Difficulty.EXTREME -> R.string.difficulty_extreme
}

@Composable
fun localizedDifficultyLabel(difficulty: Difficulty): String = stringResource(difficulty.labelResource())
