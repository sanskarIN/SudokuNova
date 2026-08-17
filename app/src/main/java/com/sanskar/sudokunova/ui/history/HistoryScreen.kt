package com.sanskar.sudokunova.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.history.DifficultyHistorySummary
import com.sanskar.sudokunova.data.history.GameHistoryEntity
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(
        state = state,
        onBack = onBack,
        onScope = viewModel::setScope,
        onDifficulty = viewModel::setDifficulty,
        onFavorite = viewModel::toggleFavorite,
        onDelete = viewModel::delete,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    onScope: (HistoryScope) -> Unit,
    onDifficulty: (Difficulty?) -> Unit,
    onFavorite: (GameHistoryEntity) -> Unit,
    onDelete: (GameHistoryEntity) -> Unit,
) {
    var pendingDelete by remember { mutableStateOf<GameHistoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v05_history)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.v04_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.scope == HistoryScope.ALL,
                        onClick = { onScope(HistoryScope.ALL) },
                        label = { Text(stringResource(R.string.v05_all)) },
                    )
                    FilterChip(
                        selected = state.scope == HistoryScope.FAVORITES,
                        onClick = { onScope(HistoryScope.FAVORITES) },
                        label = { Text(stringResource(R.string.v05_favorites)) },
                    )
                }
            }

            item {
                DifficultyFilterRow(
                    selected = state.difficulty,
                    onDifficulty = onDifficulty,
                )
            }

            if (state.summaries.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.v05_difficulty_summary),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                items(state.summaries, key = { "summary-${it.difficulty}" }) { summary ->
                    DifficultySummaryCard(summary)
                }
            }

            if (state.items.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp)) {
                            Text(
                                if (state.scope == HistoryScope.FAVORITES) {
                                    stringResource(R.string.v05_no_favorites)
                                } else {
                                    stringResource(R.string.v05_no_history)
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                stringResource(R.string.v05_history_hint),
                                modifier = Modifier.padding(top = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            } else {
                items(state.items, key = { "history-${it.id}" }) { item ->
                    HistoryCard(
                        item = item,
                        onFavorite = { onFavorite(item) },
                        onDelete = { pendingDelete = item },
                    )
                }
            }

            item { Text("", modifier = Modifier.padding(bottom = 16.dp)) }
        }
    }

    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.v05_delete_question)) },
            text = { Text(stringResource(R.string.v05_delete_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(item)
                        pendingDelete = null
                    },
                ) { Text(stringResource(R.string.v05_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.v04_cancel))
                }
            },
        )
    }
}

@Composable
private fun DifficultyFilterRow(
    selected: Difficulty?,
    onDifficulty: (Difficulty?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(
            selected = selected == null,
            onClick = { onDifficulty(null) },
            label = { Text(stringResource(R.string.v05_all_difficulties)) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Difficulty.entries.take(4).forEach { difficulty ->
                FilterChip(
                    selected = selected == difficulty,
                    onClick = { onDifficulty(difficulty) },
                    label = { Text(localizedDifficultyLabel(difficulty)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Difficulty.entries.drop(4).forEach { difficulty ->
                FilterChip(
                    selected = selected == difficulty,
                    onClick = { onDifficulty(difficulty) },
                    label = { Text(localizedDifficultyLabel(difficulty)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DifficultySummaryCard(summary: DifficultyHistorySummary) {
    val difficulty = runCatching { Difficulty.valueOf(summary.difficulty) }.getOrNull()
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(
                difficulty?.let { localizedDifficultyLabel(it) } ?: summary.difficulty,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.v05_games_count, summary.games))
                Text(stringResource(R.string.v05_best_time_value, formatTime(summary.bestSeconds)))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.v05_average_time, formatTime(summary.averageSeconds.toLong())))
                Text(stringResource(R.string.v05_perfect_count, summary.perfectGames))
            }
        }
    }
}

@Composable
private fun HistoryCard(
    item: GameHistoryEntity,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
) {
    val difficulty = runCatching { Difficulty.valueOf(item.difficulty) }.getOrNull()
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0] ?: Locale.getDefault()
    val date = item.completedAtEpochMillis?.let { formatDateTime(it, locale) } ?: "—"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        difficulty?.let { localizedDifficultyLabel(it) } ?: item.difficulty,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        stringResource(R.string.v05_played_on, date),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(
                            if (item.isFavorite) R.string.v05_unfavorite else R.string.v05_favorite,
                        ),
                        tint = if (item.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.v05_delete_history),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.v05_time_value, formatTime(item.elapsedSeconds)))
                Text(stringResource(R.string.v05_mistakes_value, item.mistakes))
                Text(stringResource(R.string.v05_hints_value, item.hintsUsed))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (item.isDailyChallenge) {
                    Text(stringResource(R.string.v05_daily_badge), color = MaterialTheme.colorScheme.primary)
                }
                if (item.isPerfect) {
                    Text(stringResource(R.string.v05_perfect_badge), color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val safe = seconds.coerceAtLeast(0)
    val hours = safe / 3600
    val minutes = (safe % 3600) / 60
    val remaining = safe % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, remaining)
    else "%02d:%02d".format(minutes, remaining)
}

private fun formatDateTime(epochMillis: Long, locale: Locale): String =
    DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withLocale(locale)
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(epochMillis))
