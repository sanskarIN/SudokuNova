package in.sanskar.sudokunova.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import in.sanskar.sudokunova.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About SudokuNova") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        ) {
            item {
                Text("SudokuNova", style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 16.dp))
                Text("Think. Solve. Master the Grid.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Version ${BuildConfig.VERSION_NAME}", modifier = Modifier.padding(top = 8.dp))
                Text("Made by the Sanskar", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Open source", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "SudokuNova is released under the MIT License. Core gameplay is offline-first, requires no account, and includes no analytics by default.",
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        OutlinedButton(
                            onClick = { uriHandler.openUri("https://github.com/sanskarIN/SudokuNova") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) { Text("Open GitHub repository") }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Support SudokuNova", style = MaterialTheme.typography.titleLarge)
                        Text("If SudokuNova helps you, you can support continued open-source development through Buy Me a Coffee.")
                        Button(
                            onClick = { uriHandler.openUri("https://buymeacoffee.com/sanskarIN") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) { Text("☕ Buy Me a Coffee") }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Contact", style = MaterialTheme.typography.titleLarge)
                        Text("Business: sanskarin@outlook.in")
                        Text("Business: sanskarin.business@gmail.com")
                        Text("Support: supportramsandesh@gmail.com")
                        OutlinedButton(
                            onClick = { uriHandler.openUri("mailto:supportramsandesh@gmail.com?subject=SudokuNova%20Support") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) { Text("Email support") }
                    }
                }
            }

            item {
                Text(
                    "Privacy: SudokuNova stores settings, active game state, and gameplay statistics locally using Android DataStore. You can reset statistics from Settings. No gameplay data is transmitted by the base application.",
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
