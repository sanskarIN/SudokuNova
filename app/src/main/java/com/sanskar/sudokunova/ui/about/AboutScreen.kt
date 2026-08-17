package com.sanskar.sudokunova.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.sanskar.sudokunova.BuildConfig
import com.sanskar.sudokunova.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.v04_about_sudokunova)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.v04_back))
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        ) {
            item {
                Text(stringResource(R.string.v04_app_name), style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 16.dp))
                Text(stringResource(R.string.v04_tagline), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(stringResource(R.string.v04_version_format, BuildConfig.VERSION_NAME), modifier = Modifier.padding(top = 8.dp))
                Text(stringResource(R.string.v04_made_by), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.v04_open_source), style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(R.string.v04_open_source_desc),
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        OutlinedButton(
                            onClick = { uriHandler.openUri("https://github.com/sanskarIN/SudokuNova") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) { Text(stringResource(R.string.v04_open_github)) }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.v04_support_sudokunova), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.v04_support_desc))
                        Button(
                            onClick = { uriHandler.openUri("https://buymeacoffee.com/sanskarIN") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) { Text("☕ ${stringResource(R.string.v04_buy_me_a_coffee)}") }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.v04_contact), style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.v04_business_email_one))
                        Text(stringResource(R.string.v04_business_email_two))
                        Text(stringResource(R.string.v04_support_email))
                        OutlinedButton(
                            onClick = { uriHandler.openUri("mailto:supportramsandesh@gmail.com?subject=SudokuNova%20Support") },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) { Text(stringResource(R.string.v04_email_support)) }
                    }
                }
            }

            item {
                Text(
                    stringResource(R.string.v04_privacy_summary),
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
