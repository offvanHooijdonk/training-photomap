package by.off.photomap.storage.parse.exception

class AuthenticationFailedException(val userName: String, cause: Exception) : Exception("Authentication failed for user '$userName'", cause)