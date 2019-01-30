package by.off.photomap.di

import android.content.Context
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.ui.timeline.TimelineFragment
import by.off.photomap.storage.parse.PhotoService
import dagger.Component

@Component(modules = [PhotoScreenModule::class], dependencies = [PhotoScreenComponent.Dependencies::class])
@PerScreen
interface PhotoScreenComponent {
    companion object {
        private var instance: PhotoScreenComponent? = null

        fun get(ctx: Context): PhotoScreenComponent {
            val component = instance
            return if (component == null) {
                DaggerPhotoScreenComponent.builder()
                    .dependencies((ctx.applicationContext as DependenciesProvider).providePhotoScreenDependencies())
                    .build().also { instance = it }
            } else {
                component
            }
        }
    }

    fun inject(photoViewEditActivity: PhotoViewEditActivity)

    fun inject(timelineFragment: TimelineFragment)

    interface Dependencies {
        fun photoService(): PhotoService
    }

    interface DependenciesProvider {
        fun providePhotoScreenDependencies(): Dependencies
    }
}