package by.off.photomap.model

data class UserInfo(val id: String, val userName: String = ERROR_MISSING, val email: String = ERROR_MISSING) : DataObject { // TODO use default values
    companion object {
        const val TABLE = "_User"
        const val PROP_USER_NAME = "username"
        const val PROP_EMAIL = "email"
        const val ERROR_MISSING = "!error!"
    }
}
