package by.off.photomap.storage.parse.impl

import android.util.Log
import by.off.photomap.model.CategoryInfo
import by.off.photomap.storage.parse.CategoryService
import com.parse.ParseObject
import com.parse.ParseQuery

class CategoryServiceImpl : CategoryService {
    override fun list(): Array<CategoryInfo> {
        val list = mutableListOf<CategoryInfo>()
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery(CategoryInfo.TABLE)
        query.findInBackground { objects, e ->
            if (e != null) {
                // TODO take measures
                Log.e("PHOTOMAPAPP", "Error getting categories!", e)
            }
            for (obj in objects) {
                list.add(
                    CategoryInfo(
                        obj.objectId,
                        obj.getString(CategoryInfo.PROP_LABEL) ?: CategoryInfo.DEFAULT_VALUE,
                        obj.getString(CategoryInfo.PROP_DEFAULT_TITLE) ?: CategoryInfo.DEFAULT_VALUE
                    )
                )
            }
        }

        return list.toTypedArray()
    }
}