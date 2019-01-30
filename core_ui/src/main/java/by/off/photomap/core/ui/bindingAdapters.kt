package by.off.photomap.core.ui

import android.databinding.BindingAdapter
import android.widget.Spinner
import android.widget.TextView
import by.off.photomap.storage.parse.AuthenticationFailedException
import by.off.photomap.storage.parse.RegistrationFailedException
import by.off.photomap.storage.parse.RegistrationFailedException.Field
import by.off.photomap.storage.parse.UserNotFoundException
import java.text.DateFormat
import java.util.*

@BindingAdapter("enabled")
fun setSpinnerEnabled(spinner: Spinner, enabledFlag: Boolean) {
    spinner.isEnabled = enabledFlag
}
