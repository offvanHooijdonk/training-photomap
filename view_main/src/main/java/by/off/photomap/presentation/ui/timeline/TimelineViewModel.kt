package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import javax.inject.Inject

class TimelineViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val liveData = photoService.serviceListLiveData.map { onData(it) }
    val isRefreshing = ObservableBoolean(false)

    val listData = ObservableArrayList<PhotoInfo>()

    init {
        isRefreshing.set(true)
        photoService.listOrderTime()
        Log.i(LOGCAT, "Init Timeline ViewModel")
    }

    fun loadData() {
        isRefreshing.set(true)
        photoService.listOrderTime()
    }

    private fun onData(response: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {
        Log.i(LOGCAT, "Photos loaded, size: ${response.list.size}")

        isRefreshing.set(false)
        listData.clear()
        listData.addAll(response.list)

        return response
    }
}