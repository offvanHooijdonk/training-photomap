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
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import by.off.photomap.storage.parse.Response
import com.parse.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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

    override fun save(photo: PhotoInfo, /*photoFilePath: String*/uriPhoto: Uri) {
        launchScopeIO {
            val bytes = readBytes(uriPhoto)
            val file = if (uriPhoto.path != null) {
                ParseFile(uriPhoto.path.substringAfterLast("/"), bytes)
            } else {
                ParseFile(bytes)
            }
            Log.i(LOGCAT, "File upload starting...")
            file.saveInBackground { percentDone: Int? ->
                when (percentDone) {
                    100 -> {
                        Log.i(LOGCAT, "File upload complete")
                        val response = try {
                            saveObjectSync(photo, file)
                            Response(photo)
                        } catch (e: Exception) {
                            Response<PhotoInfo>(error = e)
                        }
                        loadLD.postValue(percentDone)
                        liveData.postValue(response)
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
                    // todo move implementation to a separate class
                    it.moveToFirst()
                    val latitude = it.getDouble(it.getColumnIndex(MediaStore.Images.Media.LATITUDE)).let { value -> if (value == 0.0) null else value }
                    val longitude = it.getDouble(it.getColumnIndex(MediaStore.Images.Media.LONGITUDE)).let { value -> if (value == 0.0) null else value }
                    val dateTaken = it.getLong(it.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
                    val description = it.getString(it.getColumnIndex(MediaStore.Images.Media.TITLE))
                    Response(PhotoInfo("", null, description ?: "", Date(dateTaken), 0, latitude, longitude))
                }
            } catch (e: Exception) {
                Response<PhotoInfo>(error = e)
            }
            liveData.postValue(response)
        }
    }

    override fun loadById(id: String) {
        launchScopeIO {
            lateinit var parseObject: ParseObject
            val response = try {
                parseObject = ParseQuery.getQuery<ParseObject>(PhotoInfo.TABLE).get(id)
                val user = convertToUser(parseObject.getParseUser(PhotoInfo.AUTHOR)!!.fetch())
                Response(convertToPhoto(parseObject, user))
            } catch (e: Exception) {
                Response<PhotoInfo>(error = e)
            }

            liveData.postValue(response)

            if (response.data != null) {
                downloadImage(parseObject.getParseFile(PhotoInfo.BIN_DATA)!!)
            }
        }
    }

    override fun list() { // TODO add parameter for sorting or rename method
        launchScopeIO {
            val list = ParseQuery.getQuery<ParseObject>(PhotoInfo.TABLE).addDescendingOrder(PhotoInfo.SHOT_TIMESTAMP).find()
            val resultList = mutableListOf<PhotoInfo>()
            for (parseObject in list) {
                val user = convertToUser(parseObject.getParseUser(PhotoInfo.AUTHOR)!!.fetch())
                resultList.add(convertToPhoto(parseObject, user))
            }
            listLiveData.postValue(ListResponse(resultList))
        }
    }

    private fun downloadImage(file: ParseFile) {
        val filePath = "${ctx.filesDir.absolutePath}/${file.name}"
        val imageFile = File(filePath)
        if (imageFile.createNewFile()) {
            file.getDataInBackground({ data, e ->
                FileOutputStream(imageFile).use {
                    it.write(data)
                }
                fileLiveData.postValue(filePath)
            }, { perCent ->
                loadLD.postValue(perCent)
            })
        } else {
            fileLiveData.postValue(filePath)
        }
    }


    private fun saveObjectSync(photoInfo: PhotoInfo, file: ParseFile) { // todo move to separate class
        val parse = ParseObject(PhotoInfo.TABLE)
        parse.put(PhotoInfo.BIN_DATA, file)
        parse.put(PhotoInfo.AUTHOR, ParseUser.getCurrentUser())
        parse.put(PhotoInfo.CATEGORY, photoInfo.category)
        parse.put(PhotoInfo.DESCRIPTION, photoInfo.description)
        parse.put(PhotoInfo.SHOT_TIMESTAMP, photoInfo.shotTimestamp)
        parse.put(PhotoInfo.LOCATION, ParseGeoPoint(photoInfo.latitude ?: 0.0, photoInfo.longitude ?: 0.0))
        parse.save()
    }

    private fun readBytes(uri: Uri): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val inputStream = ctx.contentResolver.openInputStream(uri)
        inputStream?.use {
            // TODO handle null
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)

            var len: Int
            while (inputStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
        }
        return byteBuffer.toByteArray()
    }
}