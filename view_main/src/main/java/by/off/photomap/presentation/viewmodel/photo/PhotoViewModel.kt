package by.off.photomap.presentation.viewmodel.photo

import android.arch.lifecycle.*
import android.databinding.Observable
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

    fun setupWithPhotoById(id: String) {
        modeLiveData.value = MODE.VIEW
        editMode.set(false)// todo check if this is author
        inProgress.set(true)
        downloadInProgress.set(true)
        progressIndeterminate.set(true)

        photoService.loadById(id)
    }

    fun setupWithUri(uri: Uri) {
        modeLiveData.value = MODE.CREATE
        imageUri.set(uri)
        progressIndeterminate.set(true)
        inProgress.set(true)
        editMode.set(true)

        photoService.retrieveMetadata(uri)
    }

    fun setupWithFile(filePath: String) {
        modeLiveData.postValue(MODE.CREATE)
        editMode.set(true)
        this.filePath.set(filePath)
        photoInfo.set(PhotoInfo("", null, "", Date(), CategoryInfo.ID_DEAFULT))
    }

    fun save() {
        if (validate()) {
            saveInProgress = true
            progressIndeterminate.set(false)
            inProgress.set(true)
            saveEnableLiveData.postValue(false)

            val photo = photoInfo.get()!!
            if (modeLiveData.value == MODE.EDIT) {
                photoService.update(photo)
            } else {
                val uri = imageUri.get()
                val filePath = this.filePath.get()
                when {
                    uri != null -> photoService.save(photo, uri)
                    filePath != null -> photoService.save(photo, filePath)
                }
            }
        }
    }

    fun onBackRequested() {
        handleBackLiveData.value = modeLiveData.value == MODE.CREATE || modeLiveData.value == MODE.EDIT
    }

    private fun onLoadStatus(perCent: Int) {
        progressPerCent.set(perCent)
    }

    private fun onResponse(response: Response<PhotoInfo>) {
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
                photoInfo.set(data)
                if (data.author?.id == Session.user.id) {
                    editMode.set(true)
                    modeLiveData.value = MODE.EDIT
                }
            }
            error != null -> errorMessage.set(error.message)
        }
        saveInProgress = false

        if (exitScreen) {
            modeLiveData.value = MODE.CLOSE
        }
    }

    private fun onImageFile(filePath: String) {
        downloadInProgress.set(false)
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

    enum class MODE {
        CREATE, EDIT, VIEW, CLOSE
    }
}