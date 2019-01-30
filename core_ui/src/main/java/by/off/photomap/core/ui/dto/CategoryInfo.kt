package by.off.photomap.core.ui.dto

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import by.off.photomap.core.ui.R
import by.off.photomap.model.DataObject

data class CategoryInfo(val id: Int, @StringRes val labelRes: Int, @ColorRes val colorRes: Int) : DataObject {
    companion object {
        const val ID_DEAFULT = 0
        const val ID_FRIENDS = 1
        const val ID_NATURE = 2
        val categories = mapOf(
            ID_DEAFULT to CategoryInfo(ID_DEAFULT, R.string.label_category_default, R.color.category_default),
            ID_FRIENDS to CategoryInfo(ID_FRIENDS, R.string.label_category_friends, R.color.category_friends),
            ID_NATURE to CategoryInfo(ID_NATURE, R.string.label_category_nature, R.color.category_nature)
        )

        fun getTitlesOrdered(ctx: Context): List<String> =
            categories.toSortedMap().mapTo(mutableListOf<String>()) { entry -> ctx.getString(entry.value.labelRes) }

        fun getTitleRes(id: Int) = categories[id]?.labelRes
    }
}