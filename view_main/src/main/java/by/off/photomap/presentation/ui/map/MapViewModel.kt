package by.off.photomap.presentation.ui.map

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.GeoPointService
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class MapViewModel @Inject constructor(private val photoService: PhotoService, private val geoPointService: GeoPointService) : ViewModel() {
    val fileLiveData: LiveData<String> = photoService.serviceFileLiveData
    val listLiveData = photoService.serviceListLiveData.map { onListResponse(it) }
    val placeLiveData = geoPointService.placeLiveData.map { onPlaceData(it) }

    private fun onPlaceData(info: String) = GeoResponse(info)

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

    fun loadPlaceInfo(latLong: LatLng) {
        geoPointService.loadPlaceInfo(latLong.latitude, latLong.longitude)
    }

    private fun onListResponse(listResponse: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {

        return listResponse
    }
}

data class GeoResponse(val info: String, var read: Boolean = false)