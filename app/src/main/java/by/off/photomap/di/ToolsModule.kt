package by.off.photomap.di

import android.content.Context
import by.off.photomap.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ToolsModule {
    @Provides
    @Singleton
    fun provideContext(): Context = App.getAppContext()
}