package by.off.photomap.presentation.ui.photo

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import by.off.photomap.core.ui.colorError
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.ActPhotoViewEditBinding
import by.off.photomap.presentation.viewmodel.photo.PhotoViewModel
import kotlinx.android.synthetic.main.act_photo_view_edit.*
import java.io.File
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class PhotoViewEditActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PHOTO_ID = "extra_photo_id"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var ctx: Context
    private lateinit var mode: MODE
    private lateinit var viewModel: PhotoViewModel // todo use default value insted
    private var enableSave = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
// todo slice into init methods
        PhotoScreenComponent.get(this).inject(this)
        viewModel = viewModelFactory.create(PhotoViewModel::class.java)

        val binding = DataBindingUtil.setContentView<ActPhotoViewEditBinding>(this, R.layout.act_photo_view_edit)
        binding.model = viewModel

        viewModel.liveData.observe(this, Observer { leaveScreen ->
            leaveScreen?.let { if (it) finish() }
        })
        viewModel.loadImageLiveData.observe(this, Observer { })
        viewModel.errorLiveData.observe(this, Observer { error ->
            error?.let { Snackbar.make(progressSaving, it.message ?: "Unknown", Snackbar.LENGTH_INDEFINITE).colorError().show() }
        })
        viewModel.saveEnableLiveData.observe(this, Observer { enable ->
            enableSave = enable ?: true
            invalidateOptionsMenu()
        })
        viewModel.fileLiveData.observe(this, Observer { })

        setSupportActionBar(toolbar)
        title = null
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val uri = intent.extras?.getParcelable<Uri>(EXTRA_IMAGE_URI)
        val passedId = intent.extras?.getString(EXTRA_PHOTO_ID)
        when {
            uri != null -> loadByUri(uri)
            passedId != null -> loadById(passedId)
            else -> Unit // todo handle
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.item_save)?.isEnabled = enableSave

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

    private fun loadByUri(uri: Uri) {
        mode = MODE.CREATE
        spinnerCategories.adapter = ArrayAdapter<String>(
            ctx,
            android.R.layout.simple_list_item_1,
            CategoryInfo.getTitlesOrdered().map { ctx.getString(it) }
        )

        viewModel.setupWithUri(uri)
    }

    private fun loadById(id: String) {
        mode = MODE.VIEW

        viewModel.loadById(id)
    }

    private fun handleBack() {
        if (mode == MODE.CREATE || mode == MODE.EDIT) {
            AlertDialog.Builder(ctx)
                .setTitle(R.string.dialog_confirm_title)
                .setMessage(R.string.dialog_confirm_cancel_photo)
                .setPositiveButton(R.string.dialog_btn_discard) { _, _ -> this@PhotoViewEditActivity.finish() }
                .setNegativeButton(R.string.dialog_btn_stay, null)
                .show()
        } else {
            finish()
        }
    }

    private enum class MODE {
        CREATE, EDIT, VIEW
    }
}

/*@BindingAdapter("android:src")
fun setImageUri(imageView: ImageView, uri: Uri?) {
    uri?.let { imageView.setImageURI(uri) }
}*/

@BindingAdapter("timestamp")
fun setPhotoTimestamp(textView: TextView, date: Date?) {
    textView.text = if (date != null) DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) else null
}

@BindingAdapter("error")
fun setTextInpuLayoutError(til: TextInputLayout, errorMessage: String?) {
    til.error = errorMessage
}

@BindingAdapter("category")
fun setChipCategoryLabelColor(chip: Chip, categoryId: Int) {
    chip.setText(CategoryInfo.getTitleRes(categoryId) ?: R.string.label_category_error)
    //val catColor = chip.context.resources.getColor(CategoryInfo.getColorRes(categoryId) ?: R.color.category_unknown)
    chip.setChipBackgroundColorResource(CategoryInfo.getColorRes(categoryId) ?: R.color.category_unknown)
}

@BindingAdapter("filePath")
fun setImageFile(imageView: ImageView, filePath: String?) {
    filePath?.let {
        val file = File(filePath)
        if (file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath))
        } else {
            // todo show error?
        }
    }
}