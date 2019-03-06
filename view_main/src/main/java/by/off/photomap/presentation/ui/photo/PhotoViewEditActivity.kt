package by.off.photomap.presentation.ui.photo

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import by.off.photomap.core.ui.*
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.PhotoEditBinding
import by.off.photomap.presentation.ui.photo.PhotoViewModel.MODE
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.act_photo_view_edit.*
import kotlinx.android.synthetic.main.layout_toolbar_photo_edit.*
import kotlinx.android.synthetic.main.layout_photo_form.*
import javax.inject.Inject

class PhotoViewEditActivity : BaseActivity() {
    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PHOTO_ID = "extra_photo_id"
        const val EXTRA_CAMERA_FILE = "extra_camera_file"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"

        private const val KEY_SAVED_INSTANCE = "key_saved_instance"
    }

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    private var mode: MODE = MODE.VIEW
    private lateinit var viewModel: PhotoViewModel
    private var enableSave = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PhotoScreenComponent.get(this).inject(this)
        viewModel = getViewModel(PhotoViewModel::class.java)

        initBindings()
        initModelsObserve()

        initToolbar()

        if (savedInstanceState == null) {
            setupData()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(KEY_SAVED_INSTANCE, true) // so that on screen rotation we do not call the ViewModel again
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (mode == MODE.VIEW) {
            menu?.findItem(R.id.item_save)?.isVisible = false
        } else {
            menu?.findItem(R.id.item_save)?.isVisible = true
            menu?.findItem(R.id.item_save)?.isEnabled = enableSave
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> handleBack()
            R.id.item_save -> {
                viewModel.save()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_view_edit, menu)
        return true
    }

    override fun onBackPressed() {
        handleBack()
    }

    private fun setupData() {
        val uri = intent.extras?.getParcelable<Uri>(EXTRA_IMAGE_URI)
        val passedId = intent.extras?.getString(EXTRA_PHOTO_ID)
        val passedFile = intent.extras?.getString(EXTRA_CAMERA_FILE)
        val lat = intent.extras?.getDouble(EXTRA_LATITUDE)
        val lon = intent.extras?.getDouble(EXTRA_LONGITUDE)

        Log.i(LOGCAT, "Photo Edit start uri=$uri , ID=$passedId , file=$passedFile")

        val latLong = if (lat != null && lon != null) lat to lon else null
        when {
            uri != null -> loadByUri(uri, latLong)
            passedId != null -> loadById(passedId)
            passedFile != null -> loadByCameraPhoto(passedFile, latLong)
            else -> {
                Snackbar.make(progressSaving, R.string.no_data_provided, Snackbar.LENGTH_INDEFINITE).colorError().show()
            }
        }
    }

    private fun initBindings() {
        val binding = DataBindingUtil.setContentView<PhotoEditBinding>(this, R.layout.act_photo_view_edit)
        binding.model = viewModel
    }

    private fun initModelsObserve() {
        viewModel.liveData.observe(this, EmptyObserver())
        viewModel.modeLiveData.observe(this, Observer { leaveScreen ->
            leaveScreen?.let {
                when (it) {
                    MODE.CLOSE -> finish()
                    MODE.CREATE -> mode = it.also {
                        initCategoriesList()
                        invalidateOptionsMenu()
                    }
                    MODE.EDIT -> mode = it.also {
                        initCategoriesList()
                        invalidateOptionsMenu()
                    }
                    MODE.VIEW -> mode = it.also { invalidateOptionsMenu() }
                }
            }
        })
        viewModel.loadImageLiveData.observe(this, EmptyObserver())
        viewModel.saveEnableLiveData.observe(this, Observer { enable ->
            enableSave = enable ?: true
            invalidateOptionsMenu()
        })
        viewModel.fileLiveData.observe(this, EmptyObserver())
        viewModel.handleBackLiveData.observe(this, Observer { isHandle ->
            isHandle?.let {
                if (isHandle) {
                    startConfirmLeaveDialog()
                } else {
                    finish()
                }
            }
        })
    }

    private fun initCategoriesList() {
        spinnerCategories.adapter = ArrayAdapter<String>(
            ctx,
            android.R.layout.simple_list_item_1,
            CategoryInfo.getTitlesOrdered().map { ctx.getString(it) }
        )
    }

    private fun loadByUri(uri: Uri, latLong: Pair<Double, Double>?) {
        viewModel.setupWithUri(uri, latLong)
    }

    private fun loadById(id: String) {
        viewModel.setupWithPhotoById(id)
    }

    private fun loadByCameraPhoto(filePath: String, latLong: Pair<Double, Double>?) {
        viewModel.setupWithFile(filePath, latLong)
    }

    private fun handleBack() {
        viewModel.onBackRequested()
    }

    private fun startConfirmLeaveDialog() {
        AlertDialog.Builder(ctx)
            .setTitle(R.string.dialog_confirm_title)
            .setMessage(R.string.dialog_confirm_cancel_photo)
            .setPositiveButton(R.string.dialog_btn_discard) { _, _ -> this@PhotoViewEditActivity.finish() }
            .setNegativeButton(R.string.dialog_btn_stay, null)
            .show()
    }

    private fun initToolbar() {
        when {
            isPortrait() -> {
                setSupportActionBar(toolbar)
                title = null
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            isLandscape() -> {
                fabSave?.setOnClickListener { viewModel.save() }
                imgClose?.setOnClickListener { handleBack() }
            }
        }
    }

    class IntentBuilder(private val ctx: Context) {
        private val intent: Intent = Intent(ctx, PhotoViewEditActivity::class.java)
        private var requiredSet = false

        fun withFile(filePath: String) = intent.let { it.putExtra(EXTRA_CAMERA_FILE, filePath); requiredSet = true; this }

        fun withPhotoId(photoId: String) = intent.let { it.putExtra(EXTRA_PHOTO_ID, photoId); requiredSet = true; this }

        fun withUri(imageUri: Uri) = intent.let { it.putExtra(EXTRA_IMAGE_URI, imageUri); requiredSet = true; this }

        fun withGeoPoint(latitude: Double, longitude: Double) = intent.let {
            it.putExtra(EXTRA_LATITUDE, latitude)
            it.putExtra(EXTRA_LONGITUDE, longitude)
            this
        }

        fun withGeoPoint(latLong: LatLng?) = this.apply {
            latLong?.let {
                withGeoPoint(it.latitude, it.longitude)
            }
        }

        fun start() {
            if (requiredSet)
                ctx.startActivity(intent)
            else
                Toast.makeText(ctx, R.string.intent_start_no_data, Toast.LENGTH_LONG).show()
        }
    }
}