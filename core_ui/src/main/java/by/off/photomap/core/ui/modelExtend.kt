package by.off.photomap.core.ui

import android.content.Context
import by.off.photomap.model.CategoryInfo

val CategoryInfo.title: String
    get() = categoriesMap[this.label] ?: defaultTitle

private val categoriesMap = mutableMapOf<String, String>()
private const val RESOURCE_TYPE_STRING = "string"
private const val INVALID_RES_ID = 0

fun initializeCategoriesTitles(ctx: Context, categoriesLabels: List<String>) {
    categoriesMap.clear()
    val packageName = CategoryInfo::class.java.`package`?.name
    for (label in categoriesLabels) {
        val resId = ctx.resources.getIdentifier(label, RESOURCE_TYPE_STRING, packageName)
        if (resId != INVALID_RES_ID) {
            val value = ctx.getString(resId) // TODO should catch exception?
            categoriesMap[label] = value
        }
    }
}
