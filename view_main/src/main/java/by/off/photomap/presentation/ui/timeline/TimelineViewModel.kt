package by.off.photomap.presentation.ui.timeline

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.map
import by.off.photomap.model.PhotoInfo
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import javax.inject.Inject

class TimelineViewModel @Inject constructor(private val photoService: PhotoService) : ViewModel() {
    val liveData = photoService.serviceListLiveData.map { onData(it) }
    val isRefreshing = ObservableBoolean(false)
    val isShowList = ObservableBoolean(true)
    val isShowEmptyView = ObservableBoolean(false)

    val listData = ObservableArrayList<PhotoInfo>()

    var tagFilter: String = ""
        set(value) = tagFilterValue.set(value)
    val tagFilterValue = ObservableField<String>("")

    init {
        isRefreshing.set(true)
        photoService.listOrderTime()
    }

    fun loadData() {
        isRefreshing.set(true)
        isShowList.set(false)
        isShowEmptyView.set(false)
        photoService.filterTag = tagFilterValue.get().takeUnless { it == null || it.isEmpty() }
        photoService.listOrderTime()
    }

    private fun onData(response: ListResponse<PhotoInfo>): ListResponse<PhotoInfo> {
        isRefreshing.set(false)
        listData.clear()
        listData.addAll(response.list)

        if (listData.isEmpty()) {
            isShowList.set(false)
            isShowEmptyView.set(true)
        } else {
            isShowEmptyView.set(false)
            isShowList.set(true)
        }

        return response
    }
}