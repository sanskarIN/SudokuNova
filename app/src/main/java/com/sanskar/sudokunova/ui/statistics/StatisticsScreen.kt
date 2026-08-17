package com.sanskar.sudokunova.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.data.PlayerStatistics

@Composable
fun StatisticsRoute(
    onBack: () -> Unit,
    viewModel: StatisticsViewModel = viewModel(),
) {
    val statistics by viewModel.statistics.collectAsStateWithLifecycle()
    StatisticsScreen(statistics, onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsScreen(
    statistics: PlayerStatistics,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Completed", statistics.gamesCompleted.toString(), Modifier.weight(1f))
                    StatCard("Completion", "${statistics.completionRate}%", Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Current streak", statistics.currentStreak.toString(), Modifier.weight(1f))
                    StatCard("Longest streak", statistics.longestStreak.toString(), Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Best time", statistics.bestTimeSeconds?.let(::formatTime) ?: "—", Modifier.weight(1f))
                    StatCard("Play time", formatTime(statistics.totalPlaySeconds), Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Perfect games", statistics.perfectGames.toString(), Modifier.weight(1f))
                    StatCard("No-hint games", statistics.noHintGames.toString(), Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard("Mistakes", statistics.totalMistakes.toString(), Modifier.weight(1f))
                    StatCard("Hints used", statistics.totalHints.toString(), Modifier.weight(1f))
                }
            }
            item {
                Text("Achievements", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
            }
            item {
                AchievementCard("First Puzzle", "Complete your first puzzle.", statistics.gamesCompleted >= 1)
            }
            item {
                AchievementCard("10 Wins", "Complete 10 puzzles.", statistics.gamesCompleted >= 10)
            }
            item {
                AchievementCard("100 Wins", "Complete 100 puzzles.", statistics.gamesCompleted >= 100)
            }
            item {
                AchievementCard("Perfect Game", "Finish with no mistakes and no hints.", statistics.perfectGames >= 1)
            }
            item {
                AchievementCard("Seven-Day Streak", "Complete puzzles on seven consecutive days.", statistics.longestStreak >= 7)
            }
            item {
                AchievementCard("Thirty-Day Streak", "Complete puzzles on thirty consecutive days.", statistics.longestStreak >= 30)
            }
            item { Text("Statistics are stored locally on this device.", modifier = Modifier.padding(vertical = 20.dp)) }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AchievementCard(title: String, description: String, unlocked: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                if (unlocked) "✓ $title" else "○ $title",
                style = MaterialTheme.typography.titleLarge,
                color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remaining = seconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, remaining)
    else "%02d:%02d".format(minutes, remaining)
}
