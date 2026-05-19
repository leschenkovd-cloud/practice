package ci.nsu.mobile.main

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ================= ENTITY =================
@Entity(tableName = "jokes")
data class Joke(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isViewed: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

// ================= DAO =================
@Dao
interface JokeDao {
    @Query("SELECT * FROM jokes ORDER BY isViewed ASC, dateAdded ASC")
    fun getAllJokes(): Flow<List<Joke>>

    @Query("SELECT * FROM jokes WHERE isViewed = 0 ORDER BY dateAdded ASC LIMIT 1")
    suspend fun getUnviewedJoke(): Joke?

    @Query("SELECT * FROM jokes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomJoke(): Joke?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(joke: Joke)

    @Update
    suspend fun update(joke: Joke)

    @Delete
    suspend fun delete(joke: Joke)

    @Query("DELETE FROM jokes WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("UPDATE jokes SET isViewed = 0")
    suspend fun resetViewed()

    @Query("SELECT COUNT(*) FROM jokes")
    suspend fun count(): Int
}

// ================= DATABASE =================
@Database(entities = [Joke::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jokeDao(): JokeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jokes_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}