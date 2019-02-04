package by.off.photomap.model

data class UserInfo(val id: String, val userName: String = DATA_MISSING, val email: String = DATA_MISSING) : DataObject {
    companion object {
        const val TABLE = "_User"
        const val PROP_USER_NAME = "username"
        const val PROP_EMAIL = "email"
        const val DATA_MISSING = "-"
    }
}
