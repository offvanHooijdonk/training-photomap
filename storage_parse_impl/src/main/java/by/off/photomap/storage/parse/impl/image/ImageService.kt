package by.off.photomap.storage.parse.impl.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.impl.parse.ParsePhotoService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

class ImageService @Inject constructor(private val ctx: Context) {
    private val contentColumns = arrayOf(
        MediaStore.Images.Media.LATITUDE,
        MediaStore.Images.Media.LONGITUDE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.TITLE,
        MediaStore.Images.Media.DATA
    )

    fun resizePhoto(uri: Uri, width: Int, height: Int): ByteArray? {
        val bitOpts = BitmapFactory.Options()
        bitOpts.inJustDecodeBounds = true
        BitmapFactory.decodeStream(ctx.contentResolver.openInputStream(uri), null, bitOpts)

        val scaleFactor = Math.min(bitOpts.outWidth / width, bitOpts.outHeight / height)

        bitOpts.inJustDecodeBounds = false
        bitOpts.inSampleSize = scaleFactor

        val bitmap = BitmapFactory.decodeStream(ctx.contentResolver.openInputStream(uri), null, bitOpts)!!
        val stream = ByteArrayOutputStream()
        val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return if (result) stream.toByteArray() else null
    }

    fun saveBitmapToTempFile(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return if (result) {
            val filePath = "${ctx.filesDir.absolutePath}/temp_${System.currentTimeMillis()}"
            val file = File(filePath)
            if (file.exists()) file.delete() else file.createNewFile()
            FileOutputStream(file).use { it.write(stream.toByteArray()) }
            filePath
        } else {
            null
        }
    }

    fun readBytes(uri: Uri): ByteArray {
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

    fun getMetadata(uri: Uri): Response<PhotoInfo> {
        return ctx.contentResolver.query(uri, contentColumns, null, null, null).use {
            it!!.moveToFirst()
            val latitude = it.getDouble(it.getColumnIndex(MediaStore.Images.Media.LATITUDE)).let { value -> if (value == 0.0) null else value }
            val longitude = it.getDouble(it.getColumnIndex(MediaStore.Images.Media.LONGITUDE)).let { value -> if (value == 0.0) null else value }
            val dateTaken = it.getLong(it.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
            val description = it.getString(it.getColumnIndex(MediaStore.Images.Media.TITLE))
            Response(PhotoInfo("", null, description ?: "", Date(dateTaken), 0, latitude, longitude))
        }
    }
}