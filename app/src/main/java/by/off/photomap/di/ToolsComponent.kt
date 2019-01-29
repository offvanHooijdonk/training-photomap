package by.off.photomap.di

import by.off.photomap.core.utils.di.ToolsProvider
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ToolsModule::class])
@Singleton
interface ToolsComponent : ToolsProvider {
    companion object {
        fun get(): ToolsComponent = DaggerToolsComponent.builder().build()
    }
}