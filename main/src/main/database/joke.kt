package ci.nsu.mobile.main.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jokes")
data class Joke(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isViewed: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)