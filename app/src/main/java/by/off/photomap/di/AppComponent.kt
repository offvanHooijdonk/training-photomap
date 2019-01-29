package by.off.photomap.di

import by.off.photomap.storage.parse.di.StorageApi
import by.off.photomap.storage.parse.impl.di.StorageComponent
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class], dependencies = [StorageApi::class])
@Singleton
abstract class AppComponent : LoginScreenComponent.Dependencies, PhotoScreenComponent.Dependencies {
    companion object {
        val component: AppComponent by lazy { create() }
        private fun create(): AppComponent = DaggerAppComponent.builder().storageApi(
            StorageComponent.get(ToolsComponent.get())
        ).build()
    }
}