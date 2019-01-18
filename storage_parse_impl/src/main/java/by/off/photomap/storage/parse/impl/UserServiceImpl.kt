package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.UserService
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserServiceImpl : UserService {
    override fun register(user: UserInfo): UserInfo { // TODO
        return UserInfo("", "", "")
    }

    override fun getById(id: String): UserInfo? {
        var user: UserInfo? = null
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery(UserInfo.TABLE)
        query.getInBackground(id) { obj, e ->
            if (e != null) {
                Log.e("PHOTOMAPAPP", "Error getting the user $id", e)
            }
            user = convert(obj)
        }

        return user
    }

    override suspend fun authenticate(userName: String, pwd: String): LiveData<UserInfo?> {
        val liveData = MutableLiveData<UserInfo?>()
        coroutineScope {
            launch(Dispatchers.IO) {
                ParseUser.logInInBackground(userName, pwd) { parseUser, e ->
                    if (e != null) Log.e("PHOTOMAPAPP", "Error logging in the user $userName", e) // TODO handle
                    val user = when (parseUser) {
                        null -> null
                        else -> UserInfo(parseUser.objectId, parseUser.username, parseUser.email)
                    }
                    liveData.postValue(user)
                }
            }
        }//.await()
        return liveData
    }

    private fun convert(obj: ParseObject): UserInfo =
        UserInfo(
            obj.objectId, // TODO customize exceptions & handle them
            obj.getString(UserInfo.PROP_EMAIL) ?: throw Exception("email empty"),
            obj.getString(UserInfo.PROP_USER_NAME) ?: throw Exception("user name empty")
        )


}