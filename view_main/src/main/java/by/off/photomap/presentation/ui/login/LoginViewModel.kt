package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.core.utils.session.Session
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userService: UserService) : ViewModel() {
    var liveData = userService.serviceLiveData.map { response -> onUserResponse(response) }
    var isInProgress = ObservableBoolean(false)
    var isError = ObservableBoolean(false)
    var errorObject = ObservableField<Exception?>()
    val showLoginButtons = ObservableBoolean(false)

    fun logIn() {
        Log.i(LOGCAT, "Log In Started")
        progressStart()
        userService.logIn()
    }

    fun authenticate(userName: String, pwd: String) {
        progressStart()
        userService.authenticate(userName, pwd)
    }

    fun registerAndLogin(userInfo: UserInfo, pwd: String) {
        progressStart()
        userService.registerAndLogin(userInfo, pwd)
    }

    private fun onUserResponse(response: Response<UserInfo>?): Response<UserInfo>? {
        Log.i(LOGCAT, "Response arrived $response")
        response?.let {
            isError.set(response.error != null)
            errorObject.set(response.error)

            val showButtons = response.error != null || response.data == null
            showLoginButtons.set(showButtons)
            isInProgress.set(false)

            response.data?.let { Session.user = it }
        }

        return response
    }

    private fun progressStart() {
        isInProgress.set(true)
        showLoginButtons.set(false)
        isError.set(false)
    }

}