package ci.nsu.mobile.main.database

//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [Joke::class], version = 1, exportSchema = false)
//abstract class AppDatabase : RoomDatabase() {
//
//    // "Пульт управления" базой данных
//    abstract fun jokeDao(): JokeDao
//
//    companion object {
//        @Volatile private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "jokes_database" // Имя файла БД на телефоне
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}