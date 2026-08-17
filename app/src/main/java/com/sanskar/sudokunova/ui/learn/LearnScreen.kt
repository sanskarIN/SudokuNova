package com.sanskar.sudokunova.ui.learn

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Lesson(
    val title: String,
    val body: String,
)

private val lessons = listOf(
    Lesson(
        "What is Sudoku?",
        "Fill a 9×9 grid so every row, every column, and every 3×3 box contains the digits 1 through 9 exactly once. The starting clues never change.",
    ),
    Lesson(
        "Candidates and pencil marks",
        "For an empty cell, remove digits already present in its row, column, and box. The values left are its candidates. Notes help you track them without committing to a final answer.",
    ),
    Lesson(
        "Naked Single",
        "When an empty cell has only one possible candidate, that candidate must be the answer for the cell. Scan constrained rows, columns, and boxes first.",
    ),
    Lesson(
        "Hidden Single",
        "A digit can sometimes fit in only one cell of a row, column, or box even when that cell has several candidates. That unique location fixes the digit.",
    ),
    Lesson(
        "Naked Pair",
        "If exactly two cells in one unit contain the same two candidates, those digits are reserved for the pair. Remove the pair's digits from the other cells in that unit.",
    ),
    Lesson(
        "Pointing Pair / Triple",
        "If all candidates for a digit inside one 3×3 box lie in a single row or column, that digit cannot appear elsewhere in the same row or column outside the box.",
    ),
    Lesson(
        "Box-Line Reduction",
        "When every candidate for a digit in a row or column lies inside one box, remove that digit from the other cells of that box.",
    ),
    Lesson(
        "X-Wing",
        "If a digit appears in exactly two candidate positions in each of two rows and the positions share the same two columns, those four cells form an X-Wing. The digit can be removed from other cells in those columns.",
    ),
    Lesson(
        "Solving habits",
        "Work from certain logic, keep notes tidy, rescan units after every placement, and avoid guessing when a logical move is available. Accuracy usually improves speed over time.",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learn Sudoku") },
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
        ) {
            item {
                Text(
                    "Build technique step by step",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
            items(lessons) { lesson ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            lesson.body,
                            modifier = Modifier.padding(top = 6.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
