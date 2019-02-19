package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.TagInfo
import by.off.photomap.storage.parse.TagService
import com.parse.ParseObject
import com.parse.ParseQuery
import javax.inject.Inject

@PerFeature
class TagServiceImpl @Inject constructor() : TagService {
    override val tagLiveData: LiveData<List<String>>
        get() = tagLD

    private val tagLD = MutableLiveData<List<String>>()

    fun filter(text: String) {
        launchScopeIO {
            val list = ParseQuery.getQuery<ParseObject>(TagInfo.TABLE).whereContains(TagInfo.TAG_TITLE, text.toLowerCase()).setLimit(20).find()

            val tags = list.mapNotNull { it.getString(TagInfo.TAG_TITLE) }.distinct()

            tagLD.postValue(tags)
        }
    }
}