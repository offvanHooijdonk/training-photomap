package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.net.Uri
import by.off.photomap.model.PhotoInfo

interface PhotoService {
    val serviceLiveData: LiveData<Response<PhotoInfo>>

    val serviceListLiveData: LiveData<ListResponse<PhotoInfo>>
    val serviceFileLiveData: LiveData<String>
    val loadImageLiveData: LiveData<Int>
    val tempFileLiveData: LiveData<String>

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

    fun saveToTempFile(bitmap: Bitmap)

    fun update(photo: PhotoInfo)
    fun setFilter(categories: IntArray)
}