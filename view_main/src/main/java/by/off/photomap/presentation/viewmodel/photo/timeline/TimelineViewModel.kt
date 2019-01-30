package by.off.photomap.presentation.viewmodel.photo.timeline

import android.arch.lifecycle.ViewModel
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import javax.inject.Inject

class TimelineViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val liveData = photoService.serviceLiveData.map { onData(it) }

    fun getDate() {
        photoService.list()
    }

    private fun onData(response: Response<PhotoInfo>): Response<PhotoInfo> {

        return response
    }
}