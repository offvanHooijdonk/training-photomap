package by.off.photomap.core.utils

import android.content.Context
import android.preference.PreferenceManager

object PrefHelper {
    private const val KEY_SEARCH_HISTORY = "key_search_history_list"
    private const val MAX_HISTORY = 5

    private const val HISTORY_SEPARATOR = "|"

    fun getSearchHistory(ctx: Context): List<String> =
        getPref(ctx).getString(KEY_SEARCH_HISTORY, "")?.let {
            if (it.isNotEmpty()) it.split(HISTORY_SEPARATOR) else emptyList()
        } ?: emptyList()


    fun addSearchHistoryEntry(ctx: Context, entry: String) {
        val historyList = getSearchHistory(ctx).toMutableList()
        if (!historyList.map { it.toLowerCase() }.contains(entry.toLowerCase())) { // todo if contains - still need to reorder items
            if (historyList.size >= MAX_HISTORY) {
                historyList.removeAt(historyList.size - 1)
            }
            historyList.add(0, entry)

            getPref(ctx).edit().putString(KEY_SEARCH_HISTORY, historyToString(historyList)).apply()
        }

    }

    private fun historyToString(list: List<String>) =
        list.reduceIndexed { index, acc, s -> acc + (if (index > 0) HISTORY_SEPARATOR else "") + s }

    private fun getPref(ctx: Context) = PreferenceManager.getDefaultSharedPreferences(ctx)
}