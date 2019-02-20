package by.off.photomap.util.thumbs

import android.arch.lifecycle.*
import android.content.Context
import android.net.Uri
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.PhotoInfo
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

internal class ThumbnailLoaderImpl(ctx: Context) : ThumbnailLoader, LifecycleOwner {
    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    companion object {
        private var TAG_PHOTO_ID = R.string.tag_photo_id
        private const val CACHE_LIMIT = 10_000
        private const val THUMB_FILE_POSTFIX = "_thumb"

        private var tempFilesPath = ""
    }

    private val lifecycleRegistry = LifecycleRegistry(this)

    private val views = mutableMapOf<String, ImageView>()
    private val cachedFiles = mutableMapOf<String, String>()

    /**
     * Convention is: <Photo Id, View Key, File Path>
     */
    private val thumbLD = MutableLiveData<Triple<String, String, String?>>()

    init {
        tempFilesPath = ctx.filesDir.absolutePath

        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        thumbLD.observe(this, Observer { response ->
            response?.let {
                onThumbResponse(it.first, it.second, it.third)
            }
        })
    }

    private fun onThumbResponse(photoId: String, viewKey: String, filePath: String?) {
        filePath?.let {
            cacheFile(photoId, filePath)

            applyFileIfViewAwaits(photoId, viewKey, filePath)
        }
    }

    override fun loadById(photoId: String, imageView: ImageView) {
        val cachedFile = getCachedFileIfExists(photoId)
        if (cachedFile != null) {
            setFileToImageView(cachedFile, imageView)
        } else {
            val key = composeKey(photoId, imageView)
            views[key] = imageView
            setupImageView(photoId, imageView)

            requestThumbnail(key, photoId)
        }
    }

    // region File Request
    private fun requestThumbnail(viewKey: String, photoId: String) {
        launchScopeIO {
            val filePath = downloadImageSync(photoId)
            thumbLD.postValue(Triple(photoId, viewKey, filePath))
        }
    }

    private fun downloadImageSync(photoId: String): String {
        val parseObject = ParseQuery.getQuery<ParseObject>(PhotoInfo.TABLE).get(photoId)
        val parseFile = parseObject.getParseFile(PhotoInfo.THUMBNAIL_DATA)!!
        val filePath = "$tempFilesPath/${parseFile.name}$THUMB_FILE_POSTFIX"
        val imageFile = File(filePath)
        if (imageFile.createNewFile()) {
            val data = parseFile.data
            FileOutputStream(imageFile).use {
                it.write(data)
            }
        }
        return filePath
    }
    // endregion

    // region ImageView processing
    private fun applyFileIfViewAwaits(photoId: String, viewKey: String, filePath: String) {
        val imageView = views[viewKey]
        if (imageView != null && imageView.getTag(TAG_PHOTO_ID) == photoId) {
            setFileToImageView(filePath, imageView)
            views.remove(viewKey)
        }
    }

    private fun setFileToImageView(filePath: String, imageView: ImageView) {
        imageView.setImageURI(Uri.parse(filePath))
        imageView.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_INFO))
    }

    private fun setupImageView(photoId: String, imageView: ImageView) {
        imageView.setTag(TAG_PHOTO_ID, photoId)
        imageView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                v?.let {
                    views.remove(composeKey(v.getTag(TAG_PHOTO_ID).toString(), v))
                }
            }

            override fun onViewAttachedToWindow(v: View?) {}
        })
    }

    private fun composeKey(photoId: String, view: View) = "$photoId|${view.hashCode()}"
    // endregion

    // region Cache
    private fun cacheFile(photoId: String, filePath: String) {
        CoroutineScope(Dispatchers.Default).launch {
            if (cachedFiles.size > CACHE_LIMIT) cachedFiles.keys.take(CACHE_LIMIT / 2).forEach { key -> cachedFiles.remove(key) }
        }.invokeOnCompletion {
            cachedFiles[photoId] = filePath
        }
    }

    private fun getCachedFileIfExists(photoId: String): String? {
        val filePath = cachedFiles[photoId]
        return if (filePath != null && File(filePath).run { exists() && isFile }) {
            filePath
        } else null
    }
    // endregion
}