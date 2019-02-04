package by.off.photomap.app

import android.app.Application
import android.content.Context
import by.off.photomap.di.AppComponent
import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.di.MainScreenComponent
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.storage.parse.impl.ParseHelper

class App : Application(),
    LoginScreenComponent.DependenciesProvider, PhotoScreenComponent.DependenciesProvider, MainScreenComponent.DependenciesProvider {
    companion object {
        private lateinit var appContext: Context
        internal fun getAppContext() = appContext
    }

    private lateinit var di: AppComponent

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        ParseHelper.initParse(this)
        di = AppComponent.component
    }

    override fun provideLoginScreenDependencies(): LoginScreenComponent.Dependencies = di

    override fun providePhotoScreenDependencies(): PhotoScreenComponent.Dependencies = di

    override fun provideMainScreenDependencies(): MainScreenComponent.Dependencies = di
}
