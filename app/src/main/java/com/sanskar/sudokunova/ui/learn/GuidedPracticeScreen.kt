package com.sanskar.sudokunova.ui.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.ui.common.localizedTeachingExplanation
import com.sanskar.sudokunova.ui.common.localizedTeachingTechnique
import com.sanskar.sudokunova.ui.game.SudokuBoardView

@Composable
fun GuidedPracticeRoute(
    onBack: () -> Unit,
    viewModel: GuidedPracticeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    GuidedPracticeScreen(
        uiState = uiState,
        settings = settings,
        onBack = onBack,
        onCellSelected = viewModel::selectCell,
        onValueSelected = viewModel::submitValue,
        onNextPractice = viewModel::nextLesson,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuidedPracticeScreen(
    uiState: GuidedPracticeUiState,
    settings: com.sanskar.sudokunova.data.UserSettings,
    onBack: () -> Unit,
    onCellSelected: (Int) -> Unit,
    onValueSelected: (Int) -> Unit,
    onNextPractice: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v08_guided_practice)) },
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
        when (uiState) {
            GuidedPracticeUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Text(
                    stringResource(R.string.v08_practice_loading),
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            is GuidedPracticeUiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(R.string.v08_practice_generation_failed))
            }

            is GuidedPracticeUiState.Ready -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        stringResource(R.string.v08_guided_practice_desc),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item {
                    SudokuBoardView(
                        game = uiState.game,
                        settings = settings,
                        onCellSelected = onCellSelected,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                stringResource(R.string.v08_practice_reasoning),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            uiState.sequence.steps.forEach { step ->
                                Text(
                                    localizedTeachingTechnique(step.technique),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    localizedTeachingExplanation(step),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        when (uiState.lastAnswerCorrect) {
                            true -> stringResource(R.string.v08_practice_correct)
                            false -> stringResource(R.string.v08_practice_incorrect)
                            null -> stringResource(R.string.v08_practice_choose_value)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = when (uiState.lastAnswerCorrect) {
                            false -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
                if (!uiState.completed) {
                    item {
                        NumberPracticePad(
                            onValueSelected = onValueSelected,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                } else {
                    item {
                        Button(
                            onClick = onNextPractice,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        ) {
                            Text(stringResource(R.string.v08_next_practice))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPracticePad(
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(3) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(3) { column ->
                    val value = row * 3 + column + 1
                    OutlinedButton(
                        onClick = { onValueSelected(value) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(value.toString())
                    }
                }
            }
        }
    }
}
