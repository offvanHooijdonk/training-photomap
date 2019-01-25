package by.off.photomap.model

data class CategoryInfo(val id: String, val label: String, val defaultTitle: String) : DataObject {
    companion object {
        const val TABLE = "Category"
        const val PROP_LABEL = "label"
        const val PROP_DEFAULT_TITLE = "defaultTitle"
        const val DEFAULT_VALUE = "-"
    }
}