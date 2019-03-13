package by.off.photomap.presentation.ui.di

import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.SearchTagService
import by.off.photomap.storage.parse.UserService
import org.mockito.Mock
import org.mockito.MockitoAnnotations

object ServiceMocks {

    @Mock
    lateinit var userServiceMock: UserService

    @Mock
    lateinit var photoServiceMock: PhotoService

    @Mock
    lateinit var geoPointServiceMock: GeoPointService

    @Mock
    lateinit var tagServiceMock: SearchTagService

    init {
        MockitoAnnotations.initMocks(this)
    }
}