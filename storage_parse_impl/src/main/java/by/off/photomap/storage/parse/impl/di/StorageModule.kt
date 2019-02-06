package by.off.photomap.storage.parse.impl.di

import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.UserService
import by.off.photomap.storage.parse.impl.PhotoServiceImpl
import by.off.photomap.storage.parse.impl.UserServiceImpl
import dagger.Binds
import dagger.Module

@Module
@PerFeature
abstract class StorageModule {
    @Binds
    abstract fun provideUserService(userServiceImpl: UserServiceImpl): UserService

    @Binds
    abstract fun providePhotoService(photoServiceImpl: PhotoServiceImpl): PhotoService
}