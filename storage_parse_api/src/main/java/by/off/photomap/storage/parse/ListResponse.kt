package by.off.photomap.storage.parse

import by.off.photomap.model.DataObject

data class ListResponse<T: DataObject>(val list: List<T>, val error: Exception? = null)