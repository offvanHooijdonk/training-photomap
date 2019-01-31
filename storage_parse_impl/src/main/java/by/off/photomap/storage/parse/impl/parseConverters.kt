package by.off.photomap.storage.parse.impl

import by.off.photomap.model.PhotoInfo
import by.off.photomap.model.UserInfo
import com.parse.ParseObject
import com.parse.ParseUser

fun convertToPhoto(parse: ParseObject, userInfo: UserInfo): PhotoInfo =
    PhotoInfo(
        parse.objectId,
        userInfo,
        parse.getString(PhotoInfo.DESCRIPTION) ?: PhotoInfo.EMPTY_VALUE,
        parse.getDate(PhotoInfo.SHOT_TIMESTAMP)!!,
        parse.getInt(PhotoInfo.CATEGORY),
        parse.getParseGeoPoint(PhotoInfo.LOCATION)?.latitude,
        parse.getParseGeoPoint(PhotoInfo.LOCATION)?.longitude
    )

fun convertToUser(parseUser: ParseUser): UserInfo =
    UserInfo(
        parseUser.objectId,
        parseUser.username,
        parseUser.email
    )

fun convertToUser(obj: ParseObject): UserInfo =
    UserInfo(
        obj.objectId,
        obj.getString(UserInfo.PROP_EMAIL) ?: UserInfo.ERROR_MISSING,
        obj.getString(UserInfo.PROP_USER_NAME) ?: UserInfo.ERROR_MISSING
    )