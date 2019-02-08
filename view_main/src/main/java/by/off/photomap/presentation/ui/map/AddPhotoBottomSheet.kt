package by.off.photomap.presentation.ui.map

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import by.off.photomap.core.ui.*
import by.off.photomap.presentation.ui.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.dialog_add_photo.*

class AddPhotoBottomSheet : BottomSheetDialogFragment() {
    private lateinit var latLong: LatLng
    var onAddClicked: ((option: Int) -> Unit)? = null
    var placeLiveData: LiveData<GeoResponse>? = null

    companion object {
        const val OPTION_GALLERY = 0
        const val OPTION_MAKE_PHOTO = 1

        private const val ARG_GEO_POINT = "arg_geo_point"

        fun createNewDialog(latLong: LatLng): AddPhotoBottomSheet =
            AddPhotoBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_GEO_POINT, latLong)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val data = arguments?.getParcelable<LatLng>(ARG_GEO_POINT)
        if (data != null) {
            latLong = data
        } else {
            // todo show something?
            dismiss()
            return
        }

        progressPlaceLoad.show()

        txtLat.text = formatLatitude(latLong.latitude, ctx)
        txtLong.text = formatLongitude(latLong.longitude, ctx)

        txtFromGallery.setOnClickListener {
            //dismiss()
            onAddClicked?.invoke(OPTION_GALLERY)
        }
        txtFromCamera.setOnClickListener {
            //dismiss()
            onAddClicked?.invoke(OPTION_MAKE_PHOTO)
        }

        placeLiveData?.observe(this, Observer {
            it?.let { response ->
                if (!response.read) {
                    progressPlaceLoad.hide()
                    txtPlaceInfo.apply { fadeIn() }.setText(R.string.fish_text)
                    txtPlaceInfo.text = response.info
                    response.read = true
                }
            }
        })
    }
}