package by.off.photomap.core.ui

import android.content.Context
import by.off.photomap.model.CategoryInfo

val CategoryInfo.title: String
    get() = categoriesMap[this.label] ?: defaultTitle

val CategoryInfo.colorRes: Int
    get() = colorsMap[this.label] ?: R.color.label_category_default

private val categoriesMap = mutableMapOf<String, String>()
private val colorsMap = mutableMapOf<String, Int>()

private const val RESOURCE_TYPE_STRING = "string"
private const val RESOURCE_TYPE_COLOR = "color"
private const val INVALID_RES_ID = 0

fun initializeCategoriesViewProps(ctx: Context, categoriesLabels: List<String>) {
    categoriesMap.clear()
    val packageName = ctx.packageName//CategoryInfo::class.java.`package`?.name

    for (label in categoriesLabels) {
        val labelResId = ctx.resources.getIdentifier(label, RESOURCE_TYPE_STRING, packageName)
        val colorResId = ctx.resources.getIdentifier(label, RESOURCE_TYPE_COLOR, packageName)
        if (labelResId != INVALID_RES_ID) {
            val value = ctx.getString(labelResId) // TODO should catch exception?
            categoriesMap[label] = value
        }
        if (colorResId != INVALID_RES_ID) {
            //val value = ctx.resources.getColor(colorResId) // TODO should catch exception?
            colorsMap[label] = colorResId
        }
    }
}
