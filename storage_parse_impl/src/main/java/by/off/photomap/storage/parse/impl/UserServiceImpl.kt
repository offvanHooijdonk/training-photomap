package by.off.photomap.storage.parse.impl

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.*
import com.parse.*
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

    override fun registerAndLogin(user: UserInfo, pwd: String): LiveData<Response<UserInfo>> { // TODO
        val liveData = MutableLiveData<Response<UserInfo>>()

        CoroutineScope(Dispatchers.IO).launch {
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
                                Response<UserInfo>(error = RegistrationFailedException(RegistrationFailedException.Field.USER_NAME, user.userName))
                            e.code == ParseException.EMAIL_TAKEN ->
                                Response(error = RegistrationFailedException(RegistrationFailedException.Field.EMAIL, user.email))
                            else -> Response(error = e)
                        }
                    }
                    else -> Response(error = e)
                }
            }
            liveData.postValue(response)
        }

        return liveData
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
            val response = authSync(userName, pwd)

            liveData.postValue(response)
        }
        return liveData
    }

    private fun authSync(userName: String, pwd: String): Response<UserInfo> =
        try {
            val parseUser = ParseUser.logIn(userName, pwd)
            Response(UserInfo(parseUser.objectId, parseUser.username, parseUser.email))
        } catch (e: Exception) {
            Response(error = AuthenticationFailedException(userName, e))
        }

    private fun convert(obj: ParseObject): UserInfo =
        UserInfo(
            obj.objectId, // TODO implement better?
            obj.getString(UserInfo.PROP_EMAIL) ?: UserInfo.ERROR_MISSING,
            obj.getString(UserInfo.PROP_USER_NAME) ?: UserInfo.ERROR_MISSING
        )
}