package by.off.photomap.core.ui

import android.content.Context
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    private const val FORMAT_MONTH_FULL = "MMMM"
    private const val FORMAT_YEAR_FULL = "yyyy"

    //    private val timelineMonthFormatter by lazy { SimpleDateFormat(FORMAT_MONTH_FULL) }
    //private val timelineYearFormatter by lazy { SimpleDateFormat(FORMAT_YEAR_FULL) }
    private val dateFormatShort by lazy { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    private val dateFormatFull by lazy { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM) }

    fun formatTimelineDate(date: Date, ctx: Context): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val monthName = ctx.resources.getStringArray(R.array.months_full)[calendar.get(Calendar.MONTH)]
        val yearString = calendar.get(Calendar.YEAR).toString()
        return "$monthName $yearString"
    }

    fun formatDateShort(date: Date): String =
        dateFormatShort.format(date)

    fun formatDateFull(date: Date): String =
        dateFormatFull.format(date)

}