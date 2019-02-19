package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData

interface TagService {
    val tagLiveData: LiveData<List<String>>
}