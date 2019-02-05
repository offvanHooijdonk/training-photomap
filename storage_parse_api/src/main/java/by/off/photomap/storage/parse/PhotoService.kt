package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import android.net.Uri
import by.off.photomap.model.PhotoInfo

interface PhotoService {
    val serviceLiveData: LiveData<Response<PhotoInfo>>

    val serviceListLiveData: LiveData<ListResponse<PhotoInfo>>
    val thumbnailLiveData: LiveData<Pair<String, String?>>

    val loadImageLiveData: LiveData<Int>

    /**
     * Works with [serviceLiveData] and [loadImageLiveData]
     */
    fun save(photo: PhotoInfo, /*photoFilePath: String*/ uriPhoto: Uri)

    /**
     * Works with [serviceLiveData]
     */
    fun retrieveMetadata(uri: Uri)

    fun loadById(id: String)

    fun list()
    val serviceFileLiveData: LiveData<String>
    fun requestThumbnail(photoId: String)
}