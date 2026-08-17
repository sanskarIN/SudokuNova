package in.sanskar.sudokunova.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import in.sanskar.sudokunova.engine.Difficulty

@Composable
fun HomeRoute(
    onStartGame: (Difficulty) -> Unit,
    onContinue: () -> Unit,
    onDailyChallenge: () -> Unit,
    onCustomPuzzle: () -> Unit,
    onLearn: () -> Unit,
    onStatistics: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onSupport: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val hasActiveGame by viewModel.hasActiveGame.collectAsStateWithLifecycle()
    HomeScreen(
        hasActiveGame = hasActiveGame,
        onStartGame = onStartGame,
        onContinue = onContinue,
        onDailyChallenge = onDailyChallenge,
        onCustomPuzzle = onCustomPuzzle,
        onLearn = onLearn,
        onStatistics = onStatistics,
        onSettings = onSettings,
        onAbout = onAbout,
        onSupport = onSupport,
    )
}

@Composable
private fun HomeScreen(
    hasActiveGame: Boolean,
    onStartGame: (Difficulty) -> Unit,
    onContinue: () -> Unit,
    onDailyChallenge: () -> Unit,
    onCustomPuzzle: () -> Unit,
    onLearn: () -> Unit,
    onStatistics: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onSupport: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Spacer(Modifier.height(20.dp))
            Text("SudokuNova", style = MaterialTheme.typography.displaySmall)
            Text(
                "Think. Solve. Master the Grid.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (hasActiveGame) {
            item {
                Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text("  Continue game")
                }
            }
        }

        item {
            FeatureCard(
                title = "Daily Challenge",
                subtitle = "A deterministic puzzle for today — fully offline.",
                icon = Icons.Default.PlayArrow,
                onClick = onDailyChallenge,
            )
        }

        item {
            Text("Choose difficulty", style = MaterialTheme.typography.titleLarge)
        }

        items(Difficulty.entries.chunked(2).size) { rowIndex ->
            val pair = Difficulty.entries.chunked(2)[rowIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { difficulty ->
                    OutlinedButton(
                        onClick = { onStartGame(difficulty) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(difficulty.displayName)
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item {
            Text("Explore", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactAction("Custom", Icons.Default.Edit, Modifier.weight(1f), onCustomPuzzle)
                CompactAction("Learn", Icons.Default.School, Modifier.weight(1f), onLearn)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactAction("Statistics", Icons.Default.BarChart, Modifier.weight(1f), onStatistics)
                CompactAction("Settings", Icons.Default.Settings, Modifier.weight(1f), onSettings)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactAction("About", Icons.Default.Info, Modifier.weight(1f), onAbout)
                CompactAction("Support", Icons.Default.Favorite, Modifier.weight(1f), onSupport)
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Open source, ad-free by default", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Core Sudoku gameplay works offline and does not require an account or tracking.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        item {
            Text(
                "Made by the Sanskar",
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null)
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CompactAction(
    label: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
