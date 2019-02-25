package off.photomap.dao.room.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "history", primaryKeys = ["entryText", "type"])
data class HistoryBean(var entryText: String, var type: Int, var timeStamp: Long) {
    companion object {
        const val TYPE_HISTORY = 1
        const val TYPE_TAG = 2
    }
}