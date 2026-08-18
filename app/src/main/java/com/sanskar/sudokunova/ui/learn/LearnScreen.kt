package com.sanskar.sudokunova.ui.learn

import androidx.annotation.StringRes
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.sanskar.sudokunova.data.LearningProgress
import com.sanskar.sudokunova.engine.LogicalTechnique

private data class LessonResource(
    @StringRes val title: Int,
    @StringRes val body: Int,
)

private data class TechniqueLessonResource(
    val technique: LogicalTechnique,
    @StringRes val title: Int,
    @StringRes val body: Int,
)

private val generalLessons = listOf(
    LessonResource(R.string.v04_lesson_what_title, R.string.v04_lesson_what_body),
    LessonResource(R.string.v04_lesson_candidates_title, R.string.v04_lesson_candidates_body),
    LessonResource(R.string.v04_lesson_habits_title, R.string.v04_lesson_habits_body),
)

private val techniqueLessons = listOf(
    TechniqueLessonResource(
        LogicalTechnique.NAKED_SINGLE,
        R.string.v04_lesson_naked_single_title,
        R.string.v04_lesson_naked_single_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.HIDDEN_SINGLE,
        R.string.v04_lesson_hidden_single_title,
        R.string.v04_lesson_hidden_single_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.NAKED_PAIR,
        R.string.v04_lesson_naked_pair_title,
        R.string.v04_lesson_naked_pair_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.POINTING_PAIR_OR_TRIPLE,
        R.string.v04_lesson_pointing_title,
        R.string.v04_lesson_pointing_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.BOX_LINE_REDUCTION,
        R.string.v04_lesson_box_line_title,
        R.string.v04_lesson_box_line_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.HIDDEN_PAIR,
        R.string.v08_lesson_hidden_pair_title,
        R.string.v08_lesson_hidden_pair_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.NAKED_TRIPLE,
        R.string.v08_lesson_naked_triple_title,
        R.string.v08_lesson_naked_triple_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.HIDDEN_TRIPLE,
        R.string.v08_lesson_hidden_triple_title,
        R.string.v08_lesson_hidden_triple_body,
    ),
    TechniqueLessonResource(
        LogicalTechnique.X_WING,
        R.string.v04_lesson_xwing_title,
        R.string.v08_lesson_x_wing_body,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onBack: () -> Unit,
    viewModel: LearnViewModel = viewModel(),
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val practice by viewModel.practice.collectAsStateWithLifecycle()
    var openLesson by remember { mutableStateOf<TechniqueLessonResource?>(null) }
    var confirmReset by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v04_learn_sudoku)) },
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
                Text(
                    stringResource(R.string.v04_learn_heading),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            item {
                LearningProgressCard(
                    progress = progress,
                    onReset = { confirmReset = true },
                )
            }

            items(generalLessons) { lesson ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(lesson.title), style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(lesson.body),
                            modifier = Modifier.padding(top = 6.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            items(techniqueLessons, key = { it.technique.name }) { lesson ->
                val techniqueProgress = progress.forTechnique(lesson.technique)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(stringResource(lesson.title), style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(R.string.v08_learn_mastery_percent, techniqueProgress.masteryPercent),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        LinearProgressIndicator(
                            progress = { techniqueProgress.masteryPercent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            stringResource(
                                R.string.v08_learn_progress_practice,
                                techniqueProgress.practiceSuccesses,
                                techniqueProgress.practiceAttempts,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    openLesson = lesson
                                    viewModel.recordLessonViewed(lesson.technique)
                                },
                            ) {
                                Text(stringResource(R.string.v08_learn_view_lesson))
                            }
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.startPractice(lesson.technique) },
                            ) {
                                Text(stringResource(R.string.v08_learn_practice))
                            }
                        }
                    }
                }
            }

            item { Text("", modifier = Modifier.padding(bottom = 12.dp)) }
        }
    }

    openLesson?.let { lesson ->
        AlertDialog(
            onDismissRequest = { openLesson = null },
            title = { Text(stringResource(lesson.title)) },
            text = { Text(stringResource(lesson.body)) },
            confirmButton = {
                TextButton(onClick = { openLesson = null }) {
                    Text(stringResource(R.string.v08_learn_practice_close))
                }
            },
        )
    }

    practice?.let { state ->
        val answer = state.answerState
        AlertDialog(
            onDismissRequest = viewModel::closePractice,
            title = { Text(stringResource(R.string.v08_learn_practice_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.v08_learn_practice_prompt))
                    Text(
                        stringResource(
                            R.string.v08_learn_practice_evidence,
                            state.exercise.step.sourceCells.size,
                            state.exercise.step.targetCells.size,
                            state.exercise.step.candidateEliminations.size,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    state.exercise.choices.forEach { choice ->
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = answer == PracticeAnswerState.Unanswered,
                            onClick = { viewModel.answerPractice(choice) },
                        ) {
                            Text(localizedTechniqueName(choice))
                        }
                    }
                    if (answer is PracticeAnswerState.Answered) {
                        Text(
                            if (answer.correct) {
                                stringResource(
                                    R.string.v08_learn_practice_correct,
                                    localizedTechniqueName(state.technique),
                                )
                            } else {
                                stringResource(
                                    R.string.v08_learn_practice_incorrect,
                                    localizedTechniqueName(state.technique),
                                )
                            },
                        )
                    }
                }
            },
            confirmButton = {
                if (answer is PracticeAnswerState.Answered) {
                    TextButton(onClick = viewModel::nextPractice) {
                        Text(stringResource(R.string.v08_learn_practice_next))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closePractice) {
                    Text(stringResource(R.string.v08_learn_practice_close))
                }
            },
        )
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(stringResource(R.string.v08_learn_reset_title)) },
            text = { Text(stringResource(R.string.v08_learn_reset_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmReset = false
                        viewModel.resetLearningProgress()
                    },
                ) {
                    Text(stringResource(R.string.v08_learn_reset_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) {
                    Text(stringResource(R.string.v08_learn_reset_cancel))
                }
            },
        )
    }
}

