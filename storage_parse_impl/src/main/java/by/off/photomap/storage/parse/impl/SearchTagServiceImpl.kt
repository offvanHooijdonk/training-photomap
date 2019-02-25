package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.TagInfo
import by.off.photomap.storage.parse.SearchTagService
import com.parse.ParseObject
import com.parse.ParseQuery
import off.photomap.dao.room.entities.HistoryBean
import off.photomap.dao.room.entities.HistoryDao
import java.util.*
import javax.inject.Inject

@PerFeature
class SearchTagServiceImpl @Inject constructor(private val historyDao: HistoryDao) : SearchTagService {
    override val tagLiveData: LiveData<List<String>>
        get() = tagLD
    override val searchHistoryLiveData: LiveData<List<TagInfo>>
        get() = searchHistoryLD

    private val tagLD = MutableLiveData<List<String>>()
    private val searchHistoryLD = MutableLiveData<List<TagInfo>>()

    override fun filter(text: String) {
        launchScopeIO {
            val list = ParseQuery.getQuery<ParseObject>(TagInfo.TABLE).whereContains(TagInfo.TAG_TITLE, text.toLowerCase()).setLimit(20).find()

            val tags = list.mapNotNull { it.getString(TagInfo.TAG_TITLE) }.distinct()

            tagLD.postValue(tags)
        }
    }

    override fun filterSearchHistory(text: String, limit: Int?) {
        launchScopeIO {
            val list = if (limit != null) {
                historyDao.find(text, limit)
            } else {
                historyDao.find(text)
            }
            searchHistoryLD.postValue(convertToTagInfo(list))
        }
    }

    override fun addUserSearch(text: String) {
        launchScopeIO {
            historyDao.add(HistoryBean(text, HistoryBean.TYPE_HISTORY, Date().time))
        }
    }

    override fun addSearchResults(list: List<String>) {
        launchScopeIO {
            val timeStamp = Date().time
            val historyList = list.map { HistoryBean(it, HistoryBean.TYPE_TAG, timeStamp) }
            historyDao.addAll(historyList)
        }
    }

    private fun convertToTagInfo(list: List<HistoryBean>) = list.map {
        TagInfo(
            it.entryText,
            when (it.type) {
                HistoryBean.TYPE_HISTORY -> TagInfo.TYPE_HISTORY
                else -> TagInfo.TYPE_TAG
            }
        )
    }
}