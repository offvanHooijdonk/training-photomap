package by.off.photomap.core.utils

import android.content.Context
import android.preference.PreferenceManager

private const val REGEX_HASH_TAG = "#[\\p{L}\\d]+"
private const val TAG_CUT_START = "#"

fun findHashTags(text: String): List<String> =
    REGEX_HASH_TAG.toRegex().findAll(text).toList().map { it.value.substring(it.value.indexOf(TAG_CUT_START) + 1) }.distinct()

fun Context.getPrefManager() = PreferenceManager.getDefaultSharedPreferences(this)