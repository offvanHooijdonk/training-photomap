package by.off.photomap.presentation.ui

import android.app.Application
import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.presentation.ui.di.TestAppComponent

class TestApp : Application(),
    LoginScreenComponent.DependenciesProvider {

    private lateinit var di: TestAppComponent

    override fun onCreate() {
        super.onCreate()

        di = TestAppComponent.component
    }

    override fun provideLoginScreenDependencies(): LoginScreenComponent.Dependencies = di

}