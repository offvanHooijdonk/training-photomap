package by.off.photomap.presentation.ui.timeline.search

import android.arch.lifecycle.ViewModel
import by.off.photomap.core.utils.map
import by.off.photomap.storage.parse.TagService
import javax.inject.Inject

class SearchTagViewModel @Inject constructor(tagService: TagService) : ViewModel() {
    val searchLiveData = tagService.tagLiveData.map { onResponse(it) }

    private fun onResponse(list: List<String>): List<String> {

        return list
    }
}