package by.off.photomap.presentation.ui.di

import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.SearchTagService
import by.off.photomap.storage.parse.UserService
import dagger.Module
import dagger.Provides
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@Module
@PerFeature
class TestStorageModule {
    @Provides
    fun userService(): UserService = ServiceMocks.userServiceMock

    @Provides
    fun photoService(): PhotoService = ServiceMocks.photoServiceMock

    @Provides
    fun geoPointService(): GeoPointService = ServiceMocks.geoPointServiceMock

    @Provides
    fun tagService(): SearchTagService = ServiceMocks.tagServiceMock
}