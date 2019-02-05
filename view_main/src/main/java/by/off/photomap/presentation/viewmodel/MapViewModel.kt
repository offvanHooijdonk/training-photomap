package by.off.photomap.presentation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import by.off.photomap.storage.parse.PhotoService
import javax.inject.Inject

class MapViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val fileLiveData: LiveData<String> = photoService.serviceFileLiveData

    fun saveTempFile(bitmap: Bitmap) {
        photoService.saveToTempFile(bitmap)
    }
}