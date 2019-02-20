package by.off.photomap.util.thumbs

import android.widget.ImageView

interface ThumbnailLoader {
    fun loadById(photoId: String, imageView: ImageView/*, callback: () -> Unit*/)
}