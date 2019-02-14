package by.off.photomap.presentation.ui.map

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import by.off.photomap.core.ui.DateHelper
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.item_marker.view.*

class MarkerAdapter(
    private val ctx: Context,
    private val requestThumbnail: (photoId: String, callback: (photoId: String, filePath: String?) -> Unit) -> Unit
) : GoogleMap.InfoWindowAdapter {
    private val thumbCache = mutableMapOf<String, String>()

    override fun getInfoContents(marker: Marker?): View? {
        marker?.let {
            val view = LayoutInflater.from(ctx).inflate(R.layout.item_marker, null, false)
            val photo = marker.tag as PhotoInfo
            view.txtDescription.text = photo.description
            view.txtTimestamp.text = DateHelper.formatDateShort(photo.shotTimestamp)

            if (thumbCache[photo.id] == null) {
                requestThumbnail(photo.id) { photoId, filePath ->
                    filePath?.let {
                        thumbCache[photoId] = filePath
                        marker.showInfoWindow()
                    }
                }
            } else {
                view.imgThumb.setImageURI(Uri.parse(thumbCache[photo.id]))
            }
            return view
        }
        return null
    }

    override fun getInfoWindow(marker: Marker?): View? = null
}