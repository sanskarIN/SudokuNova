package com.sanskar.sudokunova.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.InputMode
import com.sanskar.sudokunova.data.UserSettings
import com.sanskar.sudokunova.ui.common.localizedThemeLabel
import com.sanskar.sudokunova.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    onBack: () -> Unit,
    onTheme: (AppTheme) -> Unit,
    onDynamicColor: (Boolean) -> Unit,
    onInputMode: (InputMode) -> Unit,
    onHighlightPeers: (Boolean) -> Unit,
    onHighlightSame: (Boolean) -> Unit,
    onAutoCheck: (Boolean) -> Unit,
    onAutoRemoveNotes: (Boolean) -> Unit,
    onShowTimer: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onSounds: (Boolean) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onHighContrast: (Boolean) -> Unit,
    onMistakeLimit: (Int) -> Unit,
    onResetStatistics: () -> Unit,
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v04_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.v04_back))
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item { SectionTitle(stringResource(R.string.v04_appearance)) }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.v04_theme)) },
                    supportingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppTheme.entries.forEach { theme ->
                                FilterChip(
                                    selected = settings.theme == theme,
                                    onClick = { onTheme(theme) },
                                    label = { Text(localizedThemeLabel(theme)) },
                                )
                            }
                        }
                    },
                )
            }
            item { ToggleSetting(stringResource(R.string.v04_dynamic_color), stringResource(R.string.v04_dynamic_color_desc), settings.dynamicColor, onDynamicColor) }
            item { ToggleSetting(stringResource(R.string.v04_high_contrast), stringResource(R.string.v04_high_contrast_desc), settings.highContrast, onHighContrast) }
            item { ToggleSetting(stringResource(R.string.v04_reduced_motion), stringResource(R.string.v04_reduced_motion_desc), settings.reducedMotion, onReducedMotion) }

            item { HorizontalDivider() }
            item { SectionTitle(stringResource(R.string.v04_gameplay)) }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.v06_input_mode)) },
                    supportingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = settings.inputMode == InputMode.CELL_FIRST,
                                onClick = { onInputMode(InputMode.CELL_FIRST) },
                                label = { Text(stringResource(R.string.v06_cell_first)) },
                            )
                            FilterChip(
                                selected = settings.inputMode == InputMode.NUMBER_FIRST,
                                onClick = { onInputMode(InputMode.NUMBER_FIRST) },
                                label = { Text(stringResource(R.string.v06_number_first)) },
                            )
                        }
                    },
                    overlineContent = { Text(stringResource(R.string.v06_input_mode_desc)) },
                )
            }
            item { ToggleSetting(stringResource(R.string.v04_highlight_peers), stringResource(R.string.v04_highlight_peers_desc), settings.highlightPeers, onHighlightPeers) }
            item { ToggleSetting(stringResource(R.string.v04_highlight_same), stringResource(R.string.v04_highlight_same_desc), settings.highlightSameNumbers, onHighlightSame) }
            item { ToggleSetting(stringResource(R.string.v04_auto_check), stringResource(R.string.v04_auto_check_desc), settings.autoCheckMistakes, onAutoCheck) }
            item { ToggleSetting(stringResource(R.string.v04_auto_remove_notes), stringResource(R.string.v04_auto_remove_notes_desc), settings.autoRemoveNotes, onAutoRemoveNotes) }
            item { ToggleSetting(stringResource(R.string.v04_show_timer), stringResource(R.string.v04_show_timer_desc), settings.showTimer, onShowTimer) }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.v04_mistake_limit)) },
                    supportingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0 to stringResource(R.string.v04_unlimited), 3 to "3", 5 to "5").forEach { (value, label) ->
                                FilterChip(
                                    selected = settings.mistakeLimit == value,
                                    onClick = { onMistakeLimit(value) },
                                    label = { Text(label) },
                                )
                            }
                        }
                    },
                )
            }

            item { HorizontalDivider() }
            item { SectionTitle(stringResource(R.string.v04_feedback)) }
            item { ToggleSetting(stringResource(R.string.v04_haptics), stringResource(R.string.v04_haptics_desc), settings.haptics, onHaptics) }
            item { ToggleSetting(stringResource(R.string.v04_sounds), stringResource(R.string.v04_sounds_desc), settings.sounds, onSounds) }

            item { HorizontalDivider() }
            item { SectionTitle(stringResource(R.string.v04_data)) }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.v04_reset_statistics)) },
                    supportingContent = { Text(stringResource(R.string.v04_reset_statistics_desc)) },
                    trailingContent = {
                        TextButton(onClick = { showResetDialog = true }) { Text(stringResource(R.string.v04_reset)) }
                    },
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.v04_reset_statistics_question)) },
            text = { Text(stringResource(R.string.v04_reset_statistics_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetStatistics()
                        showResetDialog = false
                    },
                ) { Text(stringResource(R.string.v04_reset)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text(stringResource(R.string.v04_cancel)) }
            },
        )
    }
}

@Composable
private fun ToggleSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
