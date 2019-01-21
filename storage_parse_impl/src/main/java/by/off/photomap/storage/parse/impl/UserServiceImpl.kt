package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.UserService
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserServiceImpl @Inject constructor() : UserService {
    override fun register(user: UserInfo): UserInfo { // TODO
        return UserInfo("", "", "")
    }

    override fun getById(id: String): LiveData<UserInfo?> {
        val liveData = MutableLiveData<UserInfo?>()
        var user: UserInfo? = null
        CoroutineScope(Dispatchers.Main).launch {
            launch(Dispatchers.IO) {
                val query: ParseQuery<ParseObject> = ParseQuery.getQuery(UserInfo.TABLE)
                val obj = try {
                    query.get(id)
                } catch (th: Throwable) {
                    Log.e("PHOTOMAPAPP", "Error getting the user $id", th) // TODO throw exceptions
                    null
                }
                user = obj?.let { convert(obj) }
                liveData.postValue(user)
            }
        }
        return liveData
    }

    override fun authenticate(userName: String, pwd: String): LiveData<UserInfo?> {
        val liveData = MutableLiveData<UserInfo?>()
        CoroutineScope(Dispatchers.Main).launch {
            launch(Dispatchers.IO) {
                // todo need Dispatchers.Main ???
                val parseUser = try {
                    ParseUser.logIn(userName, pwd)
                } catch (e: Throwable) {
                    Log.e(LOGCAT, "Error logging in the user $userName", e)
                    // TODO introduce custom exceptions and throw
                    null
                }

                val user = when (parseUser) {
                    null -> null
                    else -> UserInfo(parseUser.objectId, parseUser.username, parseUser.email)
                }
                liveData.postValue(user)

            }
        }
        return liveData
    }

    private fun convert(obj: ParseObject): UserInfo =
        UserInfo(
            obj.objectId, // TODO customize exceptions & handle them
            obj.getString(UserInfo.PROP_EMAIL) ?: throw Exception("email empty"),
            obj.getString(UserInfo.PROP_USER_NAME) ?: throw Exception("user name empty")
        )
}