package by.off.photomap.storage.parse.exception

class RegistrationFailedException(val fieldDuplicated: Field, val value: String) :
    Exception("Registration failed, $fieldDuplicated '$value' is already in use") {

    enum class Field {
        USER_NAME, EMAIL
    }
}