package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.impl.image.ImageService
import by.off.photomap.storage.parse.impl.parse.ParsePhotoService
import com.parse.ParseFile
import javax.inject.Inject

class PhotoServiceImpl @Inject constructor(
    private val parsePhotoService: ParsePhotoService,
    private val imageService: ImageService
) : PhotoService {

    override val thumbnailLiveData: LiveData<Pair<String, String?>>
        get() = thumbLD
    override val serviceLiveData: LiveData<Response<PhotoInfo>>
        get() = liveData
    override val serviceListLiveData: LiveData<ListResponse<PhotoInfo>>
        get() = listLiveData
    override val loadImageLiveData: LiveData<Int>
        get() = loadLD
    override val serviceFileLiveData: LiveData<String>
        get() = fileLiveData

    private val liveData = MutableLiveData<Response<PhotoInfo>>()
    private val listLiveData = MutableLiveData<ListResponse<PhotoInfo>>()
    private val loadLD = MutableLiveData<Int>()
    private val fileLiveData = MutableLiveData<String>()
    private val thumbLD = MutableLiveData<Pair<String, String?>>()

    override fun save(photo: PhotoInfo, uriPhoto: Uri) {
        launchScopeIO {
            parsePhotoService.savePhoto(uriPhoto, false) { perCent, file ->
                when (perCent) {
                    100 -> {
                        Log.i(LOGCAT, "File upload complete")
                        val response = try {
                            val thumbFile = parsePhotoService.savePhoto(uriPhoto, true, null)
                            parsePhotoService.saveObject(photo, file, thumbFile)
                            Response(photo)
                        } catch (e: Exception) {
                            Response<PhotoInfo>(error = e)
                        }
                        loadLD.postValue(perCent)
                        liveData.postValue(response)
                    }
                    else -> {
                        loadLD.postValue(perCent)
                    }
                }
            }
        }
    }

    override fun retrieveMetadata(uri: Uri) {
        launchScopeIO {
            val response = try {
                imageService.getMetadata(uri)
            } catch (e: Exception) {
                Response<PhotoInfo>(error = e)
            }
            liveData.postValue(response)
        }
    }

    override fun loadById(id: String) {
        launchScopeIO {
            lateinit var parseFile: ParseFile
            val response = try {
                val data = parsePhotoService.getWithImageById(id)
                parseFile = data.second
                data.first
            } catch (e: Exception) {
                Response<PhotoInfo>(error = e)
            }
            liveData.postValue(response)

            if (response.data != null) {
                parsePhotoService.downloadImageAsync(parseFile, { perCent ->
                    loadLD.postValue(perCent)
                }, { filePath ->
                    fileLiveData.postValue(filePath)
                })
            }
        }
    }

    override fun listOrderTime() {
        launchScopeIO {
            val resultList = parsePhotoService.list(PhotoInfo.SHOT_TIMESTAMP, false)
            listLiveData.postValue(ListResponse(resultList))
        }
    }

    override fun saveToTempFile(bitmap: Bitmap) {
        launchScopeIO {
            val filePath = imageService.saveBitmapToTempFile(bitmap)
            fileLiveData.postValue(filePath)
        }
    }

    override fun requestThumbnail(photoId: String) {
        launchScopeIO {
            val filePath = parsePhotoService.downloadImageSync(photoId)
            thumbLD.postValue(photoId to filePath)
        }
    }

}