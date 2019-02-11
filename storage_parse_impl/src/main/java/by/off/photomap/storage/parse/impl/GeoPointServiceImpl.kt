package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.location.Geocoder
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.storage.parse.GeoPointService
import javax.inject.Inject

class GeoPointServiceImpl @Inject constructor(ctx: Context) : GeoPointService {
    override val placeLiveData: LiveData<String>
        get() = placeLD

    private val placeLD = MutableLiveData<String>()
    private val geoCoder = Geocoder(ctx)

    override fun loadPlaceInfo(lat: Double, lon: Double) {
        launchScopeIO {
            var placeInfo = ""
            try {
                val addressList = geoCoder.getFromLocation(lat, lon, 5)
                addrs@ for (ad in addressList) {
                    for (i in 0..ad.maxAddressLineIndex) {
                        if (ad.getAddressLine(i) != null) {
                            placeInfo = ad.getAddressLine(i)
                            break@addrs
                        }
                    }
                }
            } catch (e: Exception) {
                // todo create Response object?
            }
            placeLD.postValue(placeInfo)
        }
    }

}