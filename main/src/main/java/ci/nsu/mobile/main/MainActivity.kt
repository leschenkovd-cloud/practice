package ci.nsu.mobile.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(db)
                }
            }
        }
    }

    @Composable
    fun MainScreen(db: AppDatabase) {
        var jokeText by remember { mutableStateOf("Загрузка...") }

        LaunchedEffect(Unit) {
            // Все операции с БД выносим в фоновый поток
            withContext(Dispatchers.IO) {
                val dao = db.jokeDao()
                if (dao.count() == 0) {
                    loadJokesFromFile(dao, this@MainActivity)
                }

                var joke = dao.getUnviewedJoke()
                if (joke == null) {
                    dao.resetViewed()
                    joke = dao.getUnviewedJoke()
                }
                if (joke == null) joke = dao.getRandomJoke()

                joke?.let {
                    dao.update(it.copy(isViewed = true))
                    jokeText = it.text
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Анекдот дня", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(jokeText, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                startActivity(Intent(this@MainActivity, JokesListActivity::class.java))
            }) {
                Text("Редактировать")
            }
        }
    }

    // 📥 Метод загрузки из файла
    private suspend fun loadJokesFromFile(dao: JokeDao, context: Context) {
        val inputStream = context.resources.openRawResource(R.raw.jokes)
        val content = inputStream.bufferedReader().use { it.readText() }

        val jokes = content.split("---")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        jokes.forEach { dao.insert(Joke(text = it)) }
    }
}