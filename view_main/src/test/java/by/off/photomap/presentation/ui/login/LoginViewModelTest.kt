package by.off.photomap.presentation.ui.login

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.StubLifecycleOwner
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var userService: UserService

    private val emptyUser = UserInfo(id = "")
    private lateinit var viewModel: LoginViewModel
    private val testLiveData = MutableLiveData<Response<UserInfo>>()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        `when`(userService.serviceLiveData).thenReturn(testLiveData)

        viewModel = LoginViewModel(userService)
        viewModel.liveData.observe(StubLifecycleOwner(), Observer {})
    }

    @Test
    fun test_loginProcess() {
        viewModel.logIn()
        assertProgressStatuses()
    }

    @Test
    fun test_authenticateProcess() {
        viewModel.authenticate("", "")
        assertProgressStatuses()
    }

    @Test
    fun test_registerProcess() {
        viewModel.registerAndLogin(emptyUser, "")
        assertProgressStatuses()
    }

    @Test
    fun test_authError() {
        testLiveData.postValue(Response(error = Exception("sample")))
        assertTrue("Is Error must be 'true' on error", viewModel.isError.get())
        assertNotNull("Error value must be not null on error", viewModel.errorObject.get())
        assertTrue("Show Login Buttons must be 'true' on error", viewModel.showLoginButtons.get())
        assertFalse("In Progress must be 'false' on error", viewModel.isInProgress.get())
    }

    @Test
    fun test_authSuccess() {
        testLiveData.postValue(Response(data = emptyUser))
        assertFalse("Is Error must be 'false' on success", viewModel.isError.get())
        assertNull("Error value must be null on success", viewModel.errorObject.get())
        assertFalse("Show Login Buttons must be 'false' on success", viewModel.showLoginButtons.get())
        assertFalse("In Progress must be 'false' on success", viewModel.isInProgress.get())
    }

    @Test
    fun test_authFail() {
        testLiveData.postValue(Response())
        assertFalse("Is Error must be 'false' on login fail", viewModel.isError.get())
        assertNull("Error value must be null on login fail", viewModel.errorObject.get())
        assertTrue("Show Login Buttons must be 'false' on login fail", viewModel.showLoginButtons.get())
        assertFalse("In Progress must be 'false' on login fail", viewModel.isInProgress.get())
    }

    private fun assertProgressStatuses() {
        assertTrue("InProgress must be 'true' while some auth is in progress.", viewModel.isInProgress.get())
        assertFalse("Show Login Button must be 'false' while some auth is in progress.", viewModel.showLoginButtons.get())
        assertFalse("Is Error must be 'false' while some auth is in progress.", viewModel.isError.get())
    }
}