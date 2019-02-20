package by.off.photomap.presentation.ui.map

import android.content.Context
import android.net.Uri
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import by.off.photomap.core.ui.DateHelper
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.util.thumbs.Thumbs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.item_marker.view.*

class MarkerAdapter(private val ctx: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View? {
        marker?.let {
            val view = LayoutInflater.from(ctx).inflate(R.layout.item_marker, null, false)
            val photo = marker.tag as PhotoInfo
            view.txtDescription.text = photo.description
            view.txtTimestamp.text = DateHelper.formatDateShort(photo.shotTimestamp)

            Thumbs.loadById(photo.id, view.imgThumb)
            view.imgThumb.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_INFO) {
                    marker.showInfoWindow()
                    true
                } else false
            }

            return view
        }
        return null
    }

    override fun getInfoWindow(marker: Marker?): View? = null
}