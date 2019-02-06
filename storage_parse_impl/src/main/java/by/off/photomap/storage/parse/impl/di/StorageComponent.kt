package by.off.photomap.storage.parse.impl.di

import by.off.photomap.core.utils.di.ToolsProvider
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.di.StorageApi
import dagger.Component

@Component(modules = [StorageModule::class], dependencies = [ToolsProvider::class])
@PerFeature
abstract class StorageComponent : StorageApi {
    companion object {
        fun get(toolsProvider: ToolsProvider): StorageComponent =
            DaggerStorageComponent.builder()
                .toolsProvider(toolsProvider)
                .build()
    }
}