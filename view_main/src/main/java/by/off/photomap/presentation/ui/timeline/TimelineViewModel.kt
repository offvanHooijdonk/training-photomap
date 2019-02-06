package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import javax.inject.Inject

class TimelineViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val liveData = photoService.serviceListLiveData.map { onData(it) }
    val thumbnailLiveData = photoService.thumbnailLiveData

    val listData = ObservableArrayList<PhotoInfo>()

    fun loadData() {
        photoService.listOrderTime()
    }

    fun requestThumbnail(photoId: String) {
        photoService.requestThumbnail(photoId)
    }

    private fun onData(response: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {
        Log.i(LOGCAT, "Photos loaded, size: ${response.list.size}")

        listData.clear()
        listData.addAll(response.list)

        return response
    }
}