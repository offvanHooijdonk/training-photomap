package by.off.photomap.model

import java.util.Date

data class PhotoInfo(
    val id: String,
    val author: UserInfo? = null,
    var description: String,
    val shotTimestamp: Date,
    var category: Int,
    var latitude: Double? = null,
    var longitude: Double? = null
) : DataObject {
    companion object {
        const val ID = "objectId"
        const val TABLE = "Photo"
        const val BIN_DATA = "binData"
        const val THUMBNAIL_DATA = "thumbnail_data"
        const val AUTHOR = "author"
        const val DESCRIPTION = "description"
        const val SHOT_TIMESTAMP = "shotTimestamp"
        const val CATEGORY = "category"
        const val LOCATION = "location"

        const val EMPTY_VALUE = "-"
    }
}