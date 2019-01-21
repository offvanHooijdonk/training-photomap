package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import by.off.photomap.model.UserInfo

interface UserService {
    /**
     * @throws AuthenticationFailedException
     */
    fun authenticate(userName: String, pwd: String): LiveData<Response<UserInfo>>

    fun register(user: UserInfo): UserInfo

    /**
     * @throws UserNotFoundException
     */
    fun getById(id: String): LiveData<Response<UserInfo>>
}