package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import by.off.photomap.model.UserInfo

interface UserService {
    /**
     * @throws UserNotFoundException
     * @return Response - user is null if no one logged in
     */
    fun logIn(): LiveData<Response<UserInfo>>

    /**
     * @throws AuthenticationFailedException
     */
    fun authenticate(userName: String, pwd: String): LiveData<Response<UserInfo>>

    /**
     * @return
     */
    fun registerAndLogin(user: UserInfo, pwd: String): LiveData<Response<UserInfo>>

    /**
     * @throws UserNotFoundException
     */
    fun getById(id: String): LiveData<Response<UserInfo>>
}