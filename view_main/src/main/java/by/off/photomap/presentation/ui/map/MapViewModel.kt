package by.off.photomap.presentation.ui.map

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.core.utils.switchMap
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MapViewModel @Inject constructor(private val photoService: PhotoService, private val geoPointService: GeoPointService) : ViewModel() {
    val fileLiveData: LiveData<String> = photoService.serviceFileLiveData
    val listLiveData = photoService.serviceListLiveData.map { onListResponse(it) }
    val thumbLiveData = photoService.thumbnailLiveData

    fun requestThumbnail(id: String) {
        photoService.requestThumbnail(id)
    }

    fun saveTempFile(bitmap: Bitmap) {
        photoService.saveToTempFile(bitmap)
    }

    private fun onListResponse(listResponse: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {
        return listResponse
    }
}

data class GeoResponse(val info: String, var read: Boolean = false)