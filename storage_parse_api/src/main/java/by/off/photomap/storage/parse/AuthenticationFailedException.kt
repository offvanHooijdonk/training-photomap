package by.off.photomap.storage.parse

class AuthenticationFailedException(val userName: String, cause: Exception) : Exception("Authentication failed for user '$userName'", cause)