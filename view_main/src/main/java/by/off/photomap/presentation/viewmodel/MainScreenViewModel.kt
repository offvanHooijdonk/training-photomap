package by.off.photomap.presentation.viewmodel

import android.arch.lifecycle.ViewModel
import by.off.photomap.core.utils.map
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(private val userService: UserService): ViewModel() {
    val liveData = userService.serviceLiveData.map { onResponse(it) }

    fun logOut() {
        userService.logOut()
    }

    private fun onResponse(response: Response<UserInfo>): Exception? {
        return response.error
    }

}