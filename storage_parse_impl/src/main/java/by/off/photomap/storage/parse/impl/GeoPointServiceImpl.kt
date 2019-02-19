package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Geocoder
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.storage.parse.GeoPointService
import javax.inject.Inject

@PerFeature
class GeoPointServiceImpl @Inject constructor(ctx: Context) : GeoPointService {
    override val placeLiveData: LiveData<String>
        get() = placeLD

    private val placeLD = MutableLiveData<String>()
    private val geoCoder = Geocoder(ctx)

    override fun loadPlaceInfo(lat: Double, lon: Double) {
        launchScopeIO {
            var placeInfo = ""
            try {
                geoCoder.getFromLocation(lat, lon, 5).firstOrNull { it.maxAddressLineIndex >= 0 }?.also { adr ->
                    placeInfo = adr.getAddressLine(0)
                }
            } catch (e: Exception) {
                // todo create Response object?
            }
            placeLD.postValue(placeInfo)
        }
    }

}