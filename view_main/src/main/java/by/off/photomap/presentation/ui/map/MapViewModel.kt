package by.off.photomap.presentation.ui.map

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import javax.inject.Inject

class MapViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val fileLiveData: LiveData<String> = photoService.tempFileLiveData
    val listLiveData = photoService.serviceListLiveData.map { onListResponse(it) }

    fun saveTempFile(bitmap: Bitmap) {
        photoService.saveToTempFile(bitmap)
    }

    private fun onListResponse(listResponse: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {
        return listResponse
    }
}

data class GeoResponse(val info: String, var read: Boolean = false)