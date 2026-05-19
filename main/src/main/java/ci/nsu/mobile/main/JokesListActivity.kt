package ci.nsu.mobile.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        // Подписываемся на изменения в БД
        val jokes by db.jokeDao().getAllJokes().collectAsState(initial = emptyList())

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
                        if (editMode) {
                            // Кнопка корзины (появляется только если есть выбранные)
                            if (selected.isNotEmpty()) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                                }
                            }
                            TextButton(onClick = {
                                editMode = false
                                selected = emptySet()
                            }) { Text("Готово") }
                        } else {
                            TextButton(onClick = { editMode = true }) { Text("Редактировать") }
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
                                    // Переход к редактированию
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
                            if (editMode) {
                                Checkbox(
                                    checked = joke.id in selected,
                                    onCheckedChange = null,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Диалог удаления
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить выбранные?") },
                text = { Text("Будет удалено: ${selected.size} анекдотов") },
                confirmButton = {
                    TextButton(onClick = {
                        // 🛠 ИСПРАВЛЕНИЕ: Ждём удаления, потом обновляем UI
                        lifecycleScope.launch {
                            // 1. Удаляем в фоновом потоке
                            withContext(Dispatchers.IO) {
                                val idsToDelete = selected.toList()
                                db.jokeDao().deleteByIds(idsToDelete)
                            }
                            // 2. Только после успешного удаления закрываем диалог и сбрасываем выбор
                            showDeleteDialog = false
                            selected = emptySet()
                            editMode = false
                        }
                    }) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
                }
            )
        }
    }
}