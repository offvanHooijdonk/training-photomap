package by.off.photomap.core.ui.dto

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import by.off.photomap.core.ui.R
import by.off.photomap.model.DataObject

data class CategoryInfo(val id: Int, @StringRes val labelRes: Int, @ColorRes val textColorRes: Int, @ColorRes val backColorRes: Int) : DataObject {
    constructor(id: Int) : this(
        id,
        categories[id]?.labelRes ?: R.string.label_category_default,
        categories[id]?.textColorRes ?: R.color.category_default,
        categories[id]?.backColorRes ?: R.color.category_default_back
    )

    companion object {
        private const val ID_DEFAULT = 0
        private const val ID_FRIENDS = 1
        private const val ID_NATURE = 2

        private val categories by lazy {
            mapOf(
                ID_DEFAULT to CategoryInfo(ID_DEFAULT, R.string.label_category_default, R.color.category_default, R.color.category_default_back),
                ID_FRIENDS to CategoryInfo(ID_FRIENDS, R.string.label_category_friends, R.color.category_friends, R.color.category_friends_back),
                ID_NATURE to CategoryInfo(ID_NATURE, R.string.label_category_nature, R.color.category_nature, R.color.category_nature_back)
            )
        }

        fun getDefault() = ID_DEFAULT

        fun getAllIds() = listOf(ID_DEFAULT, ID_FRIENDS, ID_NATURE)

        fun getTitlesOrdered(): List<Int> =
            categories.toSortedMap().mapTo(mutableListOf()) { entry -> entry.value.labelRes }

        fun getMarkerColor(id: Int): Int =
            categories[id]?.textColorRes ?: R.color.category_default
    }
}