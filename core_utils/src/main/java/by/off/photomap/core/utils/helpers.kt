package by.off.photomap.core.utils

import android.content.Context
import android.preference.PreferenceManager

private const val REGEX_HASH_TAG = "#[\\p{L}\\d]+"

fun findHashTags(text: String): List<String> =
    REGEX_HASH_TAG.toRegex().findAll(text).toList().map { it.value.substring(it.value.indexOf("#") + 1) }

fun Context.getPrefManager() = PreferenceManager.getDefaultSharedPreferences(this)