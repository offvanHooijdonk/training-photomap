package by.off.photomap.core.utils

import android.content.Context
import android.preference.PreferenceManager

object PrefHelper {
    private const val KEY_SEARCH_HISTORY = "key_search_history"
    private const val MAX_HISTORY = 5

    fun getSearchHistory(ctx: Context): Set<String> =
        getPref(ctx).getStringSet(KEY_SEARCH_HISTORY, emptySet()) ?: emptySet()

    fun addSearchHistoryEntry(ctx: Context, entry: String) {
        val historySet = mutableSetOf<String>().apply { addAll(getSearchHistory(ctx)) }
        historySet.add(entry)
        if (historySet.size > MAX_HISTORY) {
            historySet.remove(historySet.first()) // todo how do I replace the oldest one ?
        }
        getPref(ctx).edit().putStringSet(KEY_SEARCH_HISTORY, historySet).apply()
    }

    private fun getPref(ctx: Context) = PreferenceManager.getDefaultSharedPreferences(ctx)
}