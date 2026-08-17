package com.sanskar.sudokunova.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.ui.theme.AppTheme

@Composable
fun localizedDifficultyLabel(difficulty: Difficulty): String = stringResource(
    when (difficulty) {
        Difficulty.BEGINNER -> R.string.difficulty_beginner
        Difficulty.EASY -> R.string.difficulty_easy
        Difficulty.MEDIUM -> R.string.difficulty_medium
        Difficulty.HARD -> R.string.difficulty_hard
        Difficulty.EXPERT -> R.string.difficulty_expert
        Difficulty.MASTER -> R.string.difficulty_master
        Difficulty.EXTREME -> R.string.difficulty_extreme
    },
)

@Composable
fun localizedThemeLabel(theme: AppTheme): String = stringResource(
    when (theme) {
        AppTheme.SYSTEM -> R.string.theme_system
        AppTheme.LIGHT -> R.string.theme_light
        AppTheme.DARK -> R.string.theme_dark
    },
)
