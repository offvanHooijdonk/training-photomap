package by.off.photomap.presentation.ui.map

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.ui.R

class MapFragment : Fragment(), MainActivity.ButtonPhotoListener, MainActivity.ButtonLocationListener {
    companion object {
        const val OPTION_GALLERY = 0
        const val OPTION_MAKE_PHOTO = 1
    }

    private lateinit var ctx: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.screen_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()
    }

    override fun onPhotoClicked() {
        showOptionsDialog()
    }

    override fun onLocationClicked() {
        Toast.makeText(ctx, "We will enable location soon.", Toast.LENGTH_SHORT).show()
    }

    private fun showOptionsDialog() {
        AlertDialog.Builder(ctx)
            .setTitle("Photo")
            .setAdapter(ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item).apply {
                addAll("Choose from Gallery", "Take a picture")
            }) { dialog, which ->
                dialog.dismiss()
                onOptionPicked(which)
            }
            .setCancelable(true)
            .show()
    }

    private fun onOptionPicked(position: Int) {
        when(position) {
            OPTION_GALLERY -> {
                Toast.makeText(ctx, "From Gallery?", Toast.LENGTH_SHORT).show()
            }
            OPTION_MAKE_PHOTO -> {
                Toast.makeText(ctx, "We wanna make a selfie, ain't we?", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
