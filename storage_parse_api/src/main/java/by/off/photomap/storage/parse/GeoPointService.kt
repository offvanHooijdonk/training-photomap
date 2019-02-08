package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData

interface GeoPointService {
    val placeLiveData: LiveData<String>
    fun loadPlaceInfo(lat: Double, lon: Double)
}