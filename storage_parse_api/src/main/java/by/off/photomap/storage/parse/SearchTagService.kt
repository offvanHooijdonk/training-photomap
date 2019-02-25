package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import by.off.photomap.model.TagInfo

interface SearchTagService {
    val tagLiveData: LiveData<List<String>>
    val searchHistoryLiveData: LiveData<List<TagInfo>>

    fun filter(text: String)
    fun filterSearchHistory(text: String, limit: Int? = null)
    fun addUserSearch(text: String)
    fun addSearchResults(list: List<String>)
}