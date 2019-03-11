package by.off.photomap.presentation.ui

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import by.off.photomap.core.utils.map
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(private val userService: UserService, private val photoService: PhotoService): ViewModel() {
    val liveData = userService.logoutLiveData.map { onResponse(it) }
    val btnLocationStatus = ObservableBoolean(false)

    fun logOut() {
        userService.logOut()
    }

    fun filterCategories(categories: IntArray) {
        photoService.setCategoriesFilter(categories)
        photoService.listOrderTime()
    }

    fun setLocationButtonStatus(isStatusOn: Boolean) = btnLocationStatus.set(isStatusOn)

    private fun onResponse(response: Response<UserInfo>): Response<UserInfo> {
        return response
    }

}