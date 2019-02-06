package by.off.photomap.presentation.ui.photo

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import by.off.photomap.core.ui.BaseActivity
import by.off.photomap.core.ui.colorError
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ActPhotoViewEditBinding
import by.off.photomap.presentation.viewmodel.photo.PhotoViewModel
import by.off.photomap.presentation.viewmodel.photo.PhotoViewModel.MODE
import kotlinx.android.synthetic.main.act_photo_view_edit.*
import kotlinx.android.synthetic.main.include_collapsible_toolbar.*
import javax.inject.Inject

class PhotoViewEditActivity : BaseActivity() {
    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PHOTO_ID = "extra_photo_id"
        const val EXTRA_CAMERA_FILE = "extra_camera_file"

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
        when {
            uri != null -> loadByUri(uri)
            passedId != null -> loadById(passedId)
            passedFile != null -> loadByCameraPhoto(passedFile)
            else -> {
                Snackbar.make(progressSaving, R.string.no_data_provided, Snackbar.LENGTH_INDEFINITE).colorError().show()
            }
        }
    }

    private fun initBindings() {
        val binding = DataBindingUtil.setContentView<ActPhotoViewEditBinding>(this, R.layout.act_photo_view_edit)
        binding.model = viewModel
    }

    private fun initModelsObserve() {
        viewModel.liveData.observe(this, Observer { })
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
        viewModel.loadImageLiveData.observe(this, Observer { })
        viewModel.saveEnableLiveData.observe(this, Observer { enable ->
            enableSave = enable ?: true
            invalidateOptionsMenu()
        })
        viewModel.fileLiveData.observe(this, Observer { })
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

    private fun loadByUri(uri: Uri) {
        viewModel.setupWithUri(uri)
    }

    private fun loadById(id: String) {
        viewModel.setupWithPhotoById(id)
    }

    private fun loadByCameraPhoto(filePath: String) {
        viewModel.setupWithFile(filePath)
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
        setSupportActionBar(toolbar)
        title = null
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}