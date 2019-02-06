package by.off.photomap.core.ui

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    private const val FORMAT_MONTH_FULL = "MMMM"
    private const val FORMAT_YEAR_FULL = "yyyy"

    private val timelineMonthFormatter by lazy { SimpleDateFormat(FORMAT_MONTH_FULL) }
    private val timelineYearFormatter by lazy { SimpleDateFormat(FORMAT_YEAR_FULL) }
    private val dateFormatShort by lazy { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    private val dateFormatFull by lazy { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM) }

    fun formatTimelineDate(date: Date): String =
        "${timelineMonthFormatter.format(date)} ${timelineYearFormatter.format(date)}"

    fun formatDateShort(date: Date): String =
        dateFormatShort.format(date)

    fun formatDateFull(date: Date): String =
        dateFormatFull.format(date)

}