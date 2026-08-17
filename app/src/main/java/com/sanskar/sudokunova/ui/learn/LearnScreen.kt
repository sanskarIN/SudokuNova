package com.sanskar.sudokunova.ui.learn

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanskar.sudokunova.R

private data class LessonResource(
    @StringRes val title: Int,
    @StringRes val body: Int,
)

private val lessons = listOf(
    LessonResource(R.string.v04_lesson_what_title, R.string.v04_lesson_what_body),
    LessonResource(R.string.v04_lesson_candidates_title, R.string.v04_lesson_candidates_body),
    LessonResource(R.string.v04_lesson_naked_single_title, R.string.v04_lesson_naked_single_body),
    LessonResource(R.string.v04_lesson_hidden_single_title, R.string.v04_lesson_hidden_single_body),
    LessonResource(R.string.v04_lesson_naked_pair_title, R.string.v04_lesson_naked_pair_body),
    LessonResource(R.string.v04_lesson_pointing_title, R.string.v04_lesson_pointing_body),
    LessonResource(R.string.v04_lesson_box_line_title, R.string.v04_lesson_box_line_body),
    LessonResource(R.string.v04_lesson_xwing_title, R.string.v04_lesson_xwing_body),
    LessonResource(R.string.v04_lesson_habits_title, R.string.v04_lesson_habits_body),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onBack: () -> Unit,
    onLearningProgress: () -> Unit = {},
) {
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        ) {
            item {
                Text(
                    stringResource(R.string.v04_learn_heading),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
            item {
                OutlinedButton(
                    onClick = onLearningProgress,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                ) {
                    Text(stringResource(R.string.v08_open_learning_progress))
                }
            }
            items(lessons) { lesson ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
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
        }
    }
}
