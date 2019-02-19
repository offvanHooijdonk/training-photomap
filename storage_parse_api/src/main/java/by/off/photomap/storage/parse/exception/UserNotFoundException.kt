package by.off.photomap.storage.parse.exception

class UserNotFoundException(val id: String) : Exception("User not found with the id $id")