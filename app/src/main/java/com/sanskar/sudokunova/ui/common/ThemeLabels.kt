package com.sanskar.sudokunova.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.ui.theme.AppTheme

@StringRes
fun AppTheme.labelResource(): Int = when (this) {
    AppTheme.SYSTEM -> R.string.theme_system
    AppTheme.LIGHT -> R.string.theme_light
    AppTheme.DARK -> R.string.theme_dark
}

@Composable
fun localizedThemeLabel(theme: AppTheme): String = stringResource(theme.labelResource())
