package by.off.photomap.storage.parse

import by.off.photomap.model.DataObject

data class Response<out T : DataObject>(val data: T? = null, val error: Exception? = null)