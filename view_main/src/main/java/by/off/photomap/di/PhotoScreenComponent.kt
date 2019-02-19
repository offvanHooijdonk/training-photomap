package by.off.photomap.di

import android.content.Context
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.ui.map.AddPhotoBottomSheet
import by.off.photomap.presentation.ui.map.MapFragment
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.ui.timeline.TimelineFragment
import by.off.photomap.presentation.ui.timeline.search.SearchTagsDialogFragment
import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.TagService
import dagger.Component

@Component(modules = [PhotoScreenModule::class], dependencies = [PhotoScreenComponent.Dependencies::class])
@PerScreen
interface PhotoScreenComponent {
    companion object {
        private var instance: PhotoScreenComponent? = null

        fun get(ctx: Context): PhotoScreenComponent {
            val component = instance
            return component ?: DaggerPhotoScreenComponent.builder()
                .dependencies((ctx.applicationContext as DependenciesProvider).providePhotoScreenDependencies())
                .build().also { instance = it }
        }
    }

    fun inject(photoViewEditActivity: PhotoViewEditActivity)

    fun inject(timelineFragment: TimelineFragment)

    fun inject(mapFragment: MapFragment)

    fun inject(addPhotoBottomSheet: AddPhotoBottomSheet)

    fun inject(searchTagsDialogFragment: SearchTagsDialogFragment)

    interface Dependencies {
        fun photoService(): PhotoService
        fun geoPointService(): GeoPointService
        fun tagService(): TagService
    }

    interface DependenciesProvider {
        fun providePhotoScreenDependencies(): Dependencies
    }
}