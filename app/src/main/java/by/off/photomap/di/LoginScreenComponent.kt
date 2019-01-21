package by.off.photomap.di

import android.content.Context
import by.off.photomap.app.App
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.login.LoginViewModel
import by.off.photomap.presentation.ui.login.SplashActivity
import by.off.photomap.storage.parse.UserService
import dagger.Component

@Component(modules = [LoginScreenModule::class], dependencies = [LoginScreenComponent.Dependencies::class])
@PerScreen
interface LoginScreenComponent {
    companion object {
        fun get(ctx: Context): LoginScreenComponent =
            DaggerLoginScreenComponent.builder()
                .dependencies((ctx.applicationContext as App).provideLoginScreenDependencies())
                .build()
    }

    fun inject(splashActivity: SplashActivity)

    interface Dependencies {
        fun userStorage(): UserService
    }

    interface DependenciesProvider {
        fun provideLoginScreenDependencies(): Dependencies
    }
}