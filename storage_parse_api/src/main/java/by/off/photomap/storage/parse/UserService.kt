package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import by.off.photomap.model.UserInfo

interface UserService {
    suspend fun authenticate(userName: String, pwd: String): LiveData<UserInfo?>

    fun register(user: UserInfo) : UserInfo

    fun getById(id: String) : UserInfo?
}