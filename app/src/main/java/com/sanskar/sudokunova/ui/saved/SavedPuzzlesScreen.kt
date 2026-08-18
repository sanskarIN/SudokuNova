package com.sanskar.sudokunova.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.history.SavedPuzzleEntity
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.ui.common.localizedDifficultyLabel
import com.sanskar.sudokunova.ui.transfer.PuzzleShareActions

@Composable
fun SavedPuzzlesRoute(
    onBack: () -> Unit,
    onPlay: (String, Difficulty) -> Unit,
    viewModel: SavedPuzzlesViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SavedPuzzlesScreen(
        state = state,
        onBack = onBack,
        onFavoritesOnly = viewModel::setFavoritesOnly,
        onFavorite = viewModel::toggleFavorite,
        onDelete = viewModel::delete,
        onPlay = onPlay,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedPuzzlesScreen(
    state: SavedPuzzlesUiState,
    onBack: () -> Unit,
    onFavoritesOnly: (Boolean) -> Unit,
    onFavorite: (SavedPuzzleEntity) -> Unit,
    onDelete: (SavedPuzzleEntity) -> Unit,
    onPlay: (String, Difficulty) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v05_saved_puzzles)) },
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
                        selected = !state.favoritesOnly,
                        onClick = { onFavoritesOnly(false) },
                        label = { Text(stringResource(R.string.v05_all)) },
                    )
                    FilterChip(
                        selected = state.favoritesOnly,
                        onClick = { onFavoritesOnly(true) },
                        label = { Text(stringResource(R.string.v05_favorites)) },
                    )
                }
            }

            if (state.items.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp)) {
                            Text(
                                if (state.favoritesOnly) {
                                    stringResource(R.string.v05_no_favorites)
                                } else {
                                    stringResource(R.string.v05_no_saved_puzzles)
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                stringResource(R.string.v05_saved_hint),
                                modifier = Modifier.padding(top = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            } else {
                items(state.items, key = { "saved-${it.id}" }) { item ->
                    SavedPuzzleCard(
                        item = item,
                        onPlay = {
                            val difficulty = runCatching { Difficulty.valueOf(item.difficulty) }
                                .getOrDefault(Difficulty.MEDIUM)
                            onPlay(item.puzzle, difficulty)
                        },
                        onFavorite = { onFavorite(item) },
                        onDelete = { onDelete(item) },
                    )
                }
            }
            item { Spacer(modifier = Modifier.padding(bottom = 16.dp)) }
        }
    }
}

@Composable
private fun SavedPuzzleCard(
    item: SavedPuzzleEntity,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
) {
    val difficulty = runCatching { Difficulty.valueOf(item.difficulty) }.getOrNull()
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        item.title ?: stringResource(R.string.v05_saved_puzzles),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        difficulty?.let { localizedDifficultyLabel(it) } ?: item.difficulty,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(
                            if (item.isFavorite) R.string.v05_unfavorite else R.string.v05_favorite,
                        ),
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.v05_delete_saved),
                    )
                }
            }
            Button(
                onClick = onPlay,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text(stringResource(R.string.v05_play_saved))
            }
            if (difficulty != null) {
                PuzzleShareActions(
                    puzzle = item.puzzle,
                    difficulty = difficulty,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
