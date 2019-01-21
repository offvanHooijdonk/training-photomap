package by.off.photomap.model

data class UserInfo(val id: String, val userName: String, val email: String) {
    companion object {
        const val TABLE = "_User"
        const val PROP_USER_NAME = "username"
        const val PROP_EMAIL = "email"
    }
}
