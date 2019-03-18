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
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.ui.getColorVal
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
        private const val PICKER_GALLERY = 1
        private const val PICKER_CAMERA = 2
        private const val EXTRA_CAMERA_DATA = "data"
        private const val PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

        private const val EXTRA_SAVED_CURRENT_LOCATION_MODE = "EXTRA_SAVED_CURRENT_LOCATION_MODE"
        private const val EXTRA_SAVED_MARKER_OPENED = "EXTRA_SAVED_MARKER_OPENED"

        private const val DEFAULT_ZOOM = 15.0f
        private const val CAMERA_ANIM_DURATION = 650
        private const val LOCATION_REQUEST_INTERVAL = 2000L
        private const val TAG_DIALOG_ADD_PHOTO = "DIALOG_ADD_PHOTO"
    }

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MapViewModel
    // locations
    private val locationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(ctx) }
    // callbacks
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.let {
                moveCameraToCurrent(LatLng(it.lastLocation.latitude, it.lastLocation.longitude))
            }
        }
    }
    private val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(LOCATION_REQUEST_INTERVAL)
    // map
    private var googleMap: GoogleMap? = null
    private var markerIdPicked: String? = null
    private var latLongCurrent: LatLng? = null
    private var workingLocation: LatLng? = null
    private var isCurrentLocationMode = false
    // views
    private var progressDialog: AlertDialog? = null
    private var dialogAddPhoto: AddPhotoBottomSheet? = null
    // helpers
    private val markers = mutableListOf<Marker>()
    private val hueMap = mutableMapOf<Int, Float>() // category to hue

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        PhotoScreenComponent.get(ctx).inject(this)
        viewModel = getViewModel(MapViewModel::class.java)
        return inflater.inflate(R.layout.screen_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLiveData()

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        markerIdPicked = savedInstanceState?.getString(EXTRA_SAVED_MARKER_OPENED)
        isCurrentLocationMode = savedInstanceState?.getBoolean(EXTRA_SAVED_CURRENT_LOCATION_MODE) ?: true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_SAVED_MARKER_OPENED, markerIdPicked)
        outState.putBoolean(EXTRA_SAVED_CURRENT_LOCATION_MODE, isCurrentLocationMode)
    }

    override fun onMapReady(gMap: GoogleMap?) {
        gMap?.also {
            googleMap = gMap
            viewModel.listLiveData.value?.let { updateMarkers(it.list) }


            gMap.setInfoWindowAdapter(MarkerAdapter(ctx))
            gMap.uiSettings?.isMapToolbarEnabled = false

            markers.firstOrNull { marker -> markerIdPicked == (marker.tag as PhotoInfo).id }?.showInfoWindow()
            markers.clear()
            gMap.setOnMarkerClickListener { marker ->
                markerIdPicked = (marker.tag as PhotoInfo?)?.id
                false
            }

            gMap.setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    (activity as MainActivity).setNavigationButtonMode(false)
                    locationClient.removeLocationUpdates(locationCallback)
                    isCurrentLocationMode = false
                }
            }
            gMap.setOnInfoWindowClickListener(::onMarkerPopupClick)
            gMap.setOnMapLongClickListener {
                workingLocation = it
                startPhotoOnLocationDialog(it)
            }

            checkAndRequestLocationPermission(false)
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
            startPhotoOnLocationDialog(latLong)
        } else {
            Snackbar.make((activity as MainActivity).snackbarRoot, R.string.location_unknown_try_manual, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onLocationClicked() {
        isCurrentLocationMode = true
        checkAndRequestLocationPermission(false)
    }

    fun onAddPhotoDialogCreated(addPhotoBottomSheet: AddPhotoBottomSheet) {
        dialogAddPhoto = addPhotoBottomSheet
        dialogAddPhoto?.onAddClicked = ::onOptionPicked
    }

    private fun initLiveData() {
        viewModel.fileLiveData.observe(this, Observer { filePath -> onCameraImageSaved(filePath) })// saving Camera image

        viewModel.listLiveData.observe(this, Observer { listResponse ->
            listResponse?.let {
                updateMarkers(it.list)
            }
        })
    }

    private fun startPhotoOnLocationDialog(geoPoint: LatLng) {
        dialogAddPhoto = AddPhotoBottomSheet.createNewDialog(geoPoint).also { dialog ->
            dialog.show(childFragmentManager, TAG_DIALOG_ADD_PHOTO)
        }
    }

    // region Markers
    private fun updateMarkers(photoList: List<PhotoInfo>) {
        val gMap = googleMap
        gMap?.let {
            it.clear()
            markers.clear()
            for (photo in photoList) {
                val lat = photo.latitude
                val lon = photo.longitude
                if (lat != null && lon != null) {
                    val marker = gMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title(photo.description))
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(getCategoryHue(photo.category)))
                    marker.tag = photo
                    markers.add(marker)
                }
            }
        }
    }

    private fun onMarkerPopupClick(marker: Marker) {
        val photo = marker.tag as PhotoInfo
        PhotoViewEditActivity.IntentBuilder(ctx)
            .withPhotoId(photo.id)
            .start()
    }
    // endregion Markers

    // region Map Navigation
    @SuppressLint("MissingPermission")
    private fun goToCurrentLocation() {
        locationClient.lastLocation.addOnCompleteListener { task ->
            val loc = task.result
            if (task.isSuccessful && loc != null) {
                (activity as? MainActivity)?.setNavigationButtonMode(true)
                googleMap?.run {
                    isMyLocationEnabled = true
                    uiSettings?.isMyLocationButtonEnabled = false
                    moveCameraToCurrent(LatLng(loc.latitude, loc.longitude))
                }
            } else {
                (activity as? MainActivity)?.setNavigationButtonMode(false)
            }
        }

        locationClient.locationAvailability.addOnCompleteListener { task ->
            val result = task.result
            if (task.isSuccessful && result != null && !result.isLocationAvailable) {
                locationClient.flushLocations()
                showLocationNotEnabled()
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

    private fun showLocationNotEnabled() {
        Snackbar.make((activity as MainActivity).snackbarRoot, "Please check if your location is enabled.", Snackbar.LENGTH_LONG)
            .show()
    }
    // endregion Map Navigation

    // region Camera/Gallery callbacks
    private fun onCameraImageSaved(filePath: String?) {
        filePath?.let {
            progressDialog?.dismiss()
            PhotoViewEditActivity.IntentBuilder(ctx)
                .withFile(filePath)
                .withGeoPoint(workingLocation)
                .start()
            workingLocation = null
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
        val imageUri = data?.data
        if (resultCode == AppCompatActivity.RESULT_OK && imageUri != null) {
            dialogAddPhoto?.dismiss()
            PhotoViewEditActivity.IntentBuilder(ctx)
                .withUri(imageUri)
                .withGeoPoint(workingLocation)
                .start()

        }
        workingLocation = null
    }

    private fun onOptionPicked(position: Int) {
        when (position) {
            AddPhotoBottomSheet.OPTION_GALLERY -> startGalleryPick()
            AddPhotoBottomSheet.OPTION_MAKE_PHOTO -> startCamera()
        }
    }
    // endregion Camera/Gallery callbacks

    private fun checkAndRequestLocationPermission(force: Boolean): Boolean {
        val granted = checkPermission(PERMISSION_LOCATION)
        if (granted) {
            if (isCurrentLocationMode) goToCurrentLocation()
        } else {
            requestPermission(PERMISSION_LOCATION, force, { showPermissionExplanation() }) { isGranted ->
                if (isGranted && isCurrentLocationMode) goToCurrentLocation()
            }
        }

        return granted
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
            .setAction(R.string.grant_permission_cta) { checkAndRequestLocationPermission(true) }
            .show()
    }

    private fun getCategoryHue(category: Int) = hueMap[category] ?: let {
        hue(ctx.getColorVal(CategoryInfo.getMarkerColor(category))).also { hueMap[category] = it }
    }
}
