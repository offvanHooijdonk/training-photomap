package by.off.photomap.util.thumbs

import android.content.Context
import android.widget.ImageView

object Thumbs : ThumbnailLoader {
    private var loader: ThumbnailLoader? = null

    fun initLoader(context: Context) {
        if (loader == null) loader = ThumbnailLoaderImpl(context)
    }

    override fun loadById(photoId: String, imageView: ImageView/*, callback: () -> Unit*/) {
        loader?.loadById(photoId, imageView/*, callback*/)
    }
}