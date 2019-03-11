package by.off.photomap.presentation.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MainScreenViewModelTest {

    private lateinit var viewModel: MainScreenViewModel

    @Mock
    lateinit var photoService: PhotoService

    @Mock
    lateinit var userService: UserService

    private val testLiveData = MutableLiveData<Response<UserInfo>>()

    @Before
    fun prepareModel() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(userService.logoutLiveData).thenReturn(testLiveData)
        viewModel = MainScreenViewModel(userService, photoService)
        viewModel.liveData.observe(StubLifecycleOwner(), Observer { })
    }

    @Test
    fun test_locationButtonStatus() {
        viewModel.setLocationButtonStatus(true)
        assertTrue("Location button status expected to be 'true'", viewModel.btnLocationStatus.get())
        viewModel.setLocationButtonStatus(false)
        assertFalse("Location button status expected to be 'false'", viewModel.btnLocationStatus.get())
    }
}