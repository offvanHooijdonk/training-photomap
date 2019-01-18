package by.off.photomap.storage.parse

import by.off.photomap.model.CategoryInfo

interface CategoryService {
    fun list(): Array<CategoryInfo>
}