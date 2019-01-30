package by.off.photomap.model

import java.util.Date

data class PhotoInfo(
    val id: String,
    val author: UserInfo? = null,
    var description: String? = null,
    val shotTimestamp: Date,
    var category: Int,
    val latitude: Double? = null,
    val longitude: Double? = null
) : DataObject {
    companion object {
        const val TABLE = "Photo"
        const val BIN_DATA = "binData"
        const val AUTHOR = "author"
        const val DESCRIPTION = "description"
        const val SHOT_TIMESTAMP = "shotTimestamp"
        const val CATEGORY = "category"
        const val LOCATION = "location"
    }
}