package by.off.photomap.core.ui

data class CallbackHolder(val photoId: String, val callback: (photoId: String, filePath: String?) -> Unit)