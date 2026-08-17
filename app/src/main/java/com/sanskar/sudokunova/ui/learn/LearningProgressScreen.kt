package com.sanskar.sudokunova.ui.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanskar.sudokunova.R
import com.sanskar.sudokunova.data.learning.LearningProgressSnapshot
import com.sanskar.sudokunova.data.learning.TechniqueLearningProgress
import com.sanskar.sudokunova.engine.LogicalTechnique
import com.sanskar.sudokunova.ui.common.localizedTeachingTechnique

@Composable
fun LearningProgressRoute(
    onBack: () -> Unit,
    viewModel: LearningProgressViewModel = viewModel(),
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    LearningProgressScreen(
        progress = progress,
        onBack = onBack,
        onReset = viewModel::reset,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningProgressScreen(
    progress: LearningProgressSnapshot,
    onBack: () -> Unit,
    onReset: () -> Unit,
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v08_learning_progress)) },
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
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            stringResource(R.string.v08_learning_progress_desc),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            stringResource(
                                R.string.v08_total_progress,
                                progress.totalHintViews,
                                progress.totalPracticeAttempts,
                                progress.totalCompletedSteps,
                            ),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            LogicalTechnique.entries.forEach { technique ->
                item(key = technique.name) {
                    TechniqueProgressCard(progress[technique])
                }
            }

            item {
                TextButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                ) {
                    Text(stringResource(R.string.v08_reset_learning_progress))
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.v08_reset_learning_progress_question)) },
            text = { Text(stringResource(R.string.v08_reset_learning_progress_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onReset()
                        showResetDialog = false
                    },
                ) { Text(stringResource(R.string.v04_reset)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.v04_cancel))
                }
            },
        )
    }
}

@Composable
private fun TechniqueProgressCard(progress: TechniqueLearningProgress) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                localizedTeachingTechnique(progress.technique),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(stringResource(R.string.v08_hint_views, progress.hintViews))
            Text(stringResource(R.string.v08_practice_attempts, progress.practiceAttempts))
            Text(
                progress.accuracyPercent?.let { stringResource(R.string.v08_accuracy, it) }
                    ?: stringResource(R.string.v08_no_accuracy),
            )
            Text(stringResource(R.string.v08_completed_steps, progress.completedSteps))
            Text(stringResource(R.string.v08_completed_sessions, progress.completedSessions))
        }
    }
}
