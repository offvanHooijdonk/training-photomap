package off.photomap.dao.di

import by.off.photomap.core.utils.di.ToolsProvider
import by.off.photomap.core.utils.di.scopes.PerFeature
import dagger.Component

@Component(modules = [DaoModule::class], dependencies = [ToolsProvider::class])
@PerFeature
abstract class DaoComponent : DaoProvider {
    companion object {
        fun get(toolsProvider: ToolsProvider): DaoComponent = DaggerDaoComponent.builder().toolsProvider(toolsProvider).build()
    }
}