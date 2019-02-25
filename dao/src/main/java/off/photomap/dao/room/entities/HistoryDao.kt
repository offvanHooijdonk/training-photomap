package off.photomap.dao.room.entities

import android.arch.persistence.room.*
import off.photomap.dao.room.LIMIT_NONE

@Dao
interface HistoryDao {

    @Query("select * from history where entryText like '%'||:text||'%' order by type,timeStamp desc,  entryText limit :limit")
    fun find(text: String, limit: Int = LIMIT_NONE) : List<HistoryBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(tagBean: HistoryBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(list: List<HistoryBean>)

    @Delete
    fun deleteHistory(history: HistoryBean)
}