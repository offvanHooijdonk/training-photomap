package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import by.off.photomap.core.utils.map
import by.off.photomap.core.utils.switchMap
import by.off.photomap.model.TagInfo
import by.off.photomap.storage.parse.SearchTagService
import javax.inject.Inject

class SearchTagViewModel @Inject constructor(private val searchTagService: SearchTagService) : ViewModel() {
    val searchLiveData = searchTagService.tagLiveData.switchMap { switchToLocalLD(it) }.map { onSearchResponse(it) }
    val historyLiveData = searchTagService.searchHistoryLiveData.switchMap { switchToLocalHistoryLD(it) }.map { onHistoryResponse(it) }

    private val switchLD = MutableLiveData<List<String>>()
    private val switchHistoryLD = MutableLiveData<List<TagInfo>>()

    val searchProgress = ObservableBoolean(false)
    val searchText = ObservableField<String>("")

    fun filterTags(text: String) {
        searchProgress.set(true)
        searchText.set(text)
        searchTagService.filter(text)
        searchTagService.addUserSearch(text)
    }

    fun searchHistory(text: String) {
        searchTagService.filterSearchHistory(text, if (text.isEmpty()) 5 else null)
    }

    fun resetData() {
        switchLD.value = null
        switchHistoryLD.value = null
    }

    private fun onHistoryResponse(list: List<TagInfo>?): List<TagInfo>? {
        return list
    }

    private fun onSearchResponse(list: List<String>?): List<String>? { // todo make search also return TagInfo?
        searchProgress.set(false)
        list?.let { searchTagService.addSearchResults(list) }
        return list
    }

    private fun switchToLocalLD(list: List<String>?) = switchLD.apply { value = list }
    private fun switchToLocalHistoryLD(list: List<TagInfo>?) = switchHistoryLD.apply { value = list }
}