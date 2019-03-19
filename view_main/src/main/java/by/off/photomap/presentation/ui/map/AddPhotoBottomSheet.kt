package by.off.photomap.presentation.ui.map

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.off.photomap.core.ui.BaseFragment
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.getViewModel
import by.off.photomap.core.ui.isLandscape
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.PhotoScreenComponent
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.databinding.DialogAddPhotoBinding
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.dialog_add_photo.*
import javax.inject.Inject

class AddPhotoBottomSheet : BottomSheetDialogFragment() {
    var onAddClicked: ((option: Int) -> Unit)? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @VisibleForTesting
    lateinit var viewModel: AddPhotoDialogViewModel
    private val latLong: LatLng? by lazy { arguments?.getParcelable<LatLng>(ARG_GEO_POINT) }

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
        PhotoScreenComponent.get(ctx).inject(this)
        viewModel = getViewModel(viewModelFactory, AddPhotoDialogViewModel::class.java)
        val binding = DataBindingUtil.inflate<DialogAddPhotoBinding>(inflater, R.layout.dialog_add_photo, container, false)
        binding.model = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (latLong == null) {
            dismiss()
            return
        }
        latLong?.also {
            val parent = parentFragment
            if (parent is MapFragment) parent.onAddPhotoDialogCreated(this)

            viewModel.loadPlaceInfo(it)

            txtFromGallery.setOnClickListener { onAddClicked?.invoke(OPTION_GALLERY) }
            txtFromCamera.setOnClickListener { onAddClicked?.invoke(OPTION_MAKE_PHOTO) }

            viewModel.placeLiveData.observe(this, Observer {})

            dialog.setOnShowListener { adjustSheetPeek() }
        }
    }

    private fun adjustSheetPeek() {
        if (ctx.isLandscape()) {
            (view?.parent as View?)?.let {
                BottomSheetBehavior.from(it).peekHeight = it.height
            }
        }
    }
}