/*
package ci.nsu.mobile.main.database
*/

//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface JokeDao {
//
//    // Получить список всех анекдотов (сначала непрочитанные, потом прочитанные)
//    @Query("SELECT * FROM jokes ORDER BY isViewed ASC, dateAdded DESC")
//    fun getAllJokes(): Flow<List<Joke>>
//
//    // Получить анекдот по ID
//    @Query("SELECT * FROM jokes WHERE id = :id")
//    suspend fun getJokeById(id: Long): Joke?
//
//    // Получить случайный анекдот
//    @Query("SELECT * FROM jokes ORDER BY RANDOM() LIMIT 1")
//    suspend fun getRandomJoke(): Joke?
//
//    // Обновить статус "просмотрено"
//    @Query("UPDATE jokes SET isViewed = :isViewed WHERE id = :id")
//    suspend fun updateViewed(id: Long, isViewed: Boolean)
//
//    // Сбросить статус "просмотрено" у всех (когда анекдоты закончились)
//    @Query("UPDATE jokes SET isViewed = 0")
//    suspend fun resetAllViewed()
//
//    // Добавить анекдот
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(joke: Joke): Long
//
//    // Обновить текст анекдота
//    @Update
//    suspend fun update(joke: Joke)
//
//    // Удалить один анекдот
//    @Delete
//    suspend fun delete(joke: Joke)
//
//    // Удалить несколько анекдотов по списку ID
//    @Query("DELETE FROM jokes WHERE id IN (:ids)")
//    suspend fun deleteByIds(ids: List<Long>)
//}