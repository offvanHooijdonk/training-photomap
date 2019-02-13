package by.off.photomap.storage.parse.impl.parse

import android.content.Context
import android.net.Uri
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.impl.convertToPhoto
import by.off.photomap.storage.parse.impl.convertToUser
import by.off.photomap.storage.parse.impl.image.ImageService
import com.parse.*
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@PerFeature
class ParsePhotoService @Inject constructor(private val ctx: Context, private val imageService: ImageService) {
    companion object {
        private const val RESIZE_WIDTH = 240
        private const val RESIZE_HEIGHT = 240
    }

    /**
     * Works synchronously unless [progressCallback] is provided - then file save is done in a separate thread
     */
    fun savePhoto(uriPhoto: Uri, resize: Boolean, progressCallback: ((Int, ParseFile) -> Unit)?): ParseFile? {
        var parseFile: ParseFile? = null
        val bytes = readPhotoFile(uriPhoto, resize)
        if (bytes != null) {
            val fileName = createFileObjectName(uriPhoto)
            parseFile = if (fileName != null) {
                ParseFile(fileName, bytes)
            } else {
                ParseFile(bytes)
            }
            saveParseFile(parseFile, progressCallback)
        }
        return parseFile
    }

    /**
     * Works synchronously unless [progressCallback] is provided - then file save is done in a separate thread
     */
    fun savePhoto(filePath: String, resize: Boolean, progressCallback: ((Int, ParseFile) -> Unit)?): ParseFile? {
        var parseFile: ParseFile? = null
        val bytes = readPhotoFile(filePath, resize)
        if (bytes != null) {
            val fileName = createFileObjectName(filePath)
            parseFile = if (!fileName.isEmpty()) {
                ParseFile(fileName, bytes)
            } else {
                ParseFile(bytes)
            }
            saveParseFile(parseFile, progressCallback)
        }
        return parseFile
    }

    /**
     * Works synchronously
     */
    fun saveObject(photoInfo: PhotoInfo, file: ParseFile?, thumbFile: ParseFile?) { // todo move to separate class
        val parse = ParseObject(PhotoInfo.TABLE)
        photoInfo.takeUnless { it.id.isEmpty() }?.let { parse.objectId = it.id }
        file?.let { parse.put(PhotoInfo.BIN_DATA, file) }
        thumbFile?.let { parse.put(PhotoInfo.THUMBNAIL_DATA, thumbFile) }
        parse.put(PhotoInfo.AUTHOR, ParseUser.getCurrentUser())
        parse.put(PhotoInfo.CATEGORY, photoInfo.category)
        parse.put(PhotoInfo.DESCRIPTION, photoInfo.description)
        parse.put(PhotoInfo.SHOT_TIMESTAMP, photoInfo.shotTimestamp)
        parse.put(PhotoInfo.LOCATION, ParseGeoPoint(photoInfo.latitude ?: 0.0, photoInfo.longitude ?: 0.0))
        parse.save()
    }

    /**
     * Works synchronously
     */
    fun getWithImageById(photoId: String): Pair<Response<PhotoInfo>, ParseFile> {
        val parseObject = ParseQuery.getQuery<ParseObject>(PhotoInfo.TABLE).get(photoId)
        val user = convertToUser(parseObject.getParseUser(PhotoInfo.AUTHOR)!!.fetch())
        return Response(convertToPhoto(parseObject, user)) to parseObject.getParseFile(PhotoInfo.BIN_DATA)!!
    }

    fun list(categories: IntArray?, orderBy: String?, directionAsc: Boolean = true): MutableList<PhotoInfo> {
        val list = ParseQuery.getQuery<ParseObject>(PhotoInfo.TABLE).apply {
            orderBy?.let {
                if (directionAsc) addAscendingOrder(it) else addDescendingOrder(it)
            }
            categories?.let {
                whereContainedIn(PhotoInfo.CATEGORY, categories.asList())
            }
        }.find()

        val resultList = mutableListOf<PhotoInfo>()
        for (parseObject in list) {
            val user = convertToUser(parseObject.getParseUser(PhotoInfo.AUTHOR)!!.fetch())
            resultList.add(convertToPhoto(parseObject, user))
        }
        return resultList
    }

    /**
     * Works asynchronously
     */
    fun downloadImageAsync(file: ParseFile, progressCallback: (Int) -> Unit, completeCallback: (String) -> Unit) {
        val filePath = "${ctx.filesDir.absolutePath}/${file.name}"
        val imageFile = File(filePath)
        if (imageFile.createNewFile()) {
            file.getDataInBackground({ data, e ->
                FileOutputStream(imageFile).use {
                    it.write(data)
                }
                completeCallback(filePath)
            }, { perCent ->
                progressCallback(perCent)
            })
        } else {
            completeCallback(filePath)
        }
    }

    fun downloadImageSync(photoId: String): String {
        val parseObject = ParseQuery.getQuery<ParseObject>(PhotoInfo.TABLE).get(photoId)
        val parseFile = parseObject.getParseFile(PhotoInfo.THUMBNAIL_DATA)!!
        val filePath = "${ctx.filesDir.absolutePath}/${parseFile.name}_thumb"
        val imageFile = File(filePath)
        if (imageFile.createNewFile()) {
            val data = parseFile.data
            FileOutputStream(imageFile).use {
                it.write(data)
            }
        }

        return filePath
    }

    private fun saveParseFile(parseFile: ParseFile, progressCallback: ((Int, ParseFile) -> Unit)?) {
        if (progressCallback != null) {
            parseFile.saveInBackground { perCent: Int -> progressCallback(perCent, parseFile) }
        } else {
            parseFile.save()
        }
    }

    private fun readPhotoFile(uri: Uri, resize: Boolean) =
        if (resize) {
            imageService.resizePhoto(uri, RESIZE_WIDTH, RESIZE_HEIGHT)
        } else {
            imageService.readBytes(uri)
        }

    private fun readPhotoFile(filePath: String, resize: Boolean) =
        if (resize) {
            imageService.resizePhoto(filePath, RESIZE_WIDTH, RESIZE_HEIGHT)
        } else {
            imageService.readBytes(filePath)
        }

    private fun createFileObjectName(uri: Uri) = uri.path?.substringAfterLast("/")
    private fun createFileObjectName(filePath: String) = filePath.substringAfterLast("/")
}
