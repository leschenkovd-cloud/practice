package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditJokeActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)

        val jokeId = intent.getLongExtra("JOKE_ID", -1)
        val jokeText = intent.getStringExtra("JOKE_TEXT") ?: ""

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EditScreen(db, jokeId, jokeText)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditScreen(db: AppDatabase, jokeId: Long, initialText: String) {
        var text by remember { mutableStateOf(initialText) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (jokeId == -1L) "Добавить" else "Edit") },
                    navigationIcon = { IconButton(onClick = { finish() }) { Text("←") } },
                    actions = {
                        TextButton(onClick = {
                            if (text.isNotBlank()) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    if (jokeId == -1L) {
                                        db.jokeDao().insert(Joke(text = text))
                                    } else {
                                        db.jokeDao().update(Joke(jokeId, text, true, System.currentTimeMillis()))
                                    }
                                }
                                finish()
                            }
                        }) { Text("Save") }
                    }
                )
            }
        ) { padding ->
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                label = { Text("Текст") },
                maxLines = 10
            )
        }
    }
}