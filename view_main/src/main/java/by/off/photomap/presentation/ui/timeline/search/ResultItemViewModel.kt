package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

class ResultItemViewModel : ViewModel() {
    val valueText = ObservableField<String>("")
    val searchText = ObservableField<String>("")
}