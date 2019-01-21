package by.off.photomap.model

import java.util.Date

data class PhotoInfo(
    val id: String,
    val author: UserInfo,
    val description: String?,
    val shotTimestamp: Date,
    val category: CategoryInfo,
    val latitude: Float,
    val longitude: Float
)