package off.photomap.dao.di

import android.content.Context
import by.off.photomap.core.utils.di.scopes.PerFeature
import dagger.Module
import dagger.Provides
import off.photomap.dao.room.AppDB
import off.photomap.dao.room.buildDatabase
import off.photomap.dao.room.entities.HistoryDao

@Module
class DaoModule {
    @Provides
    @PerFeature
    fun provideAddDatabase(ctx: Context) = buildDatabase(ctx)

    @Provides
    @PerFeature
    fun provideHistoryDao(appDB: AppDB): HistoryDao = appDB.historyDao()
}