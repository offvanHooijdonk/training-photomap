package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userService: UserService) : ViewModel() {

    fun logIn(): LiveData<Response<UserInfo>> = userService.logIn()

    fun authenticate(userName: String, pwd: String): LiveData<Response<UserInfo>> = userService.authenticate(userName, pwd)

    fun registerAndLogin(userInfo: UserInfo, pwd: String): LiveData<Response<UserInfo>> = userService.registerAndLogin(userInfo, pwd)
}