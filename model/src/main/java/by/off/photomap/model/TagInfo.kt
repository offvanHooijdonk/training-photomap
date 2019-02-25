package by.off.photomap.model

data class TagInfo(val text: String, val type: Int) {
    companion object {
        const val TABLE = "PhotoTag"
        const val TAG_TITLE = "tagTitle"
        const val PHOTO_ID = "photo"

        const val TYPE_TAG = 1
        const val TYPE_HISTORY = 2
    }
}