package by.off.photomap.presentation.ui

import android.arch.lifecycle.ViewModel
import by.off.photomap.core.utils.map
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(private val userService: UserService, private val photoService: PhotoService): ViewModel() {
    val liveData = userService.logoutLiveData.map { onResponse(it) }

    fun logOut() {
        userService.logOut()
    }

    fun filterCategories(categories: IntArray) {
        photoService.setCategoriesFilter(categories)
        photoService.listOrderTime()
    }

    private fun onResponse(response: Response<UserInfo>): Response<UserInfo> {
        return response
    }

}