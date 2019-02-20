package by.off.photomap.storage.parse.impl

private const val REGEX_HASH_TAG = "#[\\p{L}\\d]+"

fun findHashTags(text: String): List<String> =
    REGEX_HASH_TAG.toRegex().findAll(text).toList().map { it.value.substring(it.value.indexOf("#") + 1) }