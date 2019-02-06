package by.off.photomap.presentation.ui.map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.ui.photo.PhotoViewEditActivity
import by.off.photomap.presentation.ui.R

class MapFragment : Fragment(), MainActivity.ButtonPhotoListener, MainActivity.ButtonLocationListener {
    companion object {
        const val OPTION_GALLERY = 0
        const val OPTION_MAKE_PHOTO = 1

        const val PICKER_GALLERY = 1
    }

    private lateinit var ctx: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.screen_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICKER_GALLERY -> onGalleryResponse(resultCode, data)
        }
    }

    override fun onPhotoClicked() {
        showOptionsDialog()
    }

    override fun onLocationClicked() {
        Toast.makeText(ctx, "We will enable location soon.", Toast.LENGTH_SHORT).show()
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
            OPTION_GALLERY -> {
                startGalleryPick()
            }
            OPTION_MAKE_PHOTO -> {
                Toast.makeText(ctx, "We wanna make a selfie, ain't we?", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGalleryPick() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(intent, PICKER_GALLERY)
    }
}
