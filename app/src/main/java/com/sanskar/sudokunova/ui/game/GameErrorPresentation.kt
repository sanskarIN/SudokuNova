package com.sanskar.sudokunova.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R

val GameScreenState.Error.message: String
    @Composable get() = localizedGameError(error)

@Composable
fun localizedGameError(error: GameError): String = stringResource(
    when (error) {
        GameError.ABANDONED -> R.string.v09_game_abandoned
        GameError.CUSTOM_CONTRADICTION -> R.string.v09_game_custom_contradiction
        GameError.CUSTOM_NOT_UNIQUE -> R.string.v09_game_custom_not_unique
        GameError.CREATION_FAILED -> R.string.v09_game_creation_failed
    },
)
