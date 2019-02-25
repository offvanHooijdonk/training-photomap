package by.off.photomap.storage.parse.impl.di

import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.SearchTagService
import by.off.photomap.storage.parse.UserService
import by.off.photomap.storage.parse.impl.GeoPointServiceImpl
import by.off.photomap.storage.parse.impl.PhotoServiceImpl
import by.off.photomap.storage.parse.impl.SearchTagServiceImpl
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

    @Binds
    abstract fun provideGeoPointService(geoPointServiceImpl: GeoPointServiceImpl): GeoPointService

    @Binds
    abstract fun provideTagServiceImpl(tagServiceImpl: SearchTagServiceImpl): SearchTagService
}