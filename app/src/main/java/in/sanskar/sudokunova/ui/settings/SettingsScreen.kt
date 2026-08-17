package in.sanskar.sudokunova.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import in.sanskar.sudokunova.data.UserSettings
import in.sanskar.sudokunova.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    onBack: () -> Unit,
    onTheme: (AppTheme) -> Unit,
    onDynamicColor: (Boolean) -> Unit,
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
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item { SectionTitle("Appearance") }
            item {
                ListItem(
                    headlineContent = { Text("Theme") },
                    supportingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppTheme.entries.forEach { theme ->
                                FilterChip(
                                    selected = settings.theme == theme,
                                    onClick = { onTheme(theme) },
                                    label = {
                                        Text(theme.name.lowercase().replaceFirstChar { it.uppercase() })
                                    },
                                )
                            }
                        }
                    },
                )
            }
            item { ToggleSetting("Dynamic color", "Use Material You colors when available.", settings.dynamicColor, onDynamicColor) }
            item { ToggleSetting("High contrast", "Increase visual distinction for important states.", settings.highContrast, onHighContrast) }
            item { ToggleSetting("Reduced motion", "Prefer fewer non-essential animations.", settings.reducedMotion, onReducedMotion) }

            item { HorizontalDivider() }
            item { SectionTitle("Gameplay") }
            item { ToggleSetting("Highlight peers", "Highlight the selected row, column and box.", settings.highlightPeers, onHighlightPeers) }
            item { ToggleSetting("Highlight same numbers", "Highlight matching placed values.", settings.highlightSameNumbers, onHighlightSame) }
            item { ToggleSetting("Auto-check mistakes", "Mark entries that disagree with the solution.", settings.autoCheckMistakes, onAutoCheck) }
            item { ToggleSetting("Auto-remove notes", "Remove a correct value from peer candidates.", settings.autoRemoveNotes, onAutoRemoveNotes) }
            item { ToggleSetting("Show timer", "Display elapsed solving time.", settings.showTimer, onShowTimer) }
            item {
                ListItem(
                    headlineContent = { Text("Mistake limit") },
                    supportingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0 to "Unlimited", 3 to "3", 5 to "5").forEach { (value, label) ->
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
            item { SectionTitle("Feedback") }
            item { ToggleSetting("Haptics", "Allow subtle touch feedback.", settings.haptics, onHaptics) }
            item { ToggleSetting("Sounds", "Allow optional game sounds.", settings.sounds, onSounds) }

            item { HorizontalDivider() }
            item { SectionTitle("Data") }
            item {
                ListItem(
                    headlineContent = { Text("Reset statistics") },
                    supportingContent = { Text("Delete local statistics and streak data.") },
                    trailingContent = {
                        TextButton(onClick = { showResetDialog = true }) { Text("Reset") }
                    },
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset statistics?") },
            text = { Text("This permanently clears your local statistics. Your settings and active game are not changed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetStatistics()
                        showResetDialog = false
                    },
                ) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
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
