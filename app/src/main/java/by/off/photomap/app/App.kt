package by.off.photomap.app

import android.app.Application
import by.off.photomap.di.AppComponent
import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.storage.parse.impl.ParseHelper

class App : Application(),
    LoginScreenComponent.DependenciesProvider {
    private lateinit var di: AppComponent

    override fun onCreate() {
        super.onCreate()

        ParseHelper.initParse(this)
        di = AppComponent.component
    }

    override fun provideLoginScreenDependencies(): LoginScreenComponent.Dependencies = di
}
