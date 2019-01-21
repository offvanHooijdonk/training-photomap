package by.off.photomap.storage.parse.impl

import by.off.photomap.model.CategoryInfo
import by.off.photomap.storage.parse.CategoryService
import com.parse.ParseObject
import com.parse.ParseQuery
import javax.inject.Inject

class CategoryServiceImpl @Inject constructor() : CategoryService {
    override suspend fun list(): Array<CategoryInfo> {
        val list = mutableListOf<CategoryInfo>()
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery(CategoryInfo.TABLE)

        // TODO handle exception
        val objects = query.find()

        for (obj in objects) {
            list.add(
                CategoryInfo(
                    obj.objectId,
                    obj.getString(CategoryInfo.PROP_LABEL) ?: CategoryInfo.DEFAULT_VALUE,
                    obj.getString(CategoryInfo.PROP_DEFAULT_TITLE) ?: CategoryInfo.DEFAULT_VALUE
                )
            )
        }
        return list.toTypedArray()
    }
}