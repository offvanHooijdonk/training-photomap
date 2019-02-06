package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.net.Uri
import by.off.photomap.model.PhotoInfo

interface PhotoService {
    val serviceLiveData: LiveData<Response<PhotoInfo>>

    val serviceListLiveData: LiveData<ListResponse<PhotoInfo>>
    val thumbnailLiveData: LiveData<Pair<String, String?>>
    val serviceFileLiveData: LiveData<String>
    val loadImageLiveData: LiveData<Int>

    /**
     * Works with [serviceLiveData] and [loadImageLiveData]
     */
    fun save(photo: PhotoInfo, uriPhoto: Uri)

    /**
     * Works with [serviceLiveData] and [loadImageLiveData]
     */
    fun save(photo: PhotoInfo, filePath: String)

    /**
     * Works with [serviceLiveData]
     */
    fun retrieveMetadata(uri: Uri)

    fun loadById(id: String)

    fun listOrderTime()

    fun requestThumbnail(photoId: String)

    fun saveToTempFile(bitmap: Bitmap)

    fun update(photo: PhotoInfo)
}