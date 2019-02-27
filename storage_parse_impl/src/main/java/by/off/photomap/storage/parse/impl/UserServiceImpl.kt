package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import by.off.photomap.core.utils.di.scopes.PerFeature
import by.off.photomap.core.utils.launchScopeIO
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.*
import by.off.photomap.storage.parse.exception.AuthenticationFailedException
import by.off.photomap.storage.parse.exception.RegistrationFailedException
import by.off.photomap.storage.parse.exception.RegistrationFailedException.Field.EMAIL
import by.off.photomap.storage.parse.exception.RegistrationFailedException.Field.USER_NAME
import by.off.photomap.storage.parse.exception.UserNotFoundException
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import javax.inject.Inject

@PerFeature
class UserServiceImpl @Inject constructor() : UserService {
    override val serviceLiveData: LiveData<Response<UserInfo>>
        get() = liveData
    override val logoutLiveData: LiveData<Response<UserInfo>>
        get() = logoutLD

    private val liveData = MutableLiveData<Response<UserInfo>>()
    private val logoutLD = MutableLiveData<Response<UserInfo>>()

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

    override fun authenticate(userName: String, pwd: String) {
        launchScopeIO {
            val response = authSync(userName, pwd)

            liveData.postValue(response)
        }
    }

    override fun logOut() {
        launchScopeIO {
            val response = try {
                ParseUser.logOut()
                Response(UserInfo(""))
            } catch (e: Exception) {
                Response<UserInfo>(error = e)
            }

            logoutLD.postValue(response)
        }
    }

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
            Response(convertToUser(query.get(id)), null)
        } catch (e: ParseException) {
            val error = when {
                e.code == ParseException.OBJECT_NOT_FOUND -> UserNotFoundException(id)
                else -> e
            }
            Response<UserInfo>(null, error)
        }
}