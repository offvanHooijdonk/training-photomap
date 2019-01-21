package by.off.photomap.storage.parse.impl.di

import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.CategoryService
import by.off.photomap.storage.parse.UserService
import by.off.photomap.storage.parse.impl.CategoryServiceImpl
import by.off.photomap.storage.parse.impl.UserServiceImpl
import dagger.Binds
import dagger.Module

@Module
@PerFeature
abstract class StorageModule {
    @Binds
    abstract fun provideCategoryService(categoryServiceImpl: CategoryServiceImpl): CategoryService

    @Binds
    abstract fun provideUserService(userServiceImpl: UserServiceImpl): UserService
}