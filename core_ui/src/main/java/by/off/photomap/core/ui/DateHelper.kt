package by.off.photomap.core.ui

import java.text.DateFormat
import java.util.*

object DateHelper {
    private val dateFormatShort by lazy { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    private val dateFormatFull by lazy { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT) }

    fun formatTimelineDate(date: Date, monthsList: Array<String>): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val monthName = monthsList[calendar.get(Calendar.MONTH)]
        val yearString = calendar.get(Calendar.YEAR).toString()
        return "$monthName $yearString"
    }

    fun formatDateShort(date: Date): String =
        dateFormatShort.format(date)

    fun formatDateFull(date: Date): String =
        dateFormatFull.format(date)

}