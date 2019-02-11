package by.off.photomap.presentation.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

class MapFragment : BaseFragment(), MainActivity.ButtonPhotoListener, MainActivity.ButtonLocationListener, OnMapReadyCallback {
    companion object {
        /*private const val OPTION_GALLERY = 0
        private const val OPTION_MAKE_PHOTO = 1*/
        private const val PICKER_GALLERY = 1
        private const val PICKER_CAMERA = 2
        private const val EXTRA_CAMERA_DATA = "data"
        private const val PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

        private const val DEFAULT_ZOOM = 15.0f
        private const val CAMERA_ANIM_DURATION = 650
        private const val LOCATION_REQUEST_INTERVAL = 2000L
    }

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    private val locationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(ctx) }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.let {
                moveCameraToCurrent(LatLng(it.lastLocation.latitude, it.lastLocation.longitude))
            }
        }
    }
    private val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(LOCATION_REQUEST_INTERVAL)
    private val hueMap = mutableMapOf<Int, Float>() // category to hue
    private lateinit var viewModel: MapViewModel
    private var progressDialog: AlertDialog? = null
    private var googleMap: GoogleMap? = null
    private var dialogAddPhoto: DialogFragment? = null
    private val callbacks = mutableMapOf<String, CallbackHolder>()
    private var latLongCurrent: LatLng? = null
    private var workingLocation: LatLng? = null
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
                    .withGeoPoint(workingLocation)
                    .start()
                workingLocation = null
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
                callbacks[photoId]?.callback?.invoke(photoId, it.second)
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
        outState.putBoolean("save_instance", true) // todo save current position, zoom, mode etc.
    }

    override fun onMapReady(gMap: GoogleMap?) {
        gMap?.also {

            googleMap = gMap
            viewModel.listLiveData.value?.let { updateMarkers(it.list) }
            gMap.setInfoWindowAdapter(MarkerAdapter(ctx) { photoId, callback ->
                callbacks[photoId] = CallbackHolder(photoId, callback)
                viewModel.requestThumbnail(photoId)
            })
            gMap.setOnInfoWindowClickListener(::onMarkerPopupClick)
            gMap.setOnMapLongClickListener {
                workingLocation = it
                startLocationDialog(it)
            }
            gMap.uiSettings?.isMapToolbarEnabled = false

            checkAndRequestLocationPermission(false)
            gMap.setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    (activity as MainActivity).setNavigationButtonMode(false)
                    locationClient.removeLocationUpdates(locationCallback)
                }
            }
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
        val latLong = latLongCurrent
        if (latLong != null) {
            workingLocation = latLong
            startLocationDialog(latLong)
        } else {
            Snackbar.make((activity as MainActivity).snackbarRoot, R.string.location_unknown_try_manual, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun startLocationDialog(geoPoint: LatLng) {
        workingLocation?.let {
            dialogAddPhoto = AddPhotoBottomSheet.createNewDialog(it)
                .apply {
                    onAddClicked = ::onOptionPicked
                    placeLiveData = viewModel.placeLiveData
                }
            dialogAddPhoto?.show(childFragmentManager, "")
            viewModel.loadPlaceInfo(geoPoint)
        }
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

    private fun checkAndRequestLocationPermission(force: Boolean): Boolean {
        val granted = checkPermission(PERMISSION_LOCATION)
        if (granted) {
            goToCurrentLocation()
        } else {
            requestPermission(PERMISSION_LOCATION, force, { showPermissionExplanation() }) { isGranted ->
                if (isGranted) goToCurrentLocation()
            }
        }

        return granted
    }

    @SuppressLint("MissingPermission")
    private fun goToCurrentLocation() {
        locationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val loc = task.result
                if (loc != null) {
                    (activity as MainActivity).setNavigationButtonMode(true)
                    googleMap?.run {
                        isMyLocationEnabled = true
                        uiSettings?.isMyLocationButtonEnabled = false
                        moveCameraToCurrent(LatLng(loc.latitude, loc.longitude))
                    }
                }
            }
        }

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, null
        )
    }

    private fun moveCameraToCurrent(latLong: LatLng) {
        googleMap?.apply {
            latLongCurrent = latLong
            var zoom = googleMap?.cameraPosition?.zoom ?: DEFAULT_ZOOM
            zoom = Math.max(zoom, DEFAULT_ZOOM)
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoom), CAMERA_ANIM_DURATION, null)
        }
    }

    override fun onLocationClicked() {
        if (checkAndRequestLocationPermission(false)) {
            goToCurrentLocation()
        }
    }

    private fun onCameraResponse(resultCode: Int, data: Intent?) {
        if (data?.extras == null) workingLocation = null
        data?.extras?.let { extras ->
            dialogAddPhoto?.dismiss()
            if (resultCode == AppCompatActivity.RESULT_OK && extras.keySet().contains(EXTRA_CAMERA_DATA)) {
                val bitmap = extras[EXTRA_CAMERA_DATA] as Bitmap
                startProgressDialog()
                viewModel.saveTempFile(bitmap)
            }
        }
    }

    private fun onGalleryResponse(resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            dialogAddPhoto?.dismiss()
            val imageUri = data.data
            PhotoViewEditActivity.IntentBuilder(ctx)
                .withUri(imageUri)
                .withGeoPoint(workingLocation)
                .start()
            workingLocation = null
        } else {
            workingLocation = null
        }
    }

    /*private fun startOptionsDialog() {
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
    }*/

    private fun onOptionPicked(position: Int) {
        when (position) {
            AddPhotoBottomSheet.OPTION_GALLERY -> startGalleryPick()
            AddPhotoBottomSheet.OPTION_MAKE_PHOTO -> startCamera()
        }
    }

    private fun onMarkerPopupClick(marker: Marker) {
        val photo = marker.tag as PhotoInfo
        PhotoViewEditActivity.IntentBuilder(ctx)
            .withPhotoId(photo.id)
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

    private fun showPermissionExplanation() {
        Snackbar.make((activity as MainActivity).snackbarRoot, R.string.permission_explanation, Snackbar.LENGTH_LONG)
            .setAction("Grant") { checkAndRequestLocationPermission(true) }
            .show()
    }

    private fun getCategoryHue(category: Int) = hueMap[category] ?: let {
        hue(ctx.resources.getColor(CategoryInfo.getMarkerColor(category))).also { hueMap[category] = it }
    }
}
