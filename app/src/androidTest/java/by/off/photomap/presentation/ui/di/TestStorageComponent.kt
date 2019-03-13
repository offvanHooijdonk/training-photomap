package by.off.photomap.presentation.ui.di

import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.di.StorageApi
import dagger.Component

@Component(modules = [TestStorageModule::class])
@PerFeature
interface TestStorageComponent : StorageApi {
    companion object {
        var instance: TestStorageComponent? = null
        fun get(): TestStorageComponent = instance ?: DaggerTestStorageComponent.builder().build().also { instance = it }
    }
}