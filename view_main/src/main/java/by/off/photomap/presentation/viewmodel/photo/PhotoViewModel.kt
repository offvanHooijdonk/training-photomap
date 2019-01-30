package by.off.photomap.presentation.viewmodel.photo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.net.Uri
import android.support.design.widget.Snackbar
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import javax.inject.Inject

class PhotoViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    // todo make a separate livaData for upload
    val liveData: LiveData<Boolean> = photoService.serviceLiveData.map { response -> onResponse(response) }
    val loadImageLiveData = photoService.loadImageLiveData.map { progressPerCent -> onLoadStatus(progressPerCent) }

    val imageUri = ObservableField<Uri>()
    val inProgress = ObservableBoolean(false)
    val progressIndeterminate = ObservableBoolean(true)
    val progressPerCent = ObservableInt(0)
    val photoInfo = ObservableField<PhotoInfo?>()
    val editMode = ObservableBoolean(false)
    private val saveInProgress = ObservableBoolean(false)

    fun loadById() {
        TODO("Yet to be implemented")
    }

    fun setupWithUri(uri: Uri) {
        imageUri.set(uri)
        progressIndeterminate.set(true)
        inProgress.set(true)
        editMode.set(true)

        photoService.retrieveMetadata(uri)
    }

    fun save() {
        saveInProgress.set(true)
        progressIndeterminate.set(false)
        inProgress.set(true)

        photoService.save(photoInfo.get()!!, imageUri.get()!!) // TODO add null check
    }

    fun update(photoInfo: PhotoInfo) {
        TODO("Yet to be implemented")
    }

    private fun onLoadStatus(perCent: Int) {
        progressPerCent.set(perCent)
    }

    private fun onResponse(response: Response<PhotoInfo>): Boolean {
        var exitScreen = false
        inProgress.set(false)
        photoInfo.set(response.data)
        progressPerCent.set(0)

        if (saveInProgress.get() && response.data != null) {
            exitScreen = true
        }
        if (response.error != null) {
            // TODO create error liveData
        }
        saveInProgress.set(false)

        return exitScreen
    }
}