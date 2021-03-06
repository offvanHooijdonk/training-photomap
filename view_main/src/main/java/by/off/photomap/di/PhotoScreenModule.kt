package by.off.photomap.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.core.utils.di.ViewModelKey
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.map.MapViewModel
import by.off.photomap.presentation.ui.photo.PhotoViewModel
import by.off.photomap.presentation.ui.timeline.TimelineViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PhotoScreenModule {
    @Binds
    @PerScreen
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(PhotoViewModel::class)
    internal abstract fun bindPhotoViewModel(photoViewModel: PhotoViewModel): ViewModel

    @Binds
    @IntoMap
    @PerScreen
    @ViewModelKey(TimelineViewModel::class)
    internal abstract fun bindTimelineViewModel(timelineViewModel: TimelineViewModel): ViewModel

    @Binds
    @IntoMap
    @PerScreen
    @ViewModelKey(MapViewModel::class)
    internal abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel
}