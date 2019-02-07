package by.off.photomap.presentation.ui.map

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.CallbackHolder
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.ui.hue
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

class MapFragment : BaseFragment(), MainActivity.ButtonPhotoListener, MainActivity.ButtonLocationListener, OnMapReadyCallback {
    companion object {

        private const val OPTION_GALLERY = 0
        private const val OPTION_MAKE_PHOTO = 1
        private const val PICKER_GALLERY = 1

        private const val PICKER_CAMERA = 2
        private const val EXTRA_CAMERA_DATA = "data"
    }

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    private val hueMap = mutableMapOf<Int, Float>() // category to hue
    private lateinit var viewModel: MapViewModel
    private var progressDialog: AlertDialog? = null
    private var googleMap: GoogleMap? = null
    private val callbacks = mutableMapOf<String, CallbackHolder>()
    private var latLongPicked: LatLng? = null
    private var stateRestored = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        PhotoScreenComponent.get(ctx).inject(this)
        viewModel = getViewModel(MapViewModel::class.java)
        return inflater.inflate(R.layout.screen_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fileLiveData.observe(this, Observer { filePath ->
            filePath?.let {
                progressDialog?.dismiss()
                PhotoViewEditActivity.IntentBuilder(ctx)
                    .withFile(filePath)
                    .withGeoPoint(latLongPicked)
                    .start()
            }
        })

        viewModel.listLiveData.observe(this, Observer { listResponse ->
            listResponse?.let {
                updateMarkers(it.list)
            }
        })

        viewModel.thumbLiveData.observe(this, Observer { response ->
            response?.let {
                val photoId = it.first
                callbacks[photoId]?.let { holder -> holder.callback(photoId, it.second) }
                callbacks.remove(photoId)
            }
        })
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        stateRestored = savedInstanceState != null
    }

    override fun onStart() { // TODO save marker selected and location picked for the case of
        super.onStart()

        if (!stateRestored) {
            viewModel.loadData()
        } else {
            stateRestored = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("save_instance", true)
    }

    override fun onMapReady(gMap: GoogleMap?) {
        gMap?.let {

            googleMap = gMap
            viewModel.listLiveData.value?.let { updateMarkers(it.list) }
            gMap.setInfoWindowAdapter(MarkerAdapter(ctx) { photoId, callback ->
                callbacks[photoId] = CallbackHolder(photoId, callback)
                viewModel.requestThumbnail(photoId)
            })
            gMap.setOnInfoWindowClickListener(::onMarkerPopupClick)
            gMap.setOnMapLongClickListener {
                latLongPicked = it
                startOptionsDialog()
            }
            gMap.uiSettings?.isMapToolbarEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICKER_GALLERY -> onGalleryResponse(resultCode, data)
            PICKER_CAMERA -> onCameraResponse(resultCode, data)
        }
    }

    override fun onAddPhotoClicked() {
        latLongPicked = null
        startOptionsDialog()
    }

    private fun updateMarkers(photoList: List<PhotoInfo>) {
        val gMap = googleMap
        gMap?.let {
            it.clear()
            for (photo in photoList) {
                val lat = photo.latitude
                val lon = photo.longitude
                if (lat != null && lon != null) {
                    val marker = gMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title(photo.description))
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(getCategoryHue(photo.category)))
                    marker.tag = photo
                }
            }
        }
    }

    override fun onLocationClicked() {
        Toast.makeText(ctx, "We will enable location soon.", Toast.LENGTH_SHORT).show()
    }

    private fun onCameraResponse(resultCode: Int, data: Intent?) {
        data?.extras?.let { extras ->
            if (resultCode == AppCompatActivity.RESULT_OK && extras.keySet().contains(EXTRA_CAMERA_DATA)) {
                val bitmap = extras[EXTRA_CAMERA_DATA] as Bitmap
                startProgressDialog()
                viewModel.saveTempFile(bitmap)
            }
        }
    }

    private fun onGalleryResponse(resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            val imageUri = data.data
            PhotoViewEditActivity.IntentBuilder(ctx)
                .withUri(imageUri)
                .withGeoPoint(latLongPicked)
                .start()
        }
    }

    private fun startOptionsDialog() {
        AlertDialog.Builder(ctx)
            .setTitle("Photo")
            .setAdapter(ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item).apply {
                addAll(ctx.resources.getStringArray(R.array.options_add_picture).asList())
            }) { dialog, which ->
                dialog.dismiss()
                onOptionPicked(which)
            }
            .setCancelable(true)
            .show()
    }

    private fun onOptionPicked(position: Int) {
        when (position) {
            OPTION_GALLERY -> startGalleryPick()
            OPTION_MAKE_PHOTO -> startCamera()
        }
    }

    private fun onMarkerPopupClick(marker: Marker) {
        val photo = marker.tag as PhotoInfo
        PhotoViewEditActivity.IntentBuilder(ctx)
            .withPhotoId(photo.id)
            .withGeoPoint(latLongPicked)
            .start()
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(requireActivity().packageManager)
        }
        startActivityForResult(intent, PICKER_CAMERA)
    }

    private fun startGalleryPick() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICKER_GALLERY)
    }

    private fun startProgressDialog() {
        progressDialog = AlertDialog.Builder(ctx)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .show()
    }

    private fun getCategoryHue(category: Int) = hueMap[category] ?: let {
        hue(ctx.resources.getColor(CategoryInfo.getMarkerColor(category))).also { hueMap[category] = it }
    }
}
