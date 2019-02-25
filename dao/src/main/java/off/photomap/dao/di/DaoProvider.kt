package off.photomap.dao.di

import off.photomap.dao.room.entities.HistoryDao

interface DaoProvider {
    fun historyDao(): HistoryDao
}