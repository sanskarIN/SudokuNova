package com.sanskar.sudokunova.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.engine.Difficulty

@Composable
fun localizedDifficultyLabel(difficulty: Difficulty): String = stringResource(
    when (difficulty) {
        Difficulty.BEGINNER -> R.string.v06_difficulty_beginner
        Difficulty.EASY -> R.string.v06_difficulty_easy
        Difficulty.MEDIUM -> R.string.v06_difficulty_medium
        Difficulty.HARD -> R.string.v06_difficulty_hard
        Difficulty.EXPERT -> R.string.v06_difficulty_expert
        Difficulty.MASTER -> R.string.v06_difficulty_master
        Difficulty.EXTREME -> R.string.v06_difficulty_extreme
    },
)
