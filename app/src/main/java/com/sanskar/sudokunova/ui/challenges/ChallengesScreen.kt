package com.sanskar.sudokunova.ui.challenges

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.challenge.ChallengeDescriptor
import com.sanskar.sudokunova.data.challenge.ChallengeType
import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ChallengesRoute(
    onBack: () -> Unit,
    onPlay: (ChallengeDescriptor) -> Unit,
    viewModel: ChallengesViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChallengesScreen(
        state = state,
        onBack = onBack,
        onSelectType = viewModel::selectType,
        onPlay = onPlay,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChallengesScreen(
    state: ChallengesUiState,
    onBack: () -> Unit,
    onSelectType: (ChallengeType) -> Unit,
    onPlay: (ChallengeDescriptor) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v06_challenges)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.v06_back),
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
                        selected = state.selectedType == ChallengeType.DAILY,
                        onClick = { onSelectType(ChallengeType.DAILY) },
                        label = { Text(stringResource(R.string.v06_daily)) },
                    )
                    FilterChip(
                        selected = state.selectedType == ChallengeType.WEEKLY,
                        onClick = { onSelectType(ChallengeType.WEEKLY) },
                        label = { Text(stringResource(R.string.v06_weekly)) },
                    )
                }
            }

            item {
                Text(
                    if (state.selectedType == ChallengeType.DAILY) {
                        stringResource(R.string.v06_daily_archive)
                    } else {
                        stringResource(R.string.v06_weekly_archive)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    stringResource(R.string.v06_challenge_offline),
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            items(
                items = state.entries,
                key = { entry -> "${entry.descriptor.type}-${entry.descriptor.key}" },
            ) { entry ->
                ChallengeEntryCard(entry = entry, onPlay = { onPlay(entry.descriptor) })
            }

            item { Text("", modifier = Modifier.padding(bottom = 16.dp)) }
        }
    }
}

@Composable
private fun ChallengeEntryCard(
    entry: ChallengeEntry,
    onPlay: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val dateLabel = entry.displayDate.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale),
    )
    val result = entry.result

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        when {
                            entry.current && entry.descriptor.type == ChallengeType.DAILY ->
                                stringResource(R.string.v06_today)
                            entry.current -> stringResource(R.string.v06_this_week)
                            entry.descriptor.type == ChallengeType.WEEKLY ->
                                stringResource(R.string.v06_week_label, dateLabel)
                            else -> dateLabel
                        },
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        localizedDifficultyLabel(entry.descriptor.difficulty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    if (result != null) stringResource(R.string.v06_completed)
                    else stringResource(R.string.v06_not_completed),
                    color = if (result != null) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (result != null) {
                Text(
                    stringResource(
                        R.string.v06_challenge_result,
                        localizedDifficultyLabel(entry.descriptor.difficulty),
                        formatTime(result.elapsedSeconds),
                        result.mistakes,
                        result.hintsUsed,
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Button(
                onClick = onPlay,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            ) {
                Text(
                    if (result == null) stringResource(R.string.v06_play_challenge)
                    else stringResource(R.string.v06_replay_challenge),
                )
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
