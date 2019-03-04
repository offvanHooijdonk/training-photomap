package by.off.photomap.core.ui

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateHelperTest {
    companion object {
        private val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        private val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2018)
            set(Calendar.MONTH, Calendar.MAY)
            set(Calendar.DAY_OF_MONTH, 25)
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 45)
        }!!
    }

    @Test
    fun formatTimelineDate() {
        val result = DateHelper.formatTimelineDate(calendar.time, months)
        assertEquals("${months[4]} 2018", result)
    }

    @Test
    fun formatDateShort() {
        val result = DateHelper.formatDateShort(calendar.time)
        assertEquals("May 25, 2018", result)
    }

    @Test
    fun formatDateFull() {
        val result = DateHelper.formatDateFull(calendar.time)
        assertEquals("May 25, 2018 4:45 PM", result)
    }
}