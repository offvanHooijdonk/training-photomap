package by.off.photomap.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.core.utils.di.ViewModelKey
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.MainScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@PerScreen
abstract class MainScreenModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @PerScreen
    @ViewModelKey(MainScreenViewModel::class)
    internal abstract fun bindMainScreenViewModel(mainScreenViewModel: MainScreenViewModel): ViewModel
}