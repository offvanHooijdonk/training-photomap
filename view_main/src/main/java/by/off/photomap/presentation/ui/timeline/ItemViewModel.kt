package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import by.off.photomap.model.PhotoInfo

class ItemViewModel(photoInfo: PhotoInfo?, itemType: Int) : ViewModel() {
    companion object {
        const val TYPE_JUST_DATA = 0
        const val TYPE_WITH_PERIOD = 1
    }

    var showMonth = ObservableBoolean()
    val photo = ObservableField<PhotoInfo>()
    val type = ObservableInt()

    // region Vars
    var photoInfo: PhotoInfo?
        get() = photo.get()
        set(value) = photo.set(value)

    var itemType: Int
        get() = type.get()
        set(value) {
            type.set(value)
            showMonth.set(value == TYPE_WITH_PERIOD)
        }

    init {
        this.photoInfo = photoInfo
        this.itemType = itemType
    }
    // endregion
}