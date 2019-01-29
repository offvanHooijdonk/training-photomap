package by.off.photomap.di

import android.content.Context
import by.off.photomap.core.utils.di.scopes.PerScreen
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.storage.parse.PhotoService
import dagger.Component

@Component(modules = [PhotoScreenModule::class], dependencies = [PhotoScreenComponent.Dependencies::class])
@PerScreen
interface PhotoScreenComponent {
    companion object {
        fun get(ctx: Context): PhotoScreenComponent =
            DaggerPhotoScreenComponent.builder()
                .dependencies((ctx.applicationContext as DependenciesProvider).providePhotoScreenDependencies())
                .build()
    }

    fun inject(photoViewEditActivity: PhotoViewEditActivity)

    interface Dependencies {
        fun photoService(): PhotoService
    }

    interface DependenciesProvider {
        fun providePhotoScreenDependencies(): Dependencies
    }
}