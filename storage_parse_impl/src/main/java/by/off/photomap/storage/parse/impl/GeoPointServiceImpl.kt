package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.location.Geocoder
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.storage.parse.GeoPointService
import javax.inject.Inject

class GeoPointServiceImpl @Inject constructor(ctx: Context) : GeoPointService {
    override val placeLiveData: LiveData<String>
        get() = placeLD

    private val placeLD = /*object :*/ MutableLiveData<String>() /*{
        override fun observe(owner: LifecycleOwner, observer: Observer<String>) {
            super.observe(owner, Observer { t ->
                if (t != null) {
                    observer.onChanged(t)
                    postValue(null)
                }
            })
        }
    }*/
    private val geoCoder = Geocoder(ctx)

    override fun loadPlaceInfo(lat: Double, lon: Double) {
        launchScopeIO {
            var placeInfo: String? = null
            val addressList = geoCoder.getFromLocation(lat, lon, 5)
            addrs@ for (ad in addressList) {
                for (i in 0..ad.maxAddressLineIndex) {
                    if (ad.getAddressLine(i) != null) {
                        placeInfo = ad.getAddressLine(i)
                        break@addrs
                    }
                }
            }
            placeLD.postValue(placeInfo ?: "") // todo this should be im ViewModel at least
        }
    }

}