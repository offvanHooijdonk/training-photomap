package by.off.photomap.storage.parse.impl.di

import by.off.photomap.core.utils.di.ToolsProvider
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.di.StorageApi
import dagger.Component
import off.photomap.dao.di.DaoComponent
import off.photomap.dao.di.DaoProvider

@Component(modules = [StorageModule::class], dependencies = [ToolsProvider::class, DaoProvider::class])
@PerFeature
abstract class StorageComponent : StorageApi {
    companion object {
        private var instance: StorageComponent? = null
        fun get(toolsProvider: ToolsProvider): StorageComponent =
            instance ?: DaggerStorageComponent.builder()
                .toolsProvider(toolsProvider)
                .daoProvider(DaoComponent.get(toolsProvider))
                .build().also { instance = it }
    }
}