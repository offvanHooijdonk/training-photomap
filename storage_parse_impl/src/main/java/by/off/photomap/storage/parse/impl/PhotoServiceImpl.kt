package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*
import javax.inject.Inject

class PhotoServiceImpl @Inject constructor(private val ctx: Context) : PhotoService {
    private val contentColumns = arrayOf(
        MediaStore.Images.Media.LATITUDE,
        MediaStore.Images.Media.LONGITUDE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.TITLE,
        MediaStore.Images.Media.DATA
    )

    override val serviceLiveData: LiveData<Response<PhotoInfo>>
        get() = liveData
    override val loadImageLiveData: LiveData<Int>
        get() = loadLD

    private val liveData = MutableLiveData<Response<PhotoInfo>>()
    private val loadLD = MutableLiveData<Int>()

    override fun save(photo: PhotoInfo, /*photoFilePath: String*/uriPhoto: Uri) {
        launchScopeIO {
            val inputStream = ctx.contentResolver.openInputStream(uriPhoto)
            val bytes = readBytes(inputStream!!)
            val file = ParseFile(uriPhoto.path!!.substringAfterLast("/"), bytes)
            Log.i(LOGCAT, "File upload starting...")
            file.saveInBackground { percentDone: Int? ->
                when (percentDone) {
                    100 -> {
                        Log.i(LOGCAT, "File upload complete")
                        saveObjectSync(photo, file)
                        loadLD.postValue(percentDone)
                        liveData.postValue(Response(data = photo))
                    }
                    else -> {
                        loadLD.postValue(percentDone)
                    }
                }
            }
        }
    }

    override fun retrieveMetadata(uri: Uri) {
        launchScopeIO {
            val response = try {
                ctx.contentResolver.query(uri, contentColumns, null, null, null)?.use {
                    it.moveToFirst()
                    val latitude = it.getDouble(it.getColumnIndex(MediaStore.Images.Media.LATITUDE))
                    val longitude = it.getDouble(it.getColumnIndex(MediaStore.Images.Media.LONGITUDE))
                    val dateTaken = it.getLong(it.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
                    val description = it.getString(it.getColumnIndex(MediaStore.Images.Media.TITLE))
                    val filePath = it.getString(it.getColumnIndex(MediaStore.Images.Media.DATA))
                    Response(PhotoInfo("", null, description, Date(dateTaken), 0, latitude, longitude))
                }
            } catch (e: Exception) {
                Response<PhotoInfo>(error = e)
            }
            liveData.postValue(response)
        }
    }

    private fun saveObjectSync(photoInfo: PhotoInfo, file: ParseFile) { // todo move to separate class
        val parse = ParseObject(PhotoInfo.TABLE)
        parse.put(PhotoInfo.BIN_DATA, file)
        parse.put(PhotoInfo.AUTHOR, ParseUser.getCurrentUser())
        parse.put(PhotoInfo.CATEGORY, photoInfo.category)
        photoInfo.description?.let {
            parse.put(PhotoInfo.DESCRIPTION, it)
        }
        parse.put(PhotoInfo.SHOT_TIMESTAMP, photoInfo.shotTimestamp)
        parse.put(PhotoInfo.LOCATION, ParseGeoPoint(photoInfo.latitude ?: 0.0, photoInfo.longitude ?: 0.0))
        parse.save()
    }

    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }

        return byteBuffer.toByteArray()
    }
}