package by.off.photomap.presentation.ui.map

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import by.off.photomap.core.utils.map
import by.off.photomap.core.utils.switchMap
import by.off.photomap.storage.parse.GeoPointService
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class AddPhotoDialogViewModel @Inject constructor(private val geoPointService: GeoPointService): ViewModel() {
    val placeLiveData = geoPointService.placeLiveData.switchMap { switchToGeoResponse(it) }.map { onPlaceData(it) }
    private val switchPlaceLD = MutableLiveData<GeoResponse>()

    val placeProgress = ObservableBoolean(false)
    val placeGeoPoint = ObservableField<LatLng>(LatLng(0.0, 0.0))
    val placeDescription = ObservableField<String?>()

    fun loadPlaceInfo(latLong: LatLng) {
        placeProgress.set(true)
        placeDescription.set(null)
        placeGeoPoint.set(latLong)
        geoPointService.loadPlaceInfo(latLong.latitude, latLong.longitude)
    }

    private fun switchToGeoResponse(place: String) = switchPlaceLD.apply { value = GeoResponse(place) }

    private fun onPlaceData(response: GeoResponse?) {
        if (response != null && !response.read) {
            placeDescription.set(response.info)
            placeProgress.set(false)
            response.read = true
        }
    }
}