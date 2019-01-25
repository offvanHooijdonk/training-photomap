package by.off.photomap.storage.parse

class UserNotFoundException(val id: String) : Exception("User not found with the id $id")