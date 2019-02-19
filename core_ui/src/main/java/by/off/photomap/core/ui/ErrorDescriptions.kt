package by.off.photomap.core.ui

import android.content.Context
import by.off.photomap.storage.parse.exception.AuthenticationFailedException
import by.off.photomap.storage.parse.exception.RegistrationFailedException
import by.off.photomap.storage.parse.exception.UserNotFoundException

object ErrorDescriptions {
    fun getDescripitionRes(ctx: Context, e: Exception): String =
        when (e) {
            is AuthenticationFailedException -> ctx.getString(by.off.photomap.core.ui.R.string.error_auth_failed, e.userName)
            is UserNotFoundException -> ctx.getString(by.off.photomap.core.ui.R.string.error_user_not_found, e.id)
            is RegistrationFailedException ->
                when (e.fieldDuplicated) {
                    RegistrationFailedException.Field.EMAIL -> ctx.getString(by.off.photomap.core.ui.R.string.error_registration_failed_by_email, e.value)
                    RegistrationFailedException.Field.USER_NAME -> ctx.getString(by.off.photomap.core.ui.R.string.error_registration_failed_by_name, e.value)
                }
            else -> ctx.getString(by.off.photomap.core.ui.R.string.error_default_auth)
        }
}