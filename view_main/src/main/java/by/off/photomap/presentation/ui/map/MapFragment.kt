package by.off.photomap.presentation.ui.map

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.viewmodel.MapViewModel
import javax.inject.Inject

class MapFragment : BaseFragment(), MainActivity.ButtonPhotoListener, MainActivity.ButtonLocationListener {
    companion object {
        private const val OPTION_GALLERY = 0
        private const val OPTION_MAKE_PHOTO = 1

        private const val PICKER_GALLERY = 1
        private const val PICKER_CAMERA = 2
        private const val EXTRA_CAMERA_DATA = "data"
    }

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MapViewModel

    private var progressDialog: AlertDialog? = null

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
                Log.i(LOGCAT, "Saved to temp file: $filePath")
                // todo start photo activity here
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICKER_GALLERY -> onGalleryResponse(resultCode, data)
            PICKER_CAMERA -> onCameraResponse(resultCode, data)
        }
    }

    override fun onPhotoClicked() {
        showOptionsDialog()
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
            startActivity(Intent(ctx, PhotoViewEditActivity::class.java).apply { putExtra(PhotoViewEditActivity.EXTRA_IMAGE_URI, imageUri) })
        }
    }

    private fun showOptionsDialog() {
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
}
