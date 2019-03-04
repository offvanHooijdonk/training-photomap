package by.off.photomap.core.utils

import org.junit.Test

import org.junit.Assert.*

class HelpersKtTest {

    @Test
    fun findHashTags_AllCases() {
        val input = "rtfi # notag #tag1 uhj #tag2dot. #tAg3Caps gigni hi #tag4end and #тэгtag4русс #42"
        val expected = arrayOf("tag1", "tag2dot", "tAg3Caps", "tag4end", "тэгtag4русс", "42")
        val resultList = findHashTags(input)
        assertArrayEquals("Hash tags extracted not as expected.", expected, resultList.toTypedArray())
    }

    @Test
    fun findHashTags_NoTags() {
        val input = "# gkg #. iui #- #"
        val expected = emptyArray<String>()
        val resultList = findHashTags(input)
        assertArrayEquals("Expected empty list of tags.", expected, resultList.toTypedArray())
    }

    @Test
    fun findHashTags_EmptyString() {
        val input = ""
        val expected = emptyArray<String>()
        val resultList = findHashTags(input)
        assertArrayEquals("Expected empty list and no error for empty string.", expected, resultList.toTypedArray())
    }
}