@Composable
private fun LearningProgressCard(
    progress: LearningProgress,
    onReset: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.v08_learn_progress_title), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.v08_learn_progress_overall, progress.overallMasteryPercent))
            LinearProgressIndicator(
                progress = { progress.overallMasteryPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                stringResource(
                    R.string.v08_learn_progress_mastered,
                    progress.masteredTechniqueCount,
                    LogicalTechnique.entries.size,
                ),
            )
            Text(
                stringResource(
                    R.string.v08_learn_progress_practice,
                    progress.totalPracticeSuccesses,
                    progress.totalPracticeAttempts,
                ),
            )
            TextButton(onClick = onReset) {
                Text(stringResource(R.string.v08_learn_reset_progress))
            }
        }
    }
}

@Composable
private fun localizedTechniqueName(technique: LogicalTechnique): String = stringResource(
    when (technique) {
        LogicalTechnique.NAKED_SINGLE -> R.string.v08_hint_naked_single
        LogicalTechnique.HIDDEN_SINGLE -> R.string.v08_hint_hidden_single
        LogicalTechnique.NAKED_PAIR -> R.string.v08_hint_naked_pair
        LogicalTechnique.POINTING_PAIR_OR_TRIPLE -> R.string.v08_hint_pointing
        LogicalTechnique.BOX_LINE_REDUCTION -> R.string.v08_hint_box_line
        LogicalTechnique.HIDDEN_PAIR -> R.string.v08_hint_hidden_pair
        LogicalTechnique.NAKED_TRIPLE -> R.string.v08_hint_naked_triple
        LogicalTechnique.HIDDEN_TRIPLE -> R.string.v08_hint_hidden_triple
        LogicalTechnique.X_WING -> R.string.v08_hint_x_wing
    },
)
