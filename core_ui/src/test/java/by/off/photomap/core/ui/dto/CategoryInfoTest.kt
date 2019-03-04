package by.off.photomap.core.ui.dto

import by.off.photomap.core.ui.R
import org.junit.Assert.*
import org.junit.Test

class CategoryInfoTest {

    companion object {
        const val CATEGORIES_SIZE = 3
        const val INDEX_OUT = 100
    }

    @Test
    fun getDefault() {
        assertNotNull("Default value must not be null", CategoryInfo.getDefault())
    }

    @Test
    fun getAllIds() {
        assertEquals("Categories number must be $CATEGORIES_SIZE", CATEGORIES_SIZE, CategoryInfo.getAllIds().size)
    }

    @Test
    fun getTitlesOrdered() {
        val result = CategoryInfo.getTitlesOrdered()
        assertEquals("Categories number be $CATEGORIES_SIZE", CATEGORIES_SIZE, result.size)

        val titlesOrdered = CategoryInfo.getAllIds().sorted().map { CategoryInfo(it).labelRes }

        assertArrayEquals("Not all title resources are equal", titlesOrdered.toIntArray(), result.toIntArray())
    }

    @Test
    fun getMarkerColor() {
        val index = 2
        assertEquals(CategoryInfo(index).textColorRes, CategoryInfo.getMarkerColor(index))
    }

    @Test
    fun getMarkerColor_Default() {
        assertEquals("Default category color must be if index passed is out of bounds", R.color.category_default, CategoryInfo.getMarkerColor(INDEX_OUT))
    }
}