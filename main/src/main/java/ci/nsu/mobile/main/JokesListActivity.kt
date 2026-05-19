package ci.nsu.mobile.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.runBlocking

class JokesListActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JokesListScreen(db)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun JokesListScreen(db: AppDatabase) {
        val jokes by db.jokeDao().getAllJokes().collectAsStateWithLifecycle(initialValue = emptyList())
        var editMode by remember { mutableStateOf(false) }
        var selected by remember { mutableStateOf(setOf<Long>()) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Анекдоты") },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) { Text("←") }
                    },
                    actions = {
                        TextButton(onClick = {
                            editMode = !editMode
                            selected = emptySet()
                        }) {
                            Text(if (editMode) "OK" else "Edit")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    startActivity(Intent(this@JokesListActivity, EditJokeActivity::class.java))
                }) { Text("+") }
            }
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(jokes, key = { it.id }) { joke ->
                    Card(
                        modifier = Modifier
                            .height(120.dp)
                            .clickable {
                                if (editMode) {
                                    selected = if (joke.id in selected) selected - joke.id else selected + joke.id
                                } else {
                                    val intent = Intent(this@JokesListActivity, EditJokeActivity::class.java)
                                    intent.putExtra("JOKE_ID", joke.id)
                                    intent.putExtra("JOKE_TEXT", joke.text)
                                    startActivity(intent)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (joke.isViewed) Color.LightGray else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            Text(
                                text = joke.text,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (editMode || joke.isViewed) {
                                Checkbox(
                                    checked = if (editMode) joke.id in selected else joke.isViewed,
                                    onCheckedChange = null,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить?") },
                confirmButton = {
                    TextButton(onClick = {
                        runBlocking { db.jokeDao().deleteByIds(selected.toList()) }
                        showDeleteDialog = false
                        selected = emptySet()
                        editMode = false
                    }) { Text("Да") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Нет") }
                }
            )
        }
    }
}