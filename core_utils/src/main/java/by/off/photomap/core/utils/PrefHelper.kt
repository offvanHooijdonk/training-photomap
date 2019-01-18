package by.off.photomap.core.utils

import android.content.Context
import android.preference.PreferenceManager

class PrefHelper(private val context: Context) {
    companion object {
        private const val KEY_USER_ID = "key_user_id"
    }

    var loggedUserId: String?
        get() = getPref().getString(KEY_USER_ID, null)
        set(id) = getPref().edit().putString(KEY_USER_ID, id).apply()

    private fun getPref() = PreferenceManager.getDefaultSharedPreferences(context)
}