package by.off.photomap.presentation.viewmodel.photo

import android.arch.lifecycle.*
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.net.Uri
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import javax.inject.Inject

class PhotoViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val liveData: LiveData<Unit> = photoService.serviceLiveData.map { response -> onResponse(response) }
    val loadImageLiveData = photoService.loadImageLiveData.map { progressPerCent -> onLoadStatus(progressPerCent) }
    val fileLiveData = photoService.serviceFileLiveData.map { filePath -> onImageFile(filePath) }
    val saveEnableLiveData = MutableLiveData<Boolean?>()
    val modeLiveData = MutableLiveData<MODE>()

    val imageUri = ObservableField<Uri>()
    val inProgress = ObservableBoolean(false)
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

    fun save() {
        if (validate()) {
            saveInProgress = true
            progressIndeterminate.set(false)
            inProgress.set(true)
            saveEnableLiveData.postValue(false)

            val photo = photoInfo.get()
            if (photo == null) {
                errorMessage.set("No data available for save.") // todo need to fix this
            } else {
                photoService.save(photo, imageUri.get()!!)
            }
        }
    }

    fun update(photoInfo: PhotoInfo) {
        TODO("Yet to be implemented")
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
        when {
            saveInProgress && response.data != null -> exitScreen = true
            !saveInProgress && response.data != null -> photoInfo.set(response.data)
            error != null -> errorMessage.set(error.message)
        }
        saveInProgress = false

        if (exitScreen) {
            modeLiveData.value = MODE.CLOSE
        }
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

    enum class MODE {
        CREATE, EDIT, VIEW, CLOSE
    }
}