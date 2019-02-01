package by.off.photomap.presentation.viewmodel.photo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.net.Uri
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
    val fileLiveData = photoService.serviceFileLiveData.map { filePath -> onImageFile(filePath) }

    val errorLiveData = MutableLiveData<Exception?>()

    val saveEnableLiveData = MutableLiveData<Boolean?>()
    val imageUri = ObservableField<Uri>()

    val inProgress = ObservableBoolean(false)
    val progressIndeterminate = ObservableBoolean(true)
    val progressPerCent = ObservableInt(0)
    val photoInfo = ObservableField<PhotoInfo?>()
    val editMode = ObservableBoolean(false)
    val descriptionError = ObservableField<String?>()
    val filePath = ObservableField<String?>()
    private val saveInProgress = ObservableBoolean(false)

    fun loadById(id: String) {
        inProgress.set(true)
        progressIndeterminate.set(true)
        editMode.set(false)// todo check if this is author

        photoService.loadById(id)
    }

    fun setupWithUri(uri: Uri) {
        imageUri.set(uri)
        progressIndeterminate.set(true)
        inProgress.set(true)
        editMode.set(true)

        photoService.retrieveMetadata(uri)
    }

    fun save() {
        if (validate()) {
            saveInProgress.set(true)
            progressIndeterminate.set(false)
            inProgress.set(true)
            saveEnableLiveData.postValue(false)

            photoService.save(photoInfo.get()!!, imageUri.get()!!) // TODO add null check
        }
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
        Log.i(LOGCAT, "Data arrived ${response.data}")

        if (saveInProgress.get() && response.data != null) {
            exitScreen = true
        }
        response.error?.also {
            errorLiveData.postValue(it)
            saveEnableLiveData.postValue(false)
        } ?: let {
            saveEnableLiveData.postValue(true)
        }
        saveInProgress.set(false)

        return exitScreen
    }

    private fun onImageFile(filePath: String) {
        this.filePath.set(filePath)
    }

    private fun validate(): Boolean {
        return if (photoInfo.get()?.description != null && photoInfo.get()?.description?.trim()?.isEmpty() != true) {
            true
        } else {
            descriptionError.set("The field is mandatory, please fill in")
            false
        }
    }

}