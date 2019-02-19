package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

class TagViewModel : ViewModel() {
    val tagText = ObservableField<String>()
}