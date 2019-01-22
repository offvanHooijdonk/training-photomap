package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.AuthenticationFailedException
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserNotFoundException
import by.off.photomap.storage.parse.UserService
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserServiceImpl @Inject constructor() : UserService {

    override fun logIn(): LiveData<Response<UserInfo>> {
        val parseUser: ParseUser? = ParseUser.getCurrentUser()

        return if (parseUser == null) {
            MutableLiveData<Response<UserInfo>>().apply {
                postValue(Response(null, null))
            }
        } else {
            getById(parseUser.objectId)
        }
    }

    override fun register(user: UserInfo): UserInfo { // TODO
        return UserInfo("", "", "")
    }

    override fun getById(id: String): LiveData<Response<UserInfo>> {
        val liveData = MutableLiveData<Response<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {
            val query: ParseQuery<ParseObject> = ParseQuery.getQuery(UserInfo.TABLE)
            val response = try {
                Response(convert(query.get(id)))
            } catch (e: ParseException) {
                val error = when {
                    e.code == ParseException.OBJECT_NOT_FOUND -> UserNotFoundException(id)
                    else -> e
                }
                Response<UserInfo>(error = error)
            }

            liveData.postValue(response)
        }
        return liveData
    }

    override fun authenticate(userName: String, pwd: String): LiveData<Response<UserInfo>> {
        val liveData = MutableLiveData<Response<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                val parseUser = ParseUser.logIn(userName, pwd)
                Response(UserInfo(parseUser.objectId, parseUser.username, parseUser.email))
            } catch (e: Exception) {
                Response<UserInfo>(error = AuthenticationFailedException(userName, e))
            }

            liveData.postValue(response)
        }
        return liveData
    }

    private fun convert(obj: ParseObject): UserInfo =
        UserInfo(
            obj.objectId, // TODO implement better?
            obj.getString(UserInfo.PROP_EMAIL) ?: UserInfo.ERROR_MISSING,
            obj.getString(UserInfo.PROP_USER_NAME) ?: UserInfo.ERROR_MISSING
        )
}