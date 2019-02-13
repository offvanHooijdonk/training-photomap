package by.off.photomap.storage.parse

import android.arch.lifecycle.LiveData
import by.off.photomap.model.UserInfo

interface UserService {
    val serviceLiveData: LiveData<Response<UserInfo>>
    val logoutLiveData: LiveData<Response<UserInfo>>

    /**
     * Works with [serviceLiveData]. Posts <code>null</code> if nobody is logged in
     * @exception UserNotFoundException Produced when the locally logged user is not found
     */
    fun logIn()

    /**
     * Works with [serviceLiveData].
     * @exception AuthenticationFailedException Produced when no user with such credentials found
     */
    fun authenticate(userName: String, pwd: String)

    /**
     * Works with [serviceLiveData]. Creates a user with the data provided and logs the user in
     * @exception RegistrationFailedException Produced if the name/email provided already taken
     */
    fun registerAndLogin(user: UserInfo, pwd: String)

    fun logOut()
}