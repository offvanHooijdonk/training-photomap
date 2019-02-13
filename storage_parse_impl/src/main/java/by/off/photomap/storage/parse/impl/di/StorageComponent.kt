package by.off.photomap.storage.parse.impl.di

import by.off.photomap.core.utils.di.ToolsProvider
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.di.StorageApi
import dagger.Component
import javax.inject.Singleton

@Component(modules = [StorageModule::class], dependencies = [ToolsProvider::class])
@PerFeature
/*@Singleton*/
abstract class StorageComponent : StorageApi {
    companion object {
        private var instance: StorageComponent? = null
        fun get(toolsProvider: ToolsProvider): StorageComponent =
            instance ?: DaggerStorageComponent.builder()
                .toolsProvider(toolsProvider)
                .build().also { instance = it }
    }
}