package off.photomap.dao.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import off.photomap.dao.room.entities.HistoryBean
import off.photomap.dao.room.entities.HistoryDao

@Database(entities = [HistoryBean::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

private const val DB_NAME = "photo-map-db-0.2"
fun buildDatabase(ctx: Context) = Room.databaseBuilder(ctx, AppDB::class.java, DB_NAME).build()

internal const val LIMIT_NONE = -1