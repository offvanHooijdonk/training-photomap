package by.off.photomap.presentation.ui.di

import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.storage.parse.di.StorageApi
import dagger.Component
import javax.inject.Singleton

@Component(dependencies = [StorageApi::class])
@Singleton
interface TestAppComponent : LoginScreenComponent.Dependencies {
    companion object {
        val component: TestAppComponent by lazy { create() }
        private fun create(): TestAppComponent = DaggerTestAppComponent.builder().storageApi(
            TestStorageComponent.get()
        ).build()
    }
}