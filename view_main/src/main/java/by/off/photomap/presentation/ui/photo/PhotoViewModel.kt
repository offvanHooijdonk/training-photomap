package by.off.photomap.presentation.ui.photo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.net.Uri
import android.util.Log
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.core.utils.session.Session
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import java.util.*
import javax.inject.Inject

class PhotoViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val liveData: LiveData<Unit> = photoService.serviceLiveData.map { response -> onResponse(response) }
    val loadImageLiveData = photoService.loadImageLiveData.map { progressPerCent -> onLoadStatus(progressPerCent) }
    val fileLiveData = photoService.serviceFileLiveData.map { filePath -> onImageFile(filePath) }
    val saveEnableLiveData = MutableLiveData<Boolean?>()
    val modeLiveData = MutableLiveData<MODE>()
    val handleBackLiveData = MutableLiveData<Boolean>()

    val imageUri = ObservableField<Uri>()
    val inProgress = ObservableBoolean(false)
    val downloadInProgress = ObservableBoolean(false)
    val progressIndeterminate = ObservableBoolean(true)
    val progressPerCent = ObservableInt(0)
    val photoInfo = ObservableField<PhotoInfo?>()
    val editMode = ObservableBoolean(false)
    val descriptionError = ObservableField<String?>()
    val filePath = ObservableField<String?>()
    val errorMessage = ObservableField<String?>()

    private var saveInProgress = false
    private var originalPhotoInfo: PhotoInfo? = null
    private var latLong: Pair<Double, Double>? = null

    fun setupWithPhotoById(id: String) {
        modeLiveData.value = MODE.VIEW
        editMode.set(false)
        inProgress.set(true)
        downloadInProgress.set(true)
        progressIndeterminate.set(true)

        photoService.loadById(id)
    }

    fun setupWithUri(uri: Uri, latLong: Pair<Double, Double>?) {
        modeLiveData.value = MODE.CREATE
        imageUri.set(uri)
        progressIndeterminate.set(true)
        inProgress.set(true)
        editMode.set(true)
        this.latLong = latLong

        photoService.retrieveMetadata(uri)
    }

    fun setupWithFile(filePath: String, latLong: Pair<Double, Double>?) {
        modeLiveData.postValue(MODE.CREATE)
        editMode.set(true)
        this.filePath.set(filePath)
        photoInfo.set(PhotoInfo("", null, "", Date(), CategoryInfo.getDefault()))
        this.latLong = latLong
    }

    fun save() {
        if (validate()) {
            photoInfo.get()?.let { photo ->
                saveInProgress = true
                inProgress.set(true)
                saveEnableLiveData.postValue(false)

                if (modeLiveData.value == MODE.EDIT) {
                    progressIndeterminate.set(true)
                    startUpdate(photo)
                } else {
                    progressIndeterminate.set(false)
                    startSave(photo)
                }
            }
        }
    }

    fun onBackRequested() {
        Log.i(LOGCAT, "Original hash ${originalPhotoInfo?.hashCode()} vs new hash ${photoInfo.get()?.hashCode()}")
        handleBackLiveData.value =
            modeLiveData.value == MODE.CREATE || modeLiveData.value == MODE.EDIT && originalPhotoInfo?.hashCode() != photoInfo.get()?.hashCode()
    }

    private fun onLoadStatus(perCent: Int?) {
        perCent?.let {
            progressPerCent.set(perCent)
        }
    }

    private fun onResponse(response: Response<PhotoInfo>?) {
        response?.let {
            var exitScreen = false
            inProgress.set(false)
            progressPerCent.set(0)
            saveEnableLiveData.postValue(true)
            //Log.i(LOGCAT, "Data arrived ${response.data}")

            val error = response.error
            val data = response.data
            when {
                saveInProgress && data != null -> exitScreen = true
                !saveInProgress && data != null -> {
                    handleLoadedData(data)
                }
                error != null -> errorMessage.set(error.message)
            }
            saveInProgress = false

            if (exitScreen) {
                modeLiveData.value = MODE.CLOSE
            }
        }
    }

    private fun onImageFile(filePath: String?) {
        this.filePath.set(filePath)
        downloadInProgress.set(false)
    }

    private fun handleLoadedData(data: PhotoInfo) {
        photoInfo.set(data)
        originalPhotoInfo = data.copy()
        if (data.author?.id == Session.user.id) {
            editMode.set(true)
            modeLiveData.value = MODE.EDIT
        }
    }

    private fun validate(): Boolean {
        return if (photoInfo.get()?.description != null && photoInfo.get()?.description?.trim()?.isEmpty() != true) {
            true
        } else {
            descriptionError.set("The field is mandatory, please fill in")
            false
        }
    }

    private fun startSave(photo: PhotoInfo) {
        val uri = imageUri.get()
        val filePath = this.filePath.get()
        latLong?.let { photo.latitude = it.first; photo.longitude = it.second }

        when {
            uri != null -> photoService.save(photo, uri)
            filePath != null -> photoService.save(photo, filePath)
        }
    }

    private fun startUpdate(photo: PhotoInfo) {
        photoService.update(photo)
    }

    enum class MODE {
        CREATE, EDIT, VIEW, CLOSE
    }
}