package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.*
import by.off.photomap.storage.parse.RegistrationFailedException.Field.EMAIL
import by.off.photomap.storage.parse.RegistrationFailedException.Field.USER_NAME
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO log errors before returning response
class UserServiceImpl @Inject constructor() : UserService {
    override val serviceLiveData: LiveData<Response<UserInfo>>
        get() = liveData

    private val liveData = MutableLiveData<Response<UserInfo>>()

    override fun logIn()/*: LiveData<Response<UserInfo>>*/ {
        launchScopeIO {
            val parseUser: ParseUser? = ParseUser.getCurrentUser()
            val response = if (parseUser == null) {
                Response(null, null)
            } else {
                getByIdSync(parseUser.objectId)
            }
            liveData.postValue(response)
        }
    }

    override fun registerAndLogin(user: UserInfo, pwd: String) {
        launchScopeIO {
            val parseUser = ParseUser()
            parseUser.username = user.userName
            parseUser.email = user.email
            parseUser.setPassword(pwd)

            val response = try {
                parseUser.signUp()
                authSync(user.userName, pwd)
            } catch (e: Exception) {
                when (e) {
                    is ParseException -> {
                        when {
                            e.code == ParseException.USERNAME_TAKEN ->
                                Response<UserInfo>(error = RegistrationFailedException(USER_NAME, user.userName))
                            e.code == ParseException.EMAIL_TAKEN ->
                                Response(error = RegistrationFailedException(EMAIL, user.email))
                            else -> Response(error = e)
                        }
                    }
                    else -> Response(error = e)
                }
            }
            liveData.postValue(response)
        }
    }

    override fun getById(id: String): LiveData<Response<UserInfo>> {
        val liveData = MutableLiveData<Response<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {
            val response = getByIdSync(id)

            liveData.postValue(response)
        }
        return liveData
    }

    override fun authenticate(userName: String, pwd: String) {
        launchScopeIO {
            val response = authSync(userName, pwd)

            liveData.postValue(response)
        }
    }

    // TODO move to a separate UserParseService ?
    private fun authSync(userName: String, pwd: String): Response<UserInfo> =
        try {
            val parseUser = ParseUser.logIn(userName, pwd)
            Response(UserInfo(parseUser.objectId, parseUser.username, parseUser.email))
        } catch (e: Exception) {
            Response(error = AuthenticationFailedException(userName, e))
        }

    private fun getByIdSync(id: String) =
        try {
            val query: ParseQuery<ParseObject> = ParseQuery.getQuery(UserInfo.TABLE)
            Response(convert(query.get(id)), null)
        } catch (e: ParseException) {
            val error = when {
                e.code == ParseException.OBJECT_NOT_FOUND -> UserNotFoundException(id)
                else -> e
            }
            Response<UserInfo>(null, error)
        }

    private fun convert(obj: ParseObject): UserInfo =
        UserInfo(
            obj.objectId, // TODO implement better?
            obj.getString(UserInfo.PROP_EMAIL) ?: UserInfo.ERROR_MISSING,
            obj.getString(UserInfo.PROP_USER_NAME) ?: UserInfo.ERROR_MISSING
        )
}