package by.off.photomap.di

import android.content.Context
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.UserService
import dagger.Component

@Component(modules = [MainScreenModule::class], dependencies = [MainScreenComponent.Dependencies::class])
@PerScreen
interface MainScreenComponent {
    companion object {
        private var instance: MainScreenComponent? = null

        fun get(ctx: Context): MainScreenComponent = instance ?: DaggerMainScreenComponent
            .builder()
            .dependencies((ctx.applicationContext as DependenciesProvider).provideMainScreenDependencies())
            .build().also { instance = it }
    }

    fun inject(mainActivity: MainActivity)

    interface Dependencies {
        fun userService(): UserService
        fun photoService(): PhotoService
    }

    interface DependenciesProvider {
        fun provideMainScreenDependencies(): Dependencies
    }
}