package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import by.off.photomap.core.utils.map
import by.off.photomap.core.utils.switchMap
import by.off.photomap.storage.parse.TagService
import javax.inject.Inject

class SearchTagViewModel @Inject constructor(private val tagService: TagService) : ViewModel() {
    val searchLiveData = tagService.tagLiveData.switchMap { switchToLocalLD(it) }.map { onResponse(it) }
    private val switchLD = MutableLiveData<List<String>>()

    val searchProgress = ObservableBoolean(false)
    val searchText = ObservableField<String>("")

    fun filterTags(text: String) {
        searchProgress.set(true)
        searchText.set(text)
        tagService.filter(text)
    }

    fun resetData() {
        switchLD.value = null
    }

    private fun onResponse(list: List<String>?): List<String>? {
        searchProgress.set(false)
        return list
    }

    private fun switchToLocalLD(list: List<String>?) = switchLD.apply { value = list }
}