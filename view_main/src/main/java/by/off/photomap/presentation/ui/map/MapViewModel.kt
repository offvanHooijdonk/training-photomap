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
    val fileLiveData: LiveData<String> = photoService.serviceFileLiveData
    val listLiveData = photoService.serviceListLiveData.map { onListResponse(it) }
    val thumbLiveData = photoService.thumbnailLiveData

    fun requestThumbnail(id: String) {
        photoService.requestThumbnail(id)
    }

    fun loadData() {
        photoService.listOrderTime()
    }

    fun saveTempFile(bitmap: Bitmap) {
        photoService.saveToTempFile(bitmap)
    }

    private fun onListResponse(listResponse: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {

        return listResponse
    }
